package observer;

/**
 * Observador que muestra alertas críticas del robot.
 */
public class RobotAlertObserver implements RobotObserver {
    private boolean batteryAlertActive = false;
    private int alertCount = 0;

    @Override
    public void onRobotEvent(RobotEvent event) {
        switch (event.getType()) {
            case BATTERY_LOW:
                if (!batteryAlertActive) {
                    System.out.println("[!] ALERTA: Bateria baja detectada!");
                    batteryAlertActive = true;
                    alertCount++;
                }
                break;
            case RETURNED_TO_CHARGER:
                if (batteryAlertActive) {
                    System.out.println("[OK] Alerta resuelta: Robot en cargador");
                    batteryAlertActive = false;
                }
                break;
            case OBSTACLE_DETECTED:
                System.out.println("[!] ALERTA: Obstaculo detectado! Recalculando ruta...");
                alertCount++;
                break;
            case CLEANING_COMPLETED:
                System.out.println("[OK] Limpieza completada exitosamente!");
                break;
        }
    }

    public boolean isBatteryAlertActive() {
        return batteryAlertActive;
    }

    public int getAlertCount() {
        return alertCount;
    }

    public void reset() {
        batteryAlertActive = false;
        alertCount = 0;
    }
}

