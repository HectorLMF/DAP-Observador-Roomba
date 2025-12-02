package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa la habitación como una rejilla de celdas.
 */
public class Room {
    private final int width;
    private final int height;
    private final Cell[][] cells;
    private final List<DynamicObstacle> dynamicObstacles = new ArrayList<>();

    public Room(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new Cell[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                this.cells[x][y] = new Cell();
                // Todas las celdas empiezan sucias (no limpiadas)
                this.cells[x][y].setCleaned(false);
            }
        }
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public boolean inBounds(Position p) {
        return p.x >= 0 && p.x < width && p.y >= 0 && p.y < height;
    }

    public Cell getCell(Position p) {
        if (!inBounds(p)) return null;
        return cells[p.x][p.y];
    }

    public boolean isObstacle(Position p) {
        Cell c = getCell(p);
        return c != null && c.isObstacle();
    }

    public void setObstacle(Position p, boolean obs) {
        Cell c = getCell(p);
        if (c != null) c.setObstacle(obs);
    }

    public boolean isCleaned(Position p) {
        Cell c = getCell(p);
        return c != null && c.isCleaned();
    }

    public void setCleaned(Position p, boolean cleaned) {
        Cell c = getCell(p);
        if (c != null) c.setCleaned(cleaned);
    }

    public void setCharger(Position p, boolean charger) {
        Cell c = getCell(p);
        if (c != null) c.setCharger(charger);
    }

    public boolean hasCharger(Position p) {
        Cell c = getCell(p);
        return c != null && c.hasCharger();
    }

    public List<Position> getNeighbors(Position p) {
        if (!inBounds(p)) return Collections.emptyList();
        List<Position> res = new ArrayList<>();
        int[][] deltas = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] d : deltas) {
            Position np = new Position(p.x + d[0], p.y + d[1]);
            // El cargador es transitable, solo los obstáculos fijos bloquean
            boolean isBlocked = isObstacle(np) && !hasChargerAt(np);
            if (inBounds(np) && !isBlocked) {
                res.add(np);
            }
        }
        return res;
    }

    public List<Position> getAllCleanablePositions() {
        List<Position> res = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Position p = new Position(x,y);
                if (!isObstacle(p)) res.add(p);
            }
        }
        return res;
    }

    /**
     * Obtiene todas las posiciones que aún no han sido limpiadas.
     */
    public List<Position> getUncleanedPositions() {
        List<Position> res = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Position p = new Position(x, y);
                if (!isObstacle(p) && !isCleaned(p)) {
                    res.add(p);
                }
            }
        }
        return res;
    }

    /**
     * Verifica si todas las celdas limpiables han sido limpiadas.
     */
    public boolean isFullyCleaned() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Position p = new Position(x, y);
                if (!isObstacle(p) && !isCleaned(p)) {
                    return false;
                }
            }
        }
        return true;
    }

    // Métodos para obstáculos dinámicos

    /**
     * Añade un obstáculo dinámico (ej: gato).
     */
    public void addDynamicObstacle(DynamicObstacle obstacle) {
        dynamicObstacles.add(obstacle);
        setObstacle(obstacle.getPosition(), true);
    }

    /**
     * Elimina un obstáculo dinámico en una posición.
     */
    public void removeDynamicObstacleAt(Position pos) {
        dynamicObstacles.removeIf(obs -> obs.getPosition().equals(pos));
        setObstacle(pos, false);
    }

    /**
     * Obtiene el obstáculo dinámico en una posición.
     */
    public DynamicObstacle getDynamicObstacleAt(Position pos) {
        for (DynamicObstacle obs : dynamicObstacles) {
            if (obs.getPosition().equals(pos)) {
                return obs;
            }
        }
        return null;
    }

    /**
     * Actualiza todos los obstáculos dinámicos.
     */
    public void updateDynamicObstacles(int robotMoveCount) {
        for (DynamicObstacle obstacle : dynamicObstacles) {
            Position oldPos = obstacle.getPosition();
            boolean moved = obstacle.update(this, robotMoveCount);

            if (moved) {
                Position newPos = obstacle.getPosition();
                // Quitar obstáculo de la posición anterior
                setObstacle(oldPos, false);
                // Poner obstáculo en la nueva posición
                setObstacle(newPos, true);
            }
        }
    }

    /**
     * Obtiene todos los obstáculos dinámicos.
     */
    public List<DynamicObstacle> getDynamicObstacles() {
        return new ArrayList<>(dynamicObstacles);
    }

    /**
     * Verifica si hay un gato en una posición.
     */
    public boolean hasCatAt(Position pos) {
        DynamicObstacle obs = getDynamicObstacleAt(pos);
        return obs != null && obs.getType() == ObstacleType.CAT;
    }

    /**
     * Verifica si hay un cargador en una posición.
     */
    public boolean hasChargerAt(Position pos) {
        DynamicObstacle obs = getDynamicObstacleAt(pos);
        return obs != null && obs.getType() == ObstacleType.CHARGER;
    }

    /**
     * Obtiene la posición del cargador.
     */
    public Position getChargerPosition() {
        for (DynamicObstacle obs : dynamicObstacles) {
            if (obs.getType() == ObstacleType.CHARGER) {
                return obs.getPosition();
            }
        }
        return null;
    }
}

