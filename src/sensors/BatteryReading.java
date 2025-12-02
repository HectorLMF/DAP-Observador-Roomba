package sensors;

import model.Position;

/**
 * Lectura del sensor de batería.
 * Incluye la distancia REAL (calculada con A*) al cargador.
 */
public class BatteryReading extends SensorReading {
    private final int currentBattery;
    private final int maxBattery;
    private final int realDistanceToCharger; // Distancia calculada con A*
    private final boolean needsCharging;
    private final boolean critical;
    private final boolean canReachCharger;

    public BatteryReading(int currentBattery, int maxBattery, int realDistanceToCharger,
                         boolean needsCharging, boolean critical, Position origin, boolean canReachCharger) {
        super(Type.FRONT, realDistanceToCharger, false, origin);
        this.currentBattery = currentBattery;
        this.maxBattery = maxBattery;
        this.realDistanceToCharger = realDistanceToCharger;
        this.needsCharging = needsCharging;
        this.critical = critical;
        this.canReachCharger = canReachCharger;
    }

    public int getCurrentBattery() { return currentBattery; }
    public int getMaxBattery() { return maxBattery; }
    public int getDistanceToCharger() { return realDistanceToCharger; }
    public int getRealDistanceToCharger() { return realDistanceToCharger; }
    public boolean needsCharging() { return needsCharging; }
    public boolean isCritical() { return critical; }
    public boolean canReachCharger() { return canReachCharger; }

    public int getBatteryPercentage() {
        return (int) ((currentBattery * 100.0) / maxBattery);
    }

    @Override
    public String toString() {
        return String.format("BatteryReading{level=%d/%d (%d%%), realDistance=%d (A*), needsCharging=%b, critical=%b, canReach=%b}",
            currentBattery, maxBattery, getBatteryPercentage(), realDistanceToCharger, needsCharging, critical, canReachCharger);
    }
}

