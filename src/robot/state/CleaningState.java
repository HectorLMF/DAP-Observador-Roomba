package robot.state;

import robot.Robot;

public class CleaningState implements RobotState {
    @Override
    public void onEnter(Robot robot) {
        // TODO: iniciar limpieza
    }

    @Override
    public void onExit(Robot robot) {}

    @Override
    public void tick(Robot robot) {
        // en cleaning state delegar a moving o idle según path
        if (robot.hasPath()) {
            robot.performMoveStep();
        }
    }
}

