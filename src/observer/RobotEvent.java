package observer;

/**
 * Evento emitido por el robot cuando cambia su estado.
 */
public class RobotEvent {
    public enum Type {
        STATE_CHANGED,
        POSITION_CHANGED,
        PATH_CALCULATED,
        BATTERY_LOW,
        CLEANING_COMPLETED,
        OBSTACLE_DETECTED,
        RETURNED_TO_CHARGER
    }

    private final Type type;
    private final Object data;
    private final long timestamp;

    public RobotEvent(Type type, Object data) {
        this.type = type;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public Type getType() { return type; }
    public Object getData() { return data; }
    public long getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return String.format("RobotEvent{type=%s, data=%s, timestamp=%d}", type, data, timestamp);
    }
}

