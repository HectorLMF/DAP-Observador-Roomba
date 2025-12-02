package observer;

import model.Position;

/**
 * Observador que recopila estadísticas sobre el comportamiento del robot.
 */
public class RobotStatisticsObserver implements RobotObserver {
    private int stateChanges = 0;
    private int positionChanges = 0;
    private int pathCalculations = 0;
    private int obstaclesDetected = 0;
    private int batteryWarnings = 0;
    private int cleaningCompletions = 0;
    private int chargerReturns = 0;
    private long totalDistance = 0;
    private Position lastPosition = null;

    @Override
    public void onRobotEvent(RobotEvent event) {
        switch (event.getType()) {
            case STATE_CHANGED:
                stateChanges++;
                break;
            case POSITION_CHANGED:
                positionChanges++;
                if (event.getData() instanceof Position) {
                    Position newPos = (Position) event.getData();
                    if (lastPosition != null) {
                        totalDistance += lastPosition.manhattanDistance(newPos);
                    }
                    lastPosition = newPos;
                }
                break;
            case PATH_CALCULATED:
                pathCalculations++;
                break;
            case BATTERY_LOW:
                batteryWarnings++;
                break;
            case CLEANING_COMPLETED:
                cleaningCompletions++;
                break;
            case OBSTACLE_DETECTED:
                obstaclesDetected++;
                break;
            case RETURNED_TO_CHARGER:
                chargerReturns++;
                break;
        }
    }

    public void printStatistics() {
        System.out.println("=== Robot Statistics ===");
        System.out.println("State changes: " + stateChanges);
        System.out.println("Position changes: " + positionChanges);
        System.out.println("Path calculations: " + pathCalculations);
        System.out.println("Obstacles detected: " + obstaclesDetected);
        System.out.println("Battery warnings: " + batteryWarnings);
        System.out.println("Cleaning completions: " + cleaningCompletions);
        System.out.println("Charger returns: " + chargerReturns);
        System.out.println("Total distance (Manhattan): " + totalDistance);
    }

    // Getters para las estadísticas
    public int getStateChanges() { return stateChanges; }
    public int getPositionChanges() { return positionChanges; }
    public int getPathCalculations() { return pathCalculations; }
    public int getObstaclesDetected() { return obstaclesDetected; }
    public int getBatteryWarnings() { return batteryWarnings; }
    public int getCleaningCompletions() { return cleaningCompletions; }
    public int getChargerReturns() { return chargerReturns; }
    public long getTotalDistance() { return totalDistance; }

    public void reset() {
        stateChanges = 0;
        positionChanges = 0;
        pathCalculations = 0;
        obstaclesDetected = 0;
        batteryWarnings = 0;
        cleaningCompletions = 0;
        chargerReturns = 0;
        totalDistance = 0;
        lastPosition = null;
    }
}

