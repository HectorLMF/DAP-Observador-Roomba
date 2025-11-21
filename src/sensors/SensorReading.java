package sensors;

import model.Position;

public class SensorReading {
    public enum Type { FRONT, LEFT, RIGHT }

    private final Type type;
    private final int distance; // -1 means no obstacle within range
    private final boolean obstacleDetected;
    private final Position origin;

    public SensorReading(Type type, int distance, boolean obstacleDetected, Position origin) {
        this.type = type;
        this.distance = distance;
        this.obstacleDetected = obstacleDetected;
        this.origin = origin;
    }

    public Type getType() { return type; }
    public int getDistance() { return distance; }
    public boolean isObstacleDetected() { return obstacleDetected; }
    public Position getOrigin() { return origin; }
}

