package observer;

import model.Position;
import model.Room;
import robot.Robot;
import robot.RobotManager;
import sensors.SimulatedProximitySensor;

/**
 * Clase de demostración del patrón Observer aplicado al Roomba.
 * Muestra cómo los diferentes observadores reaccionan a los eventos del robot.
 */
public class ObserverDemo {

    public static void main(String[] args) {
        System.out.println("=== Demostración del Patrón Observer - Roomba ===\n");

        // Crear una habitación de 10x10
        Room room = new Room(10, 10);

        // Agregar algunos obstáculos
        room.setObstacle(new Position(3, 3), true);
        room.setObstacle(new Position(3, 4), true);
        room.setObstacle(new Position(3, 5), true);
        room.setObstacle(new Position(7, 7), true);

        // Crear el robot en la posición inicial
        Position startPos = new Position(0, 0);
        Robot robot = new Robot(room, startPos);

        // Crear observadores del robot
        RobotEventLogger logger = new RobotEventLogger(true);
        RobotStatisticsObserver stats = new RobotStatisticsObserver();
        RobotAlertObserver alerts = new RobotAlertObserver();

        // Registrar observadores
        robot.addRobotObserver(logger);
        robot.addRobotObserver(stats);
        robot.addRobotObserver(alerts);

        System.out.println("Observadores registrados:");
        System.out.println("  - RobotEventLogger (registra todos los eventos)");
        System.out.println("  - RobotStatisticsObserver (recopila estadísticas)");
        System.out.println("  - RobotAlertObserver (muestra alertas críticas)");
        System.out.println("Total de observadores: " + robot.getRobotObserverCount());
        System.out.println("\n--- Iniciando simulación ---\n");

        // Simular algunos eventos del robot

        // 1. Cambiar estado a limpieza
        System.out.println("\n[Acción] Cambiando a estado de limpieza...");
        robot.setState(new robot.state.CleaningState());

        // 2. Mover el robot
        System.out.println("\n[Acción] Moviendo robot a nuevas posiciones...");
        robot.setCurrent(new Position(1, 0));
        robot.setCurrent(new Position(2, 0));
        robot.setCurrent(new Position(2, 1));

        // 3. Calcular una ruta
        System.out.println("\n[Acción] Calculando ruta a (5, 5)...");
        robot.recalculatePathTo(new Position(5, 5));

        // 4. Simular detección de obstáculo
        System.out.println("\n[Acción] Robot detecta obstáculo en su camino...");
        robot.setState(new robot.state.RecalculatingState());

        // 5. Simular batería baja
        System.out.println("\n[Acción] Nivel de batería bajo...");
        robot.notifyBatteryLow();

        // 6. Retornar al cargador
        System.out.println("\n[Acción] Robot regresa al cargador...");
        robot.setCharger(new Position(0, 0));
        robot.notifyReturnedToCharger();

        // 7. Completar limpieza
        System.out.println("\n[Acción] Limpieza completada...");
        robot.notifyCleaningCompleted();

        // Mostrar resumen de estadísticas
        System.out.println("\n\n=== RESUMEN DE LA SIMULACIÓN ===\n");
        stats.printStatistics();

        System.out.println("\n");
        logger.printSummary();

        System.out.println("\n=== Alertas generadas: " + alerts.getAlertCount() + " ===");

        // Demostrar eliminación de observador
        System.out.println("\n--- Eliminando RobotEventLogger ---");
        robot.removeRobotObserver(logger);
        System.out.println("Observadores restantes: " + robot.getRobotObserverCount());

        // Generar más eventos sin el logger
        System.out.println("\n[Acción] Movimientos adicionales sin logger...");
        robot.setCurrent(new Position(1, 1));
        robot.setCurrent(new Position(2, 2));

        System.out.println("\n");
        stats.printStatistics();

        System.out.println("\n=== Demostración completada ===");
    }
}

