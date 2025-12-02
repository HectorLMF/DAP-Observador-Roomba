package robot.state;

import robot.Robot;

/**
 * Estado cuando el robot está cargando en el cargador.
 * Recupera 5 de energía por tick.
 */
public class ChargingState implements RobotState {
    private static final int CHARGE_RATE = 5; // Energía recuperada por tick

    @Override
    public void onEnter(Robot robot) {
        System.out.println("🔌 Robot cargando...");
    }

    @Override
    public void onExit(Robot robot) {
        System.out.println("✓ Batería llena. Reanudando limpieza...");
    }

    @Override
    public void tick(Robot robot) {
        // Cargar batería
        robot.chargeBattery(CHARGE_RATE);

        // Si la batería está llena, volver a limpiar
        if (robot.isBatteryFull()) {
            robot.setState(new CleaningState());
        }
    }
}

