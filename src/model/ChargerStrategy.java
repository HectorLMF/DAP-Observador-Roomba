package model;

/**
 * Estrategia para el cargador (obstáculo especial que no bloquea pero carga).
 * Usa patrón Strategy.
 */
public class ChargerStrategy implements ObstacleStrategy {

    @Override
    public Position update(Room room, Position currentPosition, int robotMoveCount) {
        // El cargador no se mueve
        return null;
    }

    @Override
    public boolean dirtiesCell() {
        return false;
    }

    @Override
    public ObstacleType getType() {
        return ObstacleType.CHARGER;
    }
}

