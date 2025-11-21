# DAP-Roomba (Skeleton)

Este repositorio contiene el esqueleto de un simulador de Roomba en Java usando Swing para la interfaz gráfica y patrones Observador/Estado.

Requisitos
- JDK 11+ (recomendado 17)

Compilar y ejecutar
- Ejecutar la app:

```bash
./run.sh
```

- Ejecutar en modo debug (escucha JDWP en puerto 5005):

```bash
./run-debug.sh
```

IDE (IntelliJ)
- Importa el proyecto como proyecto Java simple (sin JavaFX).
- Ejecuta la clase `gui.MainApp` o usa el script `run.sh`.

Archivos importantes
- `src/gui/MainApp.java` — App Swing y controles UI.
- `src/gui/RoomView.java` — componente Swing que dibuja la rejilla, la Roomba y la ruta.
- `src/robot/RobotManager.java` — coordinador de sensores y robot (esqueleto).
- `src/pathfinding/AStar.java` — implementación A* (básica).
- `TO-DO.md` — tareas pendientes.

Siguientes pasos recomendados
- Implementar la lógica completa en `RobotManager` (selección de objetivos, registro de sensores, detección de inaccesibles)
- Terminar los estados del robot (`robot.state`)
- Mejorar la interfaz (mostrar porcentajes, indicadores de estado)
