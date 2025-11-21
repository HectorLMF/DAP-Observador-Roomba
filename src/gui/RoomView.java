package gui;

import model.Position;
import model.Room;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class RoomView extends JPanel {
    private Room room;
    private int cellSize = 25;

    private boolean placingCharger = false;
    private boolean placingObstacles = false;

    public interface RoomViewListener {
        void onChargerPlaced(Position p);
        void onObstacleToggled(Position p);
        void onCellClicked(Position p);
    }

    private RoomViewListener listener;

    private Position robotPosition = null;
    private List<Position> path = new ArrayList<>();

    public RoomView(int cols, int rows, int cellSize) {
        this.cellSize = cellSize;
        setPreferredSize(new Dimension(cols * cellSize, rows * cellSize));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleClick(e);
            }
        });
    }

    public void setListener(RoomViewListener l) { this.listener = l; }

    public void setRoom(Room room) {
        this.room = room;
        if (room != null) setPreferredSize(new Dimension(room.getWidth() * cellSize, room.getHeight() * cellSize));
        revalidate();
        repaint();
    }

    public void setCellSize(int size) { this.cellSize = size; revalidate(); repaint(); }

    private void handleClick(MouseEvent e) {
        if (room == null) return;
        int x = e.getX() / cellSize;
        int y = e.getY() / cellSize;
        Position p = new Position(x, y);
        if (!room.inBounds(p)) return;

        if (placingCharger && SwingUtilities.isLeftMouseButton(e)) {
            // colocar cargador
            // eliminar cargador anterior
            // Room maneja setCharger
            room.setCharger(p, true);
            if (listener != null) listener.onChargerPlaced(p);
            placingCharger = false;
            repaint();
            return;
        }

        if (placingObstacles && SwingUtilities.isLeftMouseButton(e)) {
            room.setObstacle(p, !room.isObstacle(p));
            if (listener != null) listener.onObstacleToggled(p);
            repaint();
            return;
        }

        if (SwingUtilities.isRightMouseButton(e)) {
            room.setObstacle(p, !room.isObstacle(p));
            if (listener != null) listener.onObstacleToggled(p);
            repaint();
            return;
        }

        if (listener != null) listener.onCellClicked(p);
    }

    public void setPlacingCharger(boolean val) { this.placingCharger = val; }
    public void setPlacingObstacles(boolean val) { this.placingObstacles = val; }

    public void setRobotPosition(Position p) { this.robotPosition = p; repaint(); }
    public void setPath(List<Position> newPath) { this.path = newPath != null ? new ArrayList<>(newPath) : new ArrayList<>(); repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        if (room == null) return;

        for (int x = 0; x < room.getWidth(); x++) {
            for (int y = 0; y < room.getHeight(); y++) {
                int sx = x * cellSize;
                int sy = y * cellSize;
                Position p = new Position(x, y);
                if (room.isObstacle(p)) {
                    g2.setColor(Color.DARK_GRAY);
                    g2.fillRect(sx, sy, cellSize, cellSize);
                } else if (room.hasCharger(p)) {
                    g2.setColor(Color.ORANGE);
                    g2.fillRect(sx, sy, cellSize, cellSize);
                } else if (room.isCleaned(p)) {
                    g2.setColor(new Color(144,238,144)); // light green
                    g2.fillRect(sx, sy, cellSize, cellSize);
                } else {
                    g2.setColor(Color.WHITE);
                    g2.fillRect(sx, sy, cellSize, cellSize);
                }
                g2.setColor(Color.BLACK);
                g2.drawRect(sx, sy, cellSize, cellSize);
            }
        }

        // dibujar ruta
        if (path != null && !path.isEmpty()) {
            g2.setColor(new Color(51, 51, 255, 80));
            for (Position pp : path) {
                int sx = pp.x * cellSize;
                int sy = pp.y * cellSize;
                g2.fillRect(sx, sy, cellSize, cellSize);
            }
        }

        // dibujar robot
        if (robotPosition != null && room.inBounds(robotPosition)) {
            int cx = robotPosition.x * cellSize + cellSize / 2;
            int cy = robotPosition.y * cellSize + cellSize / 2;
            int r = (int) (cellSize * 0.35);
            g2.setColor(Color.RED);
            g2.fillOval(cx - r, cy - r, r * 2, r * 2);
            g2.setColor(Color.BLACK);
            g2.drawOval(cx - r, cy - r, r * 2, r * 2);
        }

        g2.dispose();
    }
}
