/**
 * Punto de entrada principal del simulador Roomba.
 * Implementa el patrón Observer y Strategy.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== Iniciando Simulador Roomba ===");
        System.out.println("Patrón Observer + Strategy implementados");
        System.out.println("Características:");
        System.out.println("  - Roomba se mueve automáticamente");
        System.out.println("  - Celdas sucias (marrón) → limpias (verde)");
        System.out.println("  - Gatos que se mueven y ensucian");
        System.out.println("  - 3 Observadores activos: Logger, Statistics, Alerts");
        System.out.println();

        // Ejecutar la GUI
        new gui.MainApp();
    }
}