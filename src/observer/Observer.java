package observer;

import sensors.SensorReading;

/**
 * Observador para recibir lecturas de sensores.
 */
public interface Observer {
    void update(SensorReading reading);
}

