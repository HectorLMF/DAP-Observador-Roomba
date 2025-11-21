package robot.state;

import robot.Robot;

public class IdleState implements RobotState {
    @Override
    public void onEnter(Robot robot) {
        // TODO: acciones al entrar en Idle
    }

    @Override
    public void onExit(Robot robot) {}

    @Override
    public void tick(Robot robot) {
        // Idle no hace nada por tick
    }
}

