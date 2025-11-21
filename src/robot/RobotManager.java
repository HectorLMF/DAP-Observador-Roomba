package robot;

import model.Position;
import model.Room;
import sensors.SimulatedProximitySensor;
import sensors.SensorReading;
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

    private final List<Position> inaccessible = new ArrayList<>();

    public RobotManager(Room room, Position start) {
        this.room = room;
        this.robot = new Robot(room, start);
        this.front = new SimulatedProximitySensor(SensorReading.Type.FRONT, 3);
        this.left = new SimulatedProximitySensor(SensorReading.Type.LEFT, 3);
        this.right = new SimulatedProximitySensor(SensorReading.Type.RIGHT, 3);

        // register robot as observer
        front.register(robot);
        left.register(robot);
        right.register(robot);
    }

    public Robot getRobot() { return robot; }

    public void tick() {
        // sensores escanean desde la posicion actual
        front.scan(room, robot.getCurrent());
        left.scan(room, robot.getCurrent());
        right.scan(room, robot.getCurrent());

        // luego robot actua según estado
        robot.tick();
    }

    public void setCharger(Position p) { robot.setCharger(p); }

    public void requestPathTo(Position goal) {
        robot.recalculatePathTo(goal);
        // si no hay path, marcar inaccessible
        if (!robot.hasPath()) inaccessible.add(goal);
    }

    // TODO: seleccionar siguiente objetivo no limpiado
}

