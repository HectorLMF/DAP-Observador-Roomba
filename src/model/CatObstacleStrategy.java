package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Estrategia para el gato que se mueve cada 3 movimientos del robot
 * y ensucia la celda donde estaba.
 */
public class CatObstacleStrategy implements ObstacleStrategy {
    private static final int MOVES_TO_TRIGGER = 3;
    private final Random random = new Random();
    private int lastMoveCount = 0;

    @Override
    public Position update(Room room, Position currentPosition, int robotMoveCount) {
        // El gato se mueve cada 3 movimientos del robot
        if (robotMoveCount > 0 && robotMoveCount % MOVES_TO_TRIGGER == 0 && robotMoveCount != lastMoveCount) {
            lastMoveCount = robotMoveCount;

            // Obtener vecinos válidos (no obstáculos)
            List<Position> validNeighbors = new ArrayList<>();
            int[][] deltas = {{1,0}, {-1,0}, {0,1}, {0,-1}};

            for (int[] d : deltas) {
                Position newPos = new Position(currentPosition.x + d[0], currentPosition.y + d[1]);
                if (room.inBounds(newPos) && !room.isObstacle(newPos)) {
                    validNeighbors.add(newPos);
                }
            }

            // Si hay vecinos válidos, moverse aleatoriamente a uno
            if (!validNeighbors.isEmpty()) {
                Position newPosition = validNeighbors.get(random.nextInt(validNeighbors.size()));

                // Ensuciar la celda donde estaba el gato
                room.setCleaned(currentPosition, false);

                System.out.println("🐱 ¡El gato se movió de " + currentPosition + " a " + newPosition + " y ensució!");

                return newPosition;
            }
        }

        return null; // No se mueve este tick
    }

    @Override
    public boolean dirtiesCell() {
        return true;
    }

    @Override
    public ObstacleType getType() {
        return ObstacleType.CAT;
    }
}

