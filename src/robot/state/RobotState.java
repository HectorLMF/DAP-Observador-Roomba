package robot.state;

import robot.Robot;

public interface RobotState {
    void onEnter(Robot robot);
    void onExit(Robot robot);
    void tick(Robot robot);
}

