package model;

/**
 * Estrategia para obstáculos fijos (paredes, muebles).
 */
public class FixedObstacleStrategy implements ObstacleStrategy {

    @Override
    public Position update(Room room, Position currentPosition, int robotMoveCount) {
        // Los obstáculos fijos no se mueven
        return null;
    }

    @Override
    public boolean dirtiesCell() {
        return false;
    }

    @Override
    public ObstacleType getType() {
        return ObstacleType.FIXED;
    }
}

