package robot.state;

import robot.Robot;

public class RecalculatingState implements RobotState {
    @Override
    public void onEnter(Robot robot) {
        // Al entrar en recalculating, dejamos que el RobotManager solicite una nueva ruta.
    }

    @Override
    public void onExit(Robot robot) {}

    @Override
    public void tick(Robot robot) {
        // Mientras recalcula, no se mueve. RobotManager debe asignar la nueva ruta.
    }
}
