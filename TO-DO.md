# TO-DO

Esqueleto inicial del proyecto Roomba Observador+Estado

## Paquetes creados
- model
- observer
- sensors
- robot
- robot.state
- pathfinding
- gui

## Archivos generados (skeleton)
- src/model/Position.java
- src/model/Cell.java
- src/model/Room.java
- src/observer/Observer.java
- src/observer/Subject.java
- src/sensors/SensorReading.java
- src/sensors/SimulatedProximitySensor.java
- src/robot/Robot.java
- src/robot/RobotManager.java
- src/robot/state/RobotState.java
- src/robot/state/IdleState.java
- src/robot/state/MovingState.java
- src/robot/state/RecalculatingState.java
- src/robot/state/CleaningState.java
- src/pathfinding/AStar.java
- src/gui/RoomView.java
- src/gui/MainApp.java

## Próximas tareas (prioridad alta)
- [ ] Implementar selección de posición inicial mediante clic (diferenciar entre colocar cargador y colocar obstaculos)
- [ ] Finalizar integración RobotManager: selección de siguiente objetivo no limpiado
- [ ] Implementar lógica completa de estados y transiciones
- [ ] Manejar áreas inaccesibles y marcar objetivos inalcanzables
- [ ] Mejorar dirección/orientación del robot y sensores (definir orientación y rotaciones)
- [ ] Añadir visualización de la roomba en `RoomView` y de la ruta calculada
- [ ] Añadir tests unitarios para `AStar` y `Room` (básicos)

## Próximas tareas (media/baja)
- [ ] Refinar heurística A* para preferir celdas no limpiadas
- [ ] Añadir controles para velocidad extra o presets adicionales
- [ ] Soporte para guardar/cargar mapas (opcional)

## Notas
- UI programática JavaFX.
- Velocidades presets: 100/300/600 ms.
- El cargador actúa como inicio y meta final.



