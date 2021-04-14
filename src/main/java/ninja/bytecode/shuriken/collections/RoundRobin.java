package ninja.bytecode.shuriken.collections;

/**
 * Works like an iterator except it loops back to the start when the last entry is hit. Round robins do not store their own data which means they access the data source which may or may not change. Changes in the parent source are reflected here.
 * @param <T> the type
 */
public interface RoundRobin<T> {
    public boolean hasNext();

    public boolean isEmpty();

    /**
     * Gets the next element in the order
     * @return the next element, or null if the data source is empty
     */
    public T next();
}
