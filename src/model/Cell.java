package model;

/**
 * Estado de una celda en la habitación.
 */
public class Cell {
    private boolean obstacle = false;
    private boolean cleaned = false;
    private boolean charger = false;

    public boolean isObstacle() { return obstacle; }
    public void setObstacle(boolean obstacle) { this.obstacle = obstacle; }

    public boolean isCleaned() { return cleaned; }
    public void setCleaned(boolean cleaned) { this.cleaned = cleaned; }

    public boolean hasCharger() { return charger; }
    public void setCharger(boolean charger) { this.charger = charger; }
}

