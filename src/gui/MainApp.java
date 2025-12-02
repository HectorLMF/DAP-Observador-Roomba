package gui;

import model.Position;
import model.Room;
import robot.RobotManager;
import observer.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainApp {
    private JFrame frame;
    private RoomView roomView;
    private Room room;
    private RobotManager manager;
    private Timer timer;

    // UI fields
    private JLabel frontSensorLabel;
    private JLabel leftSensorLabel;
    private JLabel rightSensorLabel;
    private JLabel batteryLabel;
    private JProgressBar batteryProgressBar;
    private JLabel observersLabel;
    private JLabel robotStateLabel;
    private JSpinner batteryCapacitySpinner;
    private JTextPane logTextPane;
    private int turnCounter = 0;

    // Observadores del patrón Observer
    private RobotEventLogger eventLogger;
    private RobotStatisticsObserver statsObserver;
    private RobotAlertObserver alertObserver;

    public MainApp() {
        SwingUtilities.invokeLater(this::createAndShowGui);
    }

    private void createAndShowGui() {
        frame = new JFrame("Roomba Simulator - Patrón Observer + Strategy");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel controls = new JPanel();
        controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));

        JPanel sizeBox = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField dimX = new JTextField("20", 4);
        JTextField dimY = new JTextField("20", 4);
        JButton generate = new JButton("Generar");
        sizeBox.add(new JLabel("Filas:"));
        sizeBox.add(dimX);
        sizeBox.add(new JLabel("Columnas:"));
        sizeBox.add(dimY);
        sizeBox.add(generate);

        // Panel de configuración de batería
        JPanel batteryConfig = new JPanel(new FlowLayout(FlowLayout.LEFT));
        batteryConfig.setBorder(BorderFactory.createTitledBorder("Configuración"));
        batteryCapacitySpinner = new JSpinner(new SpinnerNumberModel(100, 20, 500, 10));
        batteryConfig.add(new JLabel("Capacidad Batería:"));
        batteryConfig.add(batteryCapacitySpinner);

        JComboBox<String> speed = new JComboBox<>(new String[]{
            "Rápido (100 ms)", "Medio (300 ms)", "Lento (600 ms)"
        });
        speed.setSelectedIndex(1);

        JButton startStop = new JButton("Start");
        JToggleButton placeCharger = new JToggleButton("Colocar cargador ⚡");
        JToggleButton placeObs = new JToggleButton("Obstáculo fijo");
        JToggleButton placeCat = new JToggleButton("Colocar gato 🐱");

        controls.add(sizeBox);
        controls.add(batteryConfig);

        // Panel de sensores
        JPanel sensorPanel = new JPanel(new GridLayout(0,1));
        sensorPanel.setBorder(BorderFactory.createTitledBorder("Sensores"));
        frontSensorLabel = new JLabel("Front: -");
        leftSensorLabel = new JLabel("Left: -");
        rightSensorLabel = new JLabel("Right: -");
        batteryLabel = new JLabel("🔋 Batería: 100%");
        batteryLabel.setFont(new Font("Arial", Font.BOLD, 12));

        // Barra de progreso de batería
        batteryProgressBar = new JProgressBar(0, 100);
        batteryProgressBar.setValue(100);
        batteryProgressBar.setStringPainted(true);
        batteryProgressBar.setForeground(new Color(50, 205, 50));

        sensorPanel.add(frontSensorLabel);
        sensorPanel.add(leftSensorLabel);
        sensorPanel.add(rightSensorLabel);
        sensorPanel.add(new JSeparator());
        sensorPanel.add(batteryLabel);
        sensorPanel.add(batteryProgressBar);
        controls.add(sensorPanel);

        // Panel de observadores y estado
        JPanel obsPanel = new JPanel(new GridLayout(0,1));
        obsPanel.setBorder(BorderFactory.createTitledBorder("Observador / Estado"));
        observersLabel = new JLabel("Observers: -");
        robotStateLabel = new JLabel("Robot state: -");
        obsPanel.add(observersLabel);
        obsPanel.add(robotStateLabel);
        controls.add(obsPanel);

        controls.add(speed);
        controls.add(startStop);
        controls.add(placeCharger);
        controls.add(placeObs);
        controls.add(placeCat);

        roomView = new RoomView(20, 20, 25);
        JScrollPane scroll = new JScrollPane(roomView);

        // Panel de logs del Observer
        JPanel logPanel = new JPanel(new BorderLayout());
        logPanel.setBorder(BorderFactory.createTitledBorder("Logs del Patrón Observer"));
        logTextPane = new JTextPane();
        logTextPane.setEditable(false);
        logTextPane.setFont(new Font("Monospaced", Font.PLAIN, 11));

        // Establecer tamaño fijo de 10 líneas
        FontMetrics fm = logTextPane.getFontMetrics(logTextPane.getFont());
        int lineHeight = fm.getHeight();
        int preferredHeight = lineHeight * 10 + 10; // 10 líneas + padding
        logTextPane.setPreferredSize(new Dimension(600, preferredHeight));

        JScrollPane logScroll = new JScrollPane(logTextPane);
        logScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        logScroll.setPreferredSize(new Dimension(600, preferredHeight + 20));
        logPanel.add(logScroll, BorderLayout.CENTER);

        JButton clearLogsBtn = new JButton("Limpiar Logs");
        clearLogsBtn.addActionListener(e -> {
            logTextPane.setText("");
            turnCounter = 0;
        });
        logPanel.add(clearLogsBtn, BorderLayout.SOUTH);

        frame.add(scroll, BorderLayout.CENTER);
        frame.add(controls, BorderLayout.EAST);
        frame.add(logPanel, BorderLayout.SOUTH);

        generate.addActionListener((ActionEvent e) -> {
            int w = Integer.parseInt(dimX.getText());
            int h = Integer.parseInt(dimY.getText());
            int batteryCapacity = (Integer) batteryCapacitySpinner.getValue();

            room = new Room(w, h);
            roomView.setRoom(room);
            manager = new RobotManager(room, new Position(0, 0), batteryCapacity);

            // Colocar cargador por defecto en (0,0) usando Strategy
            Position chargerPos = new Position(0, 0);
            room.addDynamicObstacle(new model.DynamicObstacle(chargerPos, new model.ChargerStrategy()));
            manager.getRobot().setCharger(chargerPos);

            // Inicializar y registrar observadores del patrón Observer
            eventLogger = new RobotEventLogger(true);
            statsObserver = new RobotStatisticsObserver();
            alertObserver = new RobotAlertObserver();

            manager.getRobot().addRobotObserver(eventLogger);
            manager.getRobot().addRobotObserver(statsObserver);
            manager.getRobot().addRobotObserver(alertObserver);

            // Observador especial para la GUI que registra en formato específico
            observer.RobotObserver guiLogObserver = event -> {
                String timestamp = getCurrentTime();
                String sensorOrigin = determineSensorOrigin(event);
                String signalType = event.getType().name();
                String currentState = manager.getRobot().getCurrentState() != null ?
                    manager.getRobot().getCurrentState().getClass().getSimpleName() : "NULL";
                String nextState = determineNextState(event);

                String logEntry = String.format("[%03d] - [%s] - [%s] - [%s] - %s -> %s\n",
                    ++turnCounter, timestamp, sensorOrigin, signalType, currentState, nextState);

                Color color = getColorForState(currentState, event.getType());

                SwingUtilities.invokeLater(() -> {
                    appendColoredLog(logEntry, color);
                });
            };
            manager.getRobot().addRobotObserver(guiLogObserver);

            System.out.println("=== Observadores registrados ===");
            System.out.println("Total: " + manager.getRobot().getRobotObserverCount());
            System.out.println("Capacidad de batería: " + batteryCapacity);

            roomView.setListener(new RoomView.RoomViewListener() {
                @Override
                public void onChargerPlaced(Position p) {
                    // Eliminar cargador anterior
                    Position oldCharger = room.getChargerPosition();
                    if (oldCharger != null) {
                        room.removeDynamicObstacleAt(oldCharger);
                    }
                    // Agregar nuevo cargador usando Strategy
                    room.addDynamicObstacle(new model.DynamicObstacle(p, new model.ChargerStrategy()));
                    manager.getRobot().setCharger(p);
                    System.out.println("⚡ Cargador colocado en " + p);
                    roomView.repaint();
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

                @Override
                public void onCatPlaced(Position p) {
                    room.addDynamicObstacle(new model.DynamicObstacle(p, new model.CatObstacleStrategy()));
                    System.out.println("🐱 Gato colocado en " + p);
                    roomView.repaint();
                }
            });

            frame.pack();
            updateSensorLabels();
            updateObserverLabels();
        });

        placeCharger.addActionListener(e -> {
            roomView.setPlacingCharger(placeCharger.isSelected());
            if (placeCharger.isSelected()) {
                placeObs.setSelected(false);
                placeCat.setSelected(false);
                roomView.setPlacingObstacles(false);
                roomView.setPlacingCat(false);
            }
        });

        placeObs.addActionListener(e -> {
            roomView.setPlacingObstacles(placeObs.isSelected());
            if (placeObs.isSelected()) {
                placeCharger.setSelected(false);
                placeCat.setSelected(false);
                roomView.setPlacingCharger(false);
                roomView.setPlacingCat(false);
            }
        });

        placeCat.addActionListener(e -> {
            roomView.setPlacingCat(placeCat.isSelected());
            if (placeCat.isSelected()) {
                placeCharger.setSelected(false);
                placeObs.setSelected(false);
                roomView.setPlacingCharger(false);
                roomView.setPlacingObstacles(false);
            }
        });

        startStop.addActionListener(e -> {
            if (timer != null && timer.isRunning()) {
                timer.stop();
                startStop.setText("Start");
            } else {
                int ms = 300;
                String sel = (String) speed.getSelectedItem();
                if (sel != null) {
                    if (sel.contains("100")) ms = 100;
                    if (sel.contains("600")) ms = 600;
                }
                timer = new Timer(ms, ev -> {
                    if (manager != null) {
                        manager.tick();
                        if (manager.getRobot() != null) {
                            roomView.setRobotPosition(manager.getRobot().getCurrent());
                            roomView.setPath(manager.getRobot().getPath());
                            updateSensorLabels();
                            updateObserverLabels();
                        }
                        roomView.repaint();
                    }
                });
                timer.start();
                startStop.setText("Stop");
            }
        });

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Generar por defecto
        generate.doClick();
    }

    private void updateSensorLabels() {
        if (manager == null) return;
        sensors.SensorReading fr = manager.getFrontReading();
        sensors.SensorReading lr = manager.getLeftReading();
        sensors.SensorReading rr = manager.getRightReading();
        frontSensorLabel.setText(formatReading(fr));
        leftSensorLabel.setText(formatReading(lr));
        rightSensorLabel.setText(formatReading(rr));

        // Actualizar batería
        if (manager.getRobot() != null) {
            int batteryPct = manager.getRobot().getBatteryPercentage();
            int batteryLevel = manager.getRobot().getBatteryLevel();
            int maxBattery = manager.getRobot().getMaxBattery();

            String batteryIcon = "🔋";
            Color batteryColor = new Color(50, 205, 50);

            if (batteryPct <= 20) {
                batteryIcon = "🪫";
                batteryColor = Color.RED;
            } else if (batteryPct <= 50) {
                batteryIcon = "🔋";
                batteryColor = Color.ORANGE;
            }

            batteryLabel.setText(String.format("%s Batería: %d/%d (%d%%)",
                batteryIcon, batteryLevel, maxBattery, batteryPct));
            batteryLabel.setForeground(batteryColor);

            // Actualizar barra de progreso
            batteryProgressBar.setValue(batteryPct);
            batteryProgressBar.setForeground(batteryColor);
            batteryProgressBar.setString(batteryLevel + "/" + maxBattery + " (" + batteryPct + "%)");
        }
    }

    private void updateObserverLabels() {
        if (manager == null) return;

        // Información de observadores de sensores
        StringBuilder sb = new StringBuilder();
        sb.append("Sensores-> Front:[").append(manager.getFrontObserversInfo()).append("] ");
        sb.append("Left:[").append(manager.getLeftObserversInfo()).append("] ");
        sb.append("Right:[").append(manager.getRightObserversInfo()).append("] | ");
        sb.append("Robot Observers: ").append(manager.getRobot().getRobotObserverCount());
        observersLabel.setText(sb.toString());

        // Estado del robot
        robot.Robot r = manager.getRobot();
        String stateInfo = "Estado: ";
        if (r.getCurrentState() != null) {
            stateInfo += r.getCurrentState().getClass().getSimpleName();
        } else {
            stateInfo += "(none)";
        }

        // Agregar estadísticas de observadores
        if (statsObserver != null) {
            stateInfo += String.format(" | Eventos: %d | Movimientos: %d | Gatos: %d",
                statsObserver.getStateChanges() + statsObserver.getPositionChanges(),
                statsObserver.getPositionChanges(),
                manager.getRoom().getDynamicObstacles().size());
        }

        robotStateLabel.setText(stateInfo);
    }

    private String formatReading(sensors.SensorReading r) {
        if (r == null) return "-";
        return r.getType().name() + ": dist=" + r.getDistance() +
               " obstacle=" + r.isObstacleDetected();
    }

    private String getCurrentTime() {
        java.time.LocalTime now = java.time.LocalTime.now();
        return String.format("%02d:%02d", now.getHour(), now.getMinute());
    }

    private String determineSensorOrigin(observer.RobotEvent event) {
        switch (event.getType()) {
            case BATTERY_LOW:
                return "BATTERY_SENSOR";
            case OBSTACLE_DETECTED:
                return "PROXIMITY_SENSOR";
            case POSITION_CHANGED:
                return "MOVEMENT_SYSTEM";
            case STATE_CHANGED:
                return "STATE_MACHINE";
            case PATH_CALCULATED:
                return "PATHFINDING";
            case CLEANING_COMPLETED:
                return "CLEANING_SYSTEM";
            case RETURNED_TO_CHARGER:
                return "NAVIGATION";
            default:
                return "ROBOT_SYSTEM";
        }
    }

    private String determineNextState(observer.RobotEvent event) {
        if (event.getType() == observer.RobotEvent.Type.STATE_CHANGED) {
            return event.getData() != null ? event.getData().toString() : "UNKNOWN";
        }

        // Predecir siguiente estado basado en el evento
        switch (event.getType()) {
            case BATTERY_LOW:
                return "ReturningState";
            case POSITION_CHANGED:
                return "CleaningState (continúa)";
            case OBSTACLE_DETECTED:
                return "RecalculatingState";
            case PATH_CALCULATED:
                return "MovingState";
            case CLEANING_COMPLETED:
                return "IdleState";
            case RETURNED_TO_CHARGER:
                return "ChargingState";
            default:
                return "(sin cambio)";
        }
    }

    private Color getColorForState(String currentState, observer.RobotEvent.Type eventType) {
        // Verificar si es un evento de "no hay camino"
        if (eventType == observer.RobotEvent.Type.PATH_CALCULATED && currentState.contains("Recalculating")) {
            return Color.RED; // Rojo para "No se encuentra camino"
        }

        // Colores según el estado actual
        if (currentState.contains("CleaningState")) {
            return new Color(0, 150, 0); // Verde oscuro
        } else if (currentState.contains("RecalculatingState")) {
            return new Color(138, 43, 226); // Violeta (BlueViolet)
        } else if (currentState.contains("ReturningState")) {
            return new Color(218, 165, 32); // Amarillo dorado (Goldenrod)
        } else if (currentState.contains("ChargingState")) {
            return new Color(255, 140, 0); // Naranja (DarkOrange)
        } else if (currentState.contains("IdleState")) {
            return Color.GRAY; // Gris para idle
        }

        // Color por defecto
        return Color.BLACK;
    }

    private void appendColoredLog(String text, Color color) {
        try {
            javax.swing.text.StyledDocument doc = logTextPane.getStyledDocument();
            javax.swing.text.Style style = logTextPane.addStyle("ColorStyle", null);
            javax.swing.text.StyleConstants.setForeground(style, color);
            doc.insertString(doc.getLength(), text, style);
            logTextPane.setCaretPosition(doc.getLength());
        } catch (javax.swing.text.BadLocationException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new MainApp();
    }
}

