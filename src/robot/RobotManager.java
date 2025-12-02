package robot;

import model.Position;
import model.Room;
import sensors.SimulatedProximitySensor;
import sensors.SensorReading;
import sensors.BatterySensor;
import sensors.BatteryReading;
import pathfinding.AStar;

import java.util.ArrayList;
import java.util.List;

/**
 * Coordina la simulación: sensores, robot y tick.
 */
public class RobotManager {
    private final Room room;
    private final Robot robot;
    private final SimulatedProximitySensor front;
    private final SimulatedProximitySensor left;
    private final SimulatedProximitySensor right;
    private final BatterySensor batterySensor;

    private sensors.SensorReading frontReading = null;
    private sensors.SensorReading leftReading = null;
    private sensors.SensorReading rightReading = null;
    private BatteryReading batteryReading = null;

    private final List<Position> inaccessible = new ArrayList<>();
    private int robotMoveCount = 0;

    public RobotManager(Room room, Position start) {
        this(room, start, 100); // Batería por defecto: 100
    }

    public RobotManager(Room room, Position start, int maxBattery) {
        this.room = room;
        this.robot = new Robot(room, start, maxBattery);
        this.front = new SimulatedProximitySensor(SensorReading.Type.FRONT, 3);
        this.left = new SimulatedProximitySensor(SensorReading.Type.LEFT, 3);
        this.right = new SimulatedProximitySensor(SensorReading.Type.RIGHT, 3);
        this.batterySensor = new BatterySensor(maxBattery);

        // register robot as observer
        front.register(robot);
        left.register(robot);
        right.register(robot);
        batterySensor.register(robot);
    }

    public Robot getRobot() { return robot; }
    public Room getRoom() { return room; }
    public int getRobotMoveCount() { return robotMoveCount; }

    public void tick() {
        // Guardar posición anterior para detectar movimiento
        Position oldPos = robot.getCurrent();

        // Obtener nivel actual de batería del robot
        int currentBatteryLevel = robot.getBatteryLevel();

        // Chequear batería ANTES de hacer cualquier cosa
        // Usa A* para calcular distancia REAL al cargador
        batteryReading = batterySensor.checkBattery(
            robot.getCurrent(),
            robot.getCharger(),
            currentBatteryLevel,
            room  // Pasar room para cálculo con A*
        );

        // sensores escanean desde la posicion actual
        frontReading = front.scan(room, robot.getCurrent());
        leftReading = left.scan(room, robot.getCurrent());
        rightReading = right.scan(room, robot.getCurrent());

        // Gestionar estados del robot (solo si no está cargando o volviendo)
        if (!(robot.getCurrentState() instanceof robot.state.ReturningState) &&
            !(robot.getCurrentState() instanceof robot.state.ChargingState)) {

            if (robot.getCurrentState() == null) {
                // Iniciar en estado de limpieza
                robot.setState(new robot.state.CleaningState());
                selectNextTarget();
            } else if (robot.getCurrentState() instanceof robot.state.CleaningState) {
                // Si no tiene ruta, buscar siguiente objetivo
                if (!robot.hasPath()) {
                    selectNextTarget();
                }
            } else if (robot.getCurrentState() instanceof robot.state.RecalculatingState) {
                // Recalcular ruta al mismo objetivo o seleccionar uno nuevo
                selectNextTarget();
                if (robot.hasPath()) {
                    robot.setState(new robot.state.CleaningState());
                }
            }
        }

        // luego robot actua según estado
        robot.tick();

        // Verificar si el robot se movió
        Position newPos = robot.getCurrent();
        if (!oldPos.equals(newPos)) {
            robotMoveCount++;
        }

        // Actualizar obstáculos dinámicos (gatos)
        room.updateDynamicObstacles(robotMoveCount);
    }

    public sensors.SensorReading getFrontReading() { return frontReading; }
    public sensors.SensorReading getLeftReading() { return leftReading; }
    public sensors.SensorReading getRightReading() { return rightReading; }
    public BatteryReading getBatteryReading() { return batteryReading; }
    public BatterySensor getBatterySensor() { return batterySensor; }

    /**
     * Selecciona el siguiente objetivo no limpiado más cercano.
     */
    private void selectNextTarget() {
        List<Position> uncleaned = room.getUncleanedPositions();

        // Verificar si ya se limpiaron todas las celdas
        if (uncleaned.isEmpty()) {
            System.out.println("¡Limpieza completada!");
            robot.notifyCleaningCompleted();
            robot.setState(new robot.state.IdleState());
            return;
        }

        // Encontrar la celda sucia más cercana
        Position current = robot.getCurrent();
        Position nearest = null;
        int minDistance = Integer.MAX_VALUE;

        for (Position p : uncleaned) {
            if (inaccessible.contains(p)) continue;

            int distance = current.manhattanDistance(p);
            if (distance < minDistance) {
                minDistance = distance;
                nearest = p;
            }
        }

        // Si encontramos un objetivo, calcular ruta
        if (nearest != null) {
            List<Position> path = AStar.findPath(current, nearest, room);
            if (path != null && !path.isEmpty()) {
                robot.setPath(path);
                System.out.println("Nueva ruta calculada hacia " + nearest + " (distancia: " + minDistance + ")");
            } else {
                inaccessible.add(nearest);
                // Intentar con otro objetivo
                selectNextTarget();
            }
        } else {
            // No hay objetivos accesibles
            System.out.println("No hay más objetivos accesibles");
            robot.setState(new robot.state.IdleState());
        }
    }

    public int getFrontObserverCount() { return front.getObserverCount(); }
    public int getLeftObserverCount() { return left.getObserverCount(); }
    public int getRightObserverCount() { return right.getObserverCount(); }

    public String getFrontObserversInfo() { return front.getObserversInfo(); }
    public String getLeftObserversInfo() { return left.getObserversInfo(); }
    public String getRightObserversInfo() { return right.getObserversInfo(); }

    public void setCharger(Position p) { robot.setCharger(p); }

    public void requestPathTo(Position goal) {
        robot.recalculatePathTo(goal);
        // si no hay path, marcar inaccessible
        if (!robot.hasPath()) inaccessible.add(goal);
    }
}

