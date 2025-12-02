package robot;

import model.Position;
import model.Room;
import observer.Observer;
import observer.RobotObserver;
import observer.RobotEvent;
import sensors.SensorReading;
import sensors.BatteryReading;
import pathfinding.AStar;

import java.util.ArrayList;
import java.util.List;

/**
 * Robot que observa sensores y mantiene estado.
 * También actúa como Subject notificando sus propios eventos a observadores.
 */
public class Robot implements Observer {
    private Position current;
    private Position charger;
    private final Room room;
    private List<Position> path = new ArrayList<>();
    private robot.state.RobotState currentState;

    // Lista de observadores del robot
    private final List<RobotObserver> robotObservers = new ArrayList<>();

    // Sistema de batería
    private int batteryLevel;
    private final int maxBattery;
    private static final int BATTERY_CONSUMPTION_PER_MOVE = 1;

    public Robot(Room room, Position start) {
        this(room, start, 100); // Batería por defecto: 100
    }

    public Robot(Room room, Position start, int maxBattery) {
        this.room = room;
        this.current = start;
        this.maxBattery = maxBattery;
        this.batteryLevel = maxBattery;
    }

    // Métodos para gestionar observadores del robot
    public void addRobotObserver(RobotObserver observer) {
        if (!robotObservers.contains(observer)) {
            robotObservers.add(observer);
        }
    }

    public void removeRobotObserver(RobotObserver observer) {
        robotObservers.remove(observer);
    }

    private void notifyRobotObservers(RobotEvent event) {
        for (RobotObserver observer : robotObservers) {
            observer.onRobotEvent(event);
        }
    }

    public int getRobotObserverCount() {
        return robotObservers.size();
    }

    public Position getCurrent() { return current; }
    public Position getCharger() { return charger; }

    public void setCurrent(Position p) {
        if (p == null) return;
        if (!room.inBounds(p) || room.isObstacle(p)) return;
        this.current = p;
        room.setCleaned(p, true);
        // limpiar ruta anterior
        this.path = new ArrayList<>();
        // Notificar cambio de posición
        notifyRobotObservers(new RobotEvent(RobotEvent.Type.POSITION_CHANGED, p));
    }

    public void setCharger(Position charger) {
        if (this.charger != null) room.setCharger(this.charger, false);
        this.charger = charger;
        room.setCharger(charger, true);
    }

    @Override
    public void update(SensorReading reading) {
        handleSensor(reading);
    }

    public void handleSensor(SensorReading reading) {
        // Manejar lectura de batería
        if (reading instanceof BatteryReading) {
            BatteryReading batteryReading = (BatteryReading) reading;

            // Si necesita carga y no está ya volviendo/cargando
            if (batteryReading.needsCharging() &&
                !(currentState instanceof robot.state.ReturningState) &&
                !(currentState instanceof robot.state.ChargingState)) {

                System.out.println("⚠️ DECISIÓN DE RETORNO:");
                System.out.println("   Batería actual: " + batteryReading.getCurrentBattery() + "/" + batteryReading.getMaxBattery());
                System.out.println("   Distancia REAL al cargador (A*): " + batteryReading.getRealDistanceToCharger() + " movimientos");
                System.out.println("   Conclusión: Batería < Distancia → DEBE VOLVER AHORA");
                System.out.println("   🔋 Regresando al cargador...");

                notifyRobotObservers(new RobotEvent(RobotEvent.Type.BATTERY_LOW, batteryReading));
                setState(new robot.state.ReturningState());
            }

            // Si batería crítica o no puede alcanzar cargador, detener
            if ((batteryReading.isCritical() || !batteryReading.canReachCharger()) &&
                !(currentState instanceof robot.state.IdleState)) {
                if (!batteryReading.canReachCharger()) {
                    System.out.println("❌ ¡NO HAY RUTA AL CARGADOR! Deteniendo...");
                } else {
                    System.out.println("❌ Batería agotada!");
                }
                setState(new robot.state.IdleState());
            }
            return;
        }

        // Sensores de proximidad
        if (reading.getType() == SensorReading.Type.FRONT &&
            reading.isObstacleDetected() &&
            reading.getDistance() == 1) {
            notifyRobotObservers(new RobotEvent(RobotEvent.Type.OBSTACLE_DETECTED, reading));
        }
    }

    // Métodos de batería

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public int getMaxBattery() {
        return maxBattery;
    }

    public int getBatteryPercentage() {
        return (int) ((batteryLevel * 100.0) / maxBattery);
    }

    public void consumeBattery(int amount) {
        batteryLevel = Math.max(0, batteryLevel - amount);
    }

    public void chargeBattery(int amount) {
        batteryLevel = Math.min(maxBattery, batteryLevel + amount);
    }

    public boolean isBatteryFull() {
        return batteryLevel >= maxBattery;
    }

    public boolean isBatteryEmpty() {
        return batteryLevel <= 0;
    }

    public void setState(robot.state.RobotState state) {
        if (this.currentState != null) this.currentState.onExit(this);
        this.currentState = state;
        if (this.currentState != null) {
            this.currentState.onEnter(this);
            // Notificar cambio de estado
            notifyRobotObservers(new RobotEvent(RobotEvent.Type.STATE_CHANGED,
                state != null ? state.getClass().getSimpleName() : "null"));
        }
    }

    public void tick() {
        if (currentState != null) currentState.tick(this);
    }

    public boolean hasPath() { return path != null && !path.isEmpty(); }

    public List<Position> getPath() {
        return new ArrayList<>(path);
    }

    public void setPath(List<Position> newPath) {
        this.path = newPath != null ? new ArrayList<>(newPath) : new ArrayList<>();
    }

    public void performMoveStep() {
        if (!hasPath()) return;

        // Verificar si hay batería suficiente
        if (batteryLevel <= 0) {
            System.out.println("❌ Sin batería para moverse!");
            setState(new robot.state.IdleState());
            return;
        }

        Position next = path.remove(0);
        // validar - el cargador NO bloquea el movimiento
        boolean isBlocked = room.isObstacle(next) && !room.hasChargerAt(next);
        if (!room.inBounds(next) || isBlocked) {
            // obstáculo inesperado (pero no el cargador)
            notifyRobotObservers(new RobotEvent(RobotEvent.Type.OBSTACLE_DETECTED, next));
            setState(new robot.state.RecalculatingState());
            return;
        }

        this.current = next;
        room.setCleaned(next, true);

        // Consumir batería por movimiento
        consumeBattery(BATTERY_CONSUMPTION_PER_MOVE);

        // Notificar cambio de posición
        notifyRobotObservers(new RobotEvent(RobotEvent.Type.POSITION_CHANGED, next));
    }

    public void recalculatePathTo(Position goal) {
        // usar A*
        List<Position> newPath = AStar.findPath(current, goal, room);
        if (newPath == null) {
            this.path = new ArrayList<>();
        } else {
            this.path = newPath;
        }
        // Notificar que se calculó una ruta
        notifyRobotObservers(new RobotEvent(RobotEvent.Type.PATH_CALCULATED,
            newPath != null ? newPath.size() : 0));
    }

    public void recalculatePathToNextTarget() {
        // placeholder: la selección de la siguiente celda la hace RobotManager
    }

    // Métodos adicionales para notificar eventos específicos
    public void notifyBatteryLow() {
        notifyRobotObservers(new RobotEvent(RobotEvent.Type.BATTERY_LOW, null));
    }

    public void notifyCleaningCompleted() {
        notifyRobotObservers(new RobotEvent(RobotEvent.Type.CLEANING_COMPLETED, null));
    }

    public void notifyReturnedToCharger() {
        notifyRobotObservers(new RobotEvent(RobotEvent.Type.RETURNED_TO_CHARGER, charger));
    }

    public Room getRoom() {
        return room;
    }

    public robot.state.RobotState getCurrentState() {
        return currentState;
    }
}

