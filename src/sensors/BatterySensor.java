package sensors;

import model.Position;
import model.Room;
import observer.Observer;
import observer.Subject;
import pathfinding.AStar;

import java.util.ArrayList;
import java.util.List;

/**
 * Sensor de batería que monitorea el nivel de energía del robot.
 * Calcula la distancia real (A*) al cargador para decidir cuándo volver.
 */
public class BatterySensor implements Subject {
    private int batteryLevel;
    private final int maxBattery;
    private final List<Observer> observers = new ArrayList<>();

    public BatterySensor(int maxBattery) {
        this.maxBattery = maxBattery;
        this.batteryLevel = maxBattery;
    }

    @Override
    public void register(Observer o) {
        observers.add(o);
    }

    @Override
    public void unregister(Observer o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers(Object event) {
        for (Observer o : observers) {
            o.update((BatteryReading) event);
        }
    }

    /**
     * Consume batería (cada movimiento).
     */
    public void consumeBattery(int amount) {
        batteryLevel = Math.max(0, batteryLevel - amount);
    }

    /**
     * Carga la batería.
     */
    public void chargeBattery(int amount) {
        batteryLevel = Math.min(maxBattery, batteryLevel + amount);
    }

    /**
     * Verifica el nivel de batería usando A* para calcular distancia real al cargador.
     * @param currentPos Posición actual del robot
     * @param chargerPos Posición del cargador
     * @param currentBatteryLevel Nivel actual de batería del robot
     * @param room La habitación para calcular ruta con A*
     */
    public BatteryReading checkBattery(Position currentPos, Position chargerPos,
                                       int currentBatteryLevel, Room room) {
        // Sincronizar nivel de batería
        this.batteryLevel = currentBatteryLevel;

        int realDistanceToCharger = 0;
        boolean canReachCharger = true;

        // Si ya está en el cargador, no necesita moverse
        if (chargerPos != null && currentPos.equals(chargerPos)) {
            realDistanceToCharger = 0;
        } else if (chargerPos != null && room != null) {
            // Calcular distancia REAL usando A* (no Manhattan)
            List<Position> pathToCharger = AStar.findPath(currentPos, chargerPos, room);

            if (pathToCharger != null && !pathToCharger.isEmpty()) {
                realDistanceToCharger = pathToCharger.size()+5; // +5 para margen de seguridad
            } else {
                // No hay ruta al cargador - crítico
                canReachCharger = false;
                realDistanceToCharger = 999; // Distancia imposible
            }
        }

        // Decisión: Si batería < distancia real (no <=), DEBE volver
        // Si batería == distancia, aún puede intentar un movimiento más
        boolean needsCharging = batteryLevel < realDistanceToCharger;
        boolean critical = batteryLevel <= 0 || !canReachCharger;

        BatteryReading reading = new BatteryReading(
            batteryLevel,
            maxBattery,
            realDistanceToCharger,
            needsCharging,
            critical,
            currentPos,
            canReachCharger
        );

        // Notificar a todos los observadores (incluyendo el Robot)
        notifyObservers(reading);

        return reading;
    }

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public int getMaxBattery() {
        return maxBattery;
    }

    public boolean isFull() {
        return batteryLevel >= maxBattery;
    }

    public boolean isEmpty() {
        return batteryLevel <= 0;
    }

    public int getBatteryPercentage() {
        return (int) ((batteryLevel * 100.0) / maxBattery);
    }
}

