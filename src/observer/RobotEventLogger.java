package observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Observador que registra todos los eventos del robot en un log.
 */
public class RobotEventLogger implements RobotObserver {
    private final List<RobotEvent> eventLog = new ArrayList<>();
    private final boolean printToConsole;

    public RobotEventLogger(boolean printToConsole) {
        this.printToConsole = printToConsole;
    }

    @Override
    public void onRobotEvent(RobotEvent event) {
        eventLog.add(event);
        if (printToConsole) {
            System.out.println("[LOG] " + event);
        }
    }

    public List<RobotEvent> getEventLog() {
        return new ArrayList<>(eventLog);
    }

    public void clearLog() {
        eventLog.clear();
    }

    public int getEventCount() {
        return eventLog.size();
    }

    public int getEventCountByType(RobotEvent.Type type) {
        return (int) eventLog.stream()
                .filter(e -> e.getType() == type)
                .count();
    }

    public void printSummary() {
        System.out.println("=== Event Log Summary ===");
        System.out.println("Total events: " + eventLog.size());
        for (RobotEvent.Type type : RobotEvent.Type.values()) {
            int count = getEventCountByType(type);
            if (count > 0) {
                System.out.println("  " + type + ": " + count);
            }
        }
    }
}

