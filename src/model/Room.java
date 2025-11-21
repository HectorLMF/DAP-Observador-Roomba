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

    public Room(int width, int height) {
        this.width = width;
        this.height = height;
        this.cells = new Cell[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                this.cells[x][y] = new Cell();
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
            if (inBounds(np) && !isObstacle(np)) res.add(np);
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
}

