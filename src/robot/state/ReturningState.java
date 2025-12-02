package robot.state;

import robot.Robot;

/**
 * Estado cuando el robot está regresando al cargador.
 */
public class ReturningState implements RobotState {
    @Override
    public void onEnter(Robot robot) {
        System.out.println("🔋 Robot regresando al cargador...");
        // Calcular ruta al cargador
        if (robot.getCharger() != null) {
            robot.recalculatePathTo(robot.getCharger());
        }
    }

    @Override
    public void onExit(Robot robot) {
        System.out.println("✓ Robot llegó al cargador");
    }

    @Override
    public void tick(Robot robot) {
        // Si ya llegó al cargador, cambiar a ChargingState
        if (robot.getCharger() != null && robot.getCurrent().equals(robot.getCharger())) {
            robot.setState(new ChargingState());
            return;
        }

        // Si tiene ruta, moverse
        if (robot.hasPath()) {
            robot.performMoveStep();
        } else {
            // Sin ruta al cargador, recalcular
            if (robot.getCharger() != null) {
                robot.recalculatePathTo(robot.getCharger());
            }
        }
    }
}

