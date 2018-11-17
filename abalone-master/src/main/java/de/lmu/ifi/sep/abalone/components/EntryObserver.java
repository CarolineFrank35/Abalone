package de.lmu.ifi.sep.abalone.components;

/**
 * Interface enabling the Observer pattern with Entry in ObservableMap Class.
 *
 * @param <K> Type of key mapping in Entry.
 * @param <V> Type of value mapping in Entry.
 */
public interface EntryObserver<K, V> {

    /**
     * Must be implemented by the Observer so that it is informed
     * every time a entry key-value pair changes.
     *
     * @param key   Key of the entry that has changed.
     * @param value Value of the entry that has changed.
     */
    void entryChanged(K key, V value);

}
