# Patrón Observer - Implementación Roomba

## Descripción General

Este proyecto implementa el **Patrón de Diseño Observer** aplicado a un sistema de simulación de un robot aspirador (Roomba). El patrón Observer permite que múltiples objetos observadores sean notificados automáticamente cuando el estado del robot cambia, sin que el robot necesite conocer los detalles específicos de sus observadores.

## Estructura del Patrón Observer

### Interfaces Base

#### 1. **Observer** (`observer/Observer.java`)
Interface básica para observadores de sensores.
```java
public interface Observer {
    void update(SensorReading reading);
}
```

#### 2. **Subject** (`observer/Subject.java`)
Interface para objetos observables.
```java
public interface Subject {
    void register(Observer o);
    void unregister(Observer o);
    void notifyObservers(Object event);
}
```

#### 3. **RobotObserver** (`observer/RobotObserver.java`)
Interface especializada para observadores del robot.
```java
public interface RobotObserver {
    void onRobotEvent(RobotEvent event);
}
```

### Clases de Eventos

#### **RobotEvent** (`observer/RobotEvent.java`)
Encapsula información sobre eventos del robot:
- **Tipos de eventos**:
  - `STATE_CHANGED`: El robot cambió de estado
  - `POSITION_CHANGED`: El robot se movió a una nueva posición
  - `PATH_CALCULATED`: Se calculó una nueva ruta
  - `BATTERY_LOW`: Nivel de batería bajo
  - `CLEANING_COMPLETED`: Limpieza completada
  - `OBSTACLE_DETECTED`: Se detectó un obstáculo
  - `RETURNED_TO_CHARGER`: Robot regresó al cargador

### Observadores Concretos

#### 1. **RobotEventLogger** (`observer/RobotEventLogger.java`)
Registra todos los eventos del robot en un log.
- Mantiene un historial de eventos
- Puede imprimir eventos en consola
- Genera resúmenes estadísticos por tipo de evento

**Características**:
```java
- getEventLog(): Lista de todos los eventos
- getEventCount(): Total de eventos registrados
- getEventCountByType(Type): Eventos por tipo específico
- printSummary(): Imprime resumen de eventos
```

#### 2. **RobotStatisticsObserver** (`observer/RobotStatisticsObserver.java`)
Recopila estadísticas detalladas sobre el comportamiento del robot.

**Métricas recopiladas**:
- Número de cambios de estado
- Número de cambios de posición
- Cálculos de ruta realizados
- Obstáculos detectados
- Advertencias de batería
- Limpiezas completadas
- Retornos al cargador
- Distancia total recorrida (Manhattan)

**Características**:
```java
- printStatistics(): Muestra todas las estadísticas
- getTotalDistance(): Distancia total recorrida
- reset(): Reinicia todas las estadísticas
```

#### 3. **RobotAlertObserver** (`observer/RobotAlertObserver.java`)
Muestra alertas críticas del robot en tiempo real.

**Alertas manejadas**:
- ⚠️ Batería baja
- ⚠️ Obstáculo detectado
- ✓ Limpieza completada
- ✓ Robot en cargador

**Características**:
```java
- isBatteryAlertActive(): Estado de alerta de batería
- getAlertCount(): Total de alertas generadas
- reset(): Reinicia el sistema de alertas
```

## Sujetos (Subjects)

### 1. **SimulatedProximitySensor** (`sensors/SimulatedProximitySensor.java`)
Implementa `Subject` para notificar lecturas de sensores.
- Sensores: FRONT, LEFT, RIGHT
- Notifica al Robot cuando detecta obstáculos

### 2. **Robot** (`robot/Robot.java`)
Implementa `Observer` (para sensores) y actúa como Subject (para eventos propios).

**Como Observer**:
- Recibe notificaciones de los sensores de proximidad
- Reacciona a detecciones de obstáculos

**Como Subject**:
- Notifica cambios de estado
- Notifica cambios de posición
- Notifica eventos importantes (batería, limpieza, etc.)

**Métodos clave**:
```java
// Gestión de observadores
- addRobotObserver(RobotObserver): Registra un observador
- removeRobotObserver(RobotObserver): Elimina un observador
- getRobotObserverCount(): Cuenta de observadores registrados

// Notificaciones específicas
- notifyBatteryLow(): Notifica batería baja
- notifyCleaningCompleted(): Notifica limpieza completada
- notifyReturnedToCharger(): Notifica retorno al cargador
```

## Flujo de Eventos

```
1. Robot se mueve → notifica POSITION_CHANGED → Observadores actualizan
2. Sensor detecta obstáculo → notifica Robot → Robot cambia estado → notifica STATE_CHANGED
3. Robot calcula ruta → notifica PATH_CALCULATED → Observadores registran
4. Batería baja → notifica BATTERY_LOW → Alerta se activa
5. Robot completa limpieza → notifica CLEANING_COMPLETED → Estadísticas actualizan
```

## Ventajas de esta Implementación

1. **Desacoplamiento**: El Robot no necesita conocer los detalles de sus observadores
2. **Extensibilidad**: Fácil agregar nuevos tipos de observadores sin modificar el Robot
3. **Múltiples observadores**: Varios observadores pueden reaccionar al mismo evento
4. **Registro dinámico**: Los observadores se pueden agregar/eliminar en tiempo de ejecución
5. **Especialización**: Diferentes observadores para diferentes propósitos (logging, estadísticas, alertas)

## Cómo Usar

### Ejemplo Básico

```java
// Crear robot
Room room = new Room(10, 10);
Robot robot = new Robot(room, new Position(0, 0));

// Crear observadores
RobotEventLogger logger = new RobotEventLogger(true);
RobotStatisticsObserver stats = new RobotStatisticsObserver();
RobotAlertObserver alerts = new RobotAlertObserver();

// Registrar observadores
robot.addRobotObserver(logger);
robot.addRobotObserver(stats);
robot.addRobotObserver(alerts);

// El robot genera eventos automáticamente
robot.setCurrent(new Position(1, 1)); // Notifica POSITION_CHANGED
robot.notifyBatteryLow(); // Notifica BATTERY_LOW

// Ver estadísticas
stats.printStatistics();
logger.printSummary();
```

### Ejecutar Demo

Para ejecutar la demostración completa del patrón Observer:

```bash
# Compilar
javac src/observer/ObserverDemo.java

# Ejecutar
java -cp src observer.ObserverDemo
```

## Diagrama de Clases

```
┌─────────────┐         ┌──────────────┐
│   Subject   │◄────────│   Observer   │
└─────────────┘         └──────────────┘
      △                        △
      │                        │
      │                        ├──────────────────┐
      │                        │                  │
┌─────┴──────────────┐  ┌──────┴──────┐  ┌───────┴────────┐
│SimulatedProximity  │  │    Robot    │  │ RobotObserver  │
│     Sensor         │  │             │  │   (interface)  │
└────────────────────┘  └─────────────┘  └────────┬───────┘
                              │                   △
                              │                   │
                    notifica  │         ┌─────────┼──────────┐
                    eventos   │         │         │          │
                              ▼         │         │          │
                        ┌─────────┐     │         │          │
                        │RobotEvent│    │         │          │
                        └─────────┘     │         │          │
                                        │         │          │
                              ┌─────────┴──┐  ┌───┴────┐ ┌───┴─────┐
                              │EventLogger │  │Statistics│ │Alert   │
                              │            │  │Observer │ │Observer │
                              └────────────┘  └─────────┘ └─────────┘
```

## Extensiones Futuras

Posibles observadores adicionales que se pueden implementar:

1. **RobotUIObserver**: Actualiza interfaz gráfica en tiempo real
2. **RobotPersistenceObserver**: Guarda eventos en base de datos
3. **RobotNetworkObserver**: Envía eventos a servidor remoto
4. **RobotPerformanceObserver**: Analiza rendimiento y eficiencia
5. **RobotDebugObserver**: Información detallada para debugging

## Conclusión

Esta implementación del patrón Observer proporciona una arquitectura flexible y extensible para monitorear y reaccionar a los eventos del robot, facilitando el mantenimiento, testing y expansión del sistema.

