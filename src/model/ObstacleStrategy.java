package model;

/**
 * Estrategia para el comportamiento de diferentes tipos de obstáculos.
 * Patrón Strategy para manejar obstáculos dinámicos vs estáticos.
 */
public interface ObstacleStrategy {
    /**
     * Actualiza el estado del obstáculo. Se llama en cada tick.
     * @param room La habitación
     * @param currentPosition Posición actual del obstáculo
     * @param robotMoveCount Número de movimientos del robot
     * @return Nueva posición del obstáculo (null si no se mueve)
     */
    Position update(Room room, Position currentPosition, int robotMoveCount);

    /**
     * Indica si este obstáculo ensucia las celdas.
     */
    boolean dirtiesCell();

    /**
     * Obtiene el tipo de obstáculo para visualización.
     */
    ObstacleType getType();
}

