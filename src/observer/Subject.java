package observer;

/**
 * Subject básico para el patrón observador.
 */
public interface Subject {
    void register(Observer o);
    void unregister(Observer o);
    void notifyObservers(Object event);
}

