package model;

/**
 * Representa un obstáculo dinámico en la habitación.
 * Usa el patrón Strategy para diferentes comportamientos.
 */
public class DynamicObstacle {
    private Position position;
    private final ObstacleStrategy strategy;

    public DynamicObstacle(Position position, ObstacleStrategy strategy) {
        this.position = position;
        this.strategy = strategy;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public ObstacleStrategy getStrategy() {
        return strategy;
    }

    public ObstacleType getType() {
        return strategy.getType();
    }

    /**
     * Actualiza el obstáculo. Retorna true si se movió.
     */
    public boolean update(Room room, int robotMoveCount) {
        Position newPos = strategy.update(room, position, robotMoveCount);
        if (newPos != null) {
            this.position = newPos;
            return true;
        }
        return false;
    }
}

