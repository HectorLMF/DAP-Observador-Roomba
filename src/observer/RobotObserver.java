package observer;

/**
 * Observador especializado para eventos del robot.
 */
public interface RobotObserver {
    /**
     * Se invoca cuando el robot emite un evento.
     * @param event El evento del robot
     */
    void onRobotEvent(RobotEvent event);
}

