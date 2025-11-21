package robot;

import model.Position;
import model.Room;
import observer.Observer;
import sensors.SensorReading;
import pathfinding.AStar;

import java.util.ArrayList;
import java.util.List;

/**
 * Robot que observa sensores y mantiene estado.
 */
public class Robot implements Observer {
    private Position current;
    private Position charger;
    private final Room room;
    private List<Position> path = new ArrayList<>();
    private robot.state.RobotState currentState;

    public Robot(Room room, Position start) {
        this.room = room;
        this.current = start;
        // marcar la celda inicial como cleaned/visitada por defecto
        room.setCleaned(start, true);
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
        // TODO: reaccionar ante lecturas (ej: si frontal y detecta obstaculo -> cambiar estado)
        if (reading.getType() == SensorReading.Type.FRONT && reading.isObstacleDetected()) {
            // cambiar a recalculating
            setState(new robot.state.RecalculatingState());
        }
    }

    public void setState(robot.state.RobotState state) {
        if (this.currentState != null) this.currentState.onExit(this);
        this.currentState = state;
        if (this.currentState != null) this.currentState.onEnter(this);
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
        Position next = path.remove(0);
        // validar
        if (!room.inBounds(next) || room.isObstacle(next)) {
            // obstáculo inesperado
            setState(new robot.state.RecalculatingState());
            return;
        }
        this.current = next;
        room.setCleaned(next, true);
    }

    public void recalculatePathTo(Position goal) {
        // usar A*
        List<Position> newPath = AStar.findPath(current, goal, room);
        if (newPath == null) {
            this.path = new ArrayList<>();
        } else {
            this.path = newPath;
        }
    }

    public void recalculatePathToNextTarget() {
        // placeholder: la selección de la siguiente celda la hace RobotManager
    }
}
