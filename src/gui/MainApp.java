package gui;

import model.Position;
import model.Room;
import robot.RobotManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class MainApp {
    private JFrame frame;
    private RoomView roomView;
    private Room room;
    private RobotManager manager;
    private Timer timer; // Swing timer for ticks

    public MainApp() {
        SwingUtilities.invokeLater(this::createAndShowGui);
    }

    private void createAndShowGui() {
        frame = new JFrame("Roomba Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

        JPanel sizeBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField dimX = new JTextField("20", 4);
        JTextField dimY = new JTextField("20", 4);
        JButton generate = new JButton("Generar");
        sizeBox.add(new JLabel("DimX:")); sizeBox.add(dimX);
        sizeBox.add(new JLabel("DimY:")); sizeBox.add(dimY);
        sizeBox.add(generate);

        JComboBox<String> speed = new JComboBox<>(new String[]{"Rápido (100 ms)", "Medio (300 ms)", "Lento (600 ms)"});
        speed.setSelectedIndex(1);

        JButton startStop = new JButton("Start");
        JToggleButton placeCharger = new JToggleButton("Colocar cargador");
        JToggleButton placeObs = new JToggleButton("Colocar obstáculos");

        controls.add(sizeBox);
        controls.add(speed);
        controls.add(startStop);
        controls.add(placeCharger);
        controls.add(placeObs);

        roomView = new RoomView(20,20,25);
        JScrollPane scroll = new JScrollPane(roomView);

        frame.add(scroll, BorderLayout.CENTER);
        frame.add(controls, BorderLayout.EAST);

        generate.addActionListener((ActionEvent e) -> {
            int w = Integer.parseInt(dimX.getText());
            int h = Integer.parseInt(dimY.getText());
            room = new Room(w, h);
            roomView.setRoom(room);
            manager = new RobotManager(room, new Position(0,0));

            roomView.setListener(new RoomView.RoomViewListener() {
                @Override
                public void onChargerPlaced(Position p) {
                    manager.setCharger(p);
                    manager.getRobot().setCharger(p);
                    manager.getRobot().setCurrent(p);
                    manager.getRobot().setPath(null);
                    roomView.setRobotPosition(manager.getRobot().getCurrent());
                    roomView.setPath(null);
                }

                @Override
                public void onObstacleToggled(Position p) {
                    if (manager != null && manager.getRobot() != null) {
                        manager.getRobot().setPath(null);
                        roomView.setPath(null);
                    }
                }

                @Override
                public void onCellClicked(Position p) {
                    // noop
                }
            });

            frame.pack();
        });

        placeCharger.addActionListener(e -> roomView.setPlacingCharger(placeCharger.isSelected()));
        placeObs.addActionListener(e -> roomView.setPlacingObstacles(placeObs.isSelected()));

        startStop.addActionListener(e -> {
            if (timer != null && timer.isRunning()) {
                timer.stop();
                startStop.setText("Start");
            } else {
                int ms = 300;
                String sel = (String) speed.getSelectedItem();
                if (sel.contains("100")) ms = 100;
                if (sel.contains("600")) ms = 600;
                timer = new Timer(ms, ev -> {
                    if (manager != null) manager.tick();
                    if (manager != null && manager.getRobot() != null) {
                        roomView.setRobotPosition(manager.getRobot().getCurrent());
                        roomView.setPath(manager.getRobot().getPath());
                    }
                    roomView.repaint();
                });
                timer.start();
                startStop.setText("Stop");
            }
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // generar por defecto
        generate.doClick();
    }

    public static void main(String[] args) {
        new MainApp();
    }
}
