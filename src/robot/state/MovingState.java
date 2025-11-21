package robot.state;

import robot.Robot;

public class MovingState implements RobotState {
    @Override
    public void onEnter(Robot robot) {
        // TODO: preparar movimiento
    }

    @Override
    public void onExit(Robot robot) {}

    @Override
    public void tick(Robot robot) {
        // avanzar un paso si hay path
        robot.performMoveStep();
    }
}

