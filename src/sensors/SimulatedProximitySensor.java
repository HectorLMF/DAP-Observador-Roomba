package sensors;

import model.Position;
import model.Room;
import observer.Subject;

import java.util.ArrayList;
import java.util.List;

/**
 * Sensor de proximidad simulado con alcance fijo.
 */
public class SimulatedProximitySensor implements Subject {
    private final SensorReading.Type type;
    private final int range;
    private final List<observer.Observer> observers = new ArrayList<>();

    public SimulatedProximitySensor(SensorReading.Type type, int range) {
        this.type = type;
        this.range = range;
    }

    @Override
    public void register(observer.Observer o) { observers.add(o); }

    @Override
    public void unregister(observer.Observer o) { observers.remove(o); }

    @Override
    public void notifyObservers(Object event) {
        for (observer.Observer o : observers) {
            o.update((SensorReading) event);
        }
    }

    /**
     * Escanea la habitación desde la posición origen y notifica una lectura.
     */
    public void scan(Room room, Position origin) {
        // Simulacion simple: dependiendo del type comprobamos en la direccion
        int dx = 0, dy = 0;
        switch (type) {
            case FRONT: dx = 0; dy = -1; break; // assumimos hacia arriba como frontal (se puede parametrizar)
            case LEFT: dx = -1; dy = 0; break;
            case RIGHT: dx = 1; dy = 0; break;
        }

        int foundDist = -1;
        boolean found = false;
        for (int d = 1; d <= range; d++) {
            Position p = new Position(origin.x + dx * d, origin.y + dy * d);
            if (!room.inBounds(p)) { foundDist = d; found = true; break; }
            if (room.isObstacle(p)) { foundDist = d; found = true; break; }
        }

        SensorReading reading = new SensorReading(type, found ? foundDist : -1, found, origin);
        notifyObservers(reading);
    }
}

