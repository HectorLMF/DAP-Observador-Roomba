package robot.state;

import robot.Robot;

public class CleaningState implements RobotState {
    @Override
    public void onEnter(Robot robot) {
        // Al entrar en estado de limpieza, limpiar la celda actual
        robot.getRoom().setCleaned(robot.getCurrent(), true);
    }

    @Override
    public void onExit(Robot robot) {}

    @Override
    public void tick(Robot robot) {
        // Limpiar la celda actual
        robot.getRoom().setCleaned(robot.getCurrent(), true);

        // Si hay ruta, moverse al siguiente paso
        if (robot.hasPath()) {
            robot.performMoveStep();
        }
        // Si no hay ruta, RobotManager se encargará de buscar un nuevo objetivo
    }
}

