package de.lmu.ifi.sep.abalone.components;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * This is an implementation of the {@code Map} interface using a HashMap as the
 * as the backbone data structure. All methods that change data contain in the
 * HashMap, invoke a call to the {@code notifyObservers} method that informs
 * all Observers of this Map of changes that occurred.
 * <p>
 * <p><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access this map concurrently, and at least
 * one of the threads modifies the map structurally, it <em>must</em> be
 * synchronized externally.
 *
 * @param <K> Type of keys maintained by this map.
 * @param <V> Type of mapped values that are held in the {@code HashMap}.
 * @implNote The Observer held by this map needs to override the methods contained
 * in the {@code EntryObserver<K,V>} interface.
 * @see Collection
 * @see Map
 * @see HashMap
 */
public class ObservableMap<K, V> implements Map<K, V> {

    /**
     * Backbone data structure.
     */
    private final HashMap<K, V> innerMap;

    /**
     * List containing the Observers that need to be notified of changes.
     */
    private final List<EntryObserver<K, V>> observer = new ArrayList<>();

    /**
     * Flag that tracks whether this HashMap has changes that the Observer
     * has not been notified of.
     */
    private boolean changed = false;

    /**
     * Tracks how many changes has occurred in this HashMap for Synchronization
     * purposes.
     */
    private int changes = 0;

    /**
     * Constructs an empty HashMap.
     */
    public ObservableMap() {
        innerMap = new HashMap<>();
    }

    /**
     * Constructs an empty HashMap with a specified initial capacity.
     *
     * @param initialCapacity The initial capacity
     * @throws IllegalArgumentException if the initial capacity is negative
     */
    public ObservableMap(int initialCapacity) {
        innerMap = new HashMap<>(initialCapacity);
    }

    /**
     * Constructs an empty HashMap with a specified initial capacity and load
     * factor.
     *
     * @param initialCapacity The initial capacity
     * @param loadFactor      The load factor
     * @throws IllegalArgumentException if the initial capacity or load factor
     *                                  is negative.
     */
    public ObservableMap(int initialCapacity, float loadFactor) {
        innerMap = new HashMap<>(initialCapacity, loadFactor);
    }

    /**
     * Constructs a new HashMap with values from the passed parameter Map.
     * Allows user to copy any map, producing an equivalent ObservableHashMap.
     * If this map has Observers, informs them of all Entries that are now held
     * in this mapping.
     *
     * @param m The map whose Key-Value pairs are to be placed in this map.
     * @throws NullPointerException if the specified map is null
     */
    public ObservableMap(Map<K, V> m) {
        innerMap = new HashMap<>(m);
        for (Entry<K, V> e : m.entrySet()) {
            setChanged();
            notifyObservers(e.getKey(), e.getValue());
        }
    }

    //*************** Query operations*******************//

    /**
     * Returns number of key-value mappings in the HashMap.
     *
     * @return number of key-value mappings in this HashMap
     */
    public int size() {
        return innerMap.size();
    }

    /**
     * Returns {@code true} if this map contains no key-value mappings.
     *
     * @return true if HashMap contains no key-value mappings, otherwise false.
     */
    public boolean isEmpty() {
        return innerMap.isEmpty();
    }

    /**
     * Returns {@code true} if the HashMap contains a mapping for specified
     * key.
     *
     * @param key Key whose presence in HashMap is to be tested.
     * @return true If HashMap contains a mapping for specified key, otherwise
     * false.
     * @throws NullPointerException if specified key is null and this HashMap
     *                              does not permit null keys
     * @throws ClassCastException   if key is inappropriate type for HashMap
     */
    public boolean containsKey(Object key) {
        return innerMap.containsKey(key);
    }

    /**
     * Returns {@code true} if this map contains a mapping for specified
     * value.
     *
     * @param value Object whose presence in HashMap is to be tested.
     * @return true If HashMap contains a mapping for the specified value,
     * otherwise false.
     * @throws ClassCastException   if value is of an inappropriate type for
     *                              HashMap
     * @throws NullPointerException if specified value is null and HashMap
     *                              does not permit null values
     */
    public boolean containsValue(Object value) {
        return innerMap.containsValue(value);
    }

    /**
     * Returns value to which the specified key is mapped in innerMap HashMap,
     * or {@code null} if this map contains no mapping for key.
     *
     * @param key Key whose associated value is to be returned if present in
     *            HashMap.
     * @throws NullPointerException if specified key is null and HashMap does
     *                              not permit null keys
     * @throws ClassCastException   if key is an inappropriate type for HashMap
     */
    public V get(Object key) {
        return innerMap.get(key);
    }

    //******************** Modification Operations *******************/   
    //*******************     Notifies Observers    *******************/ 

    /**
     * Associates specified value with specified key in {@code innerMap}
     * HashMap. If HashMap previously contained a mapping for this key,
     * old value is replaced by the specified value and returned. Notifies
     * observers of change.
     *
     * @param key   Key the specified value is to be associated with.
     * @param value Value to be associated with specified key.
     * @return Old value associated with key in the HashMap. Returns
     * <tt>null</tt> if there was no mapping for key. Can also indicate
     * that the HashMap previously associated <tt>null</tt> with key,
     * if implementation supports <tt>null</tt> values.
     * @throws ClassCastException       if the class of the specified key or value
     *                                  prevents it from being stored in this map
     * @throws NullPointerException     if the specified key or value is null
     *                                  and this map does not permit null keys or values
     * @throws IllegalArgumentException if some property of the specified key
     *                                  or value prevents it from being stored in this map
     */
    public synchronized V put(K key, V value) {
        V oldValue = innerMap.put(key, value);
        if (!observer.isEmpty() && oldValue != null) {
            setChanged();
            notifyObservers(key, value);
        }
        return oldValue;
    }

    /**
     * Removes mapping for specified key from HashMap if it is present.
     * Returns value previously associated the key, or <tt>null</tt> if
     * no mapping existed for key.
     * <p>
     * <p>If HashMap permits null values, then a return value of
     * <tt>null</tt> does not <i>necessarily</i> indicate that the map
     * contained no mapping for the key; it's also possible that the map
     * explicitly mapped the key to <tt>null</tt>.
     *
     * @param key Object whose mapping is to be removed from HashMap
     * @return Value associated with key, or <tt>null</tt>.
     * @throws ClassCastException   if key is an inappropriate type for HashMap.
     * @throws NullPointerException if specified key is null and HashMap does
     *                              not permit null keys
     */
    public V remove(Object key) {
        V value = innerMap.remove(key);
        if (value != null && !observer.isEmpty()) {
            setChanged();
            notifyObservers((K) key, null);
        }
        return value;
    }

    /**
     * Applies BiConsumer to all mappings from HashMap replacing the values
     * with the calculated values if they differ. Notifies observers of this
     * class of all changes that occurred.
     *
     * @param m Mapping of all Key-Value pairs that will be added to HashMap.
     */
    public synchronized void putAll(Map<? extends K, ? extends V> m) {
        innerMap.putAll(m);
        for (Entry<? extends K, ? extends V> e : m.entrySet()) {
            setChanged();
            notifyObservers(e.getKey(), e.getValue());
        }
    }

    /**
     * Removes all of the mappings from HashMap map and notifies observers.
     * HashMap will be empty after this call returns.
     */
    public synchronized void clear() {
        innerMap.clear();
        setChanged();
        notifyObservers(null, null);
    }

    // Bulk Operations

    /**
     * Returns a {@link Set} view of the keys contained in this map.
     * Does not accept any modifications.
     *
     * @return set view of the keys contained in this map
     */
    public Set<K> keySet() {
        return new HashSet<>(innerMap.keySet());
    }

    /**
     * Returns a {@link Collection} view of the values contained in this map.
     * Does not accept any modifications.
     *
     * @return Collection view of the values contained in this map
     */
    public Collection<V> values() {
        return new ArrayList<>(innerMap.values());
    }

    /**
     * Returns a {@link Set<Entry>} view of the values contained in this map.
     * Does not accept any modifications.
     *
     * @return Set view of innerMap.
     */
    public Set<Entry<K, V>> entrySet() {
        return innerMap.entrySet();
    }

    /**
     * Applies BiConsumer to all mappings from HashMap replacing the values
     * with the calculated values if they differ. Notifies observers of this
     * class of all changes that occurred.
     *
     * @param action Action that will be applied to all values of HashMap.
     */
    public synchronized void forEach(BiConsumer<? super K, ? super V> action) {
        if (action == null)
            throw new NullPointerException();
        int mc = changes;
        for (Entry<K, V> e : innerMap.entrySet()) {
            V before = e.getValue();
            action.accept(e.getKey(), e.getValue());
            V after = e.getValue();
            if (before != after) {
                setChanged();
                notifyObservers(e.getKey(), e.getValue());
            }
        }
        if (changes != mc) {
            throw new ConcurrentModificationException();
        }
    }

    //*********************** Views ************************//

    /**
     * Replaces all mappings from HashMap for which when the passed BiFunction
     * is applied, returns true. Notifies observers of all changes that occurred.
     *
     * @param function The function that will be applied to all values of the HashMap.
     */
    public synchronized void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        if (function == null)
            throw new NullPointerException();
        int mc = changes;
        for (Entry<K, V> e : innerMap.entrySet()) {
            V before = e.getValue();
            V after = function.apply(e.getKey(), e.getValue());
            if (before != after) {
                innerMap.replace(e.getKey(), after);
                setChanged();
                notifyObservers(e.getKey(), after);
            }
        }
        if (changes != mc)
            throw new ConcurrentModificationException();
    }

    /**
     * Calls above {@code replace} method but returns {@code boolean} if
     * replacement occurred.
     *
     * @param key      Key of the value being changed in HashMap.
     * @param oldValue Value expected to be associated with specified key.
     * @param newValue New value that the entry associated with the key is being changed
     *                 to.
     * @throws ClassCastException       if class of specified key or value
     *                                  prevents it from being stored in HashMap
     * @throws NullPointerException     if specified key or newValue is null,
     *                                  and HashMap does not permit null keys or values
     * @throws NullPointerException     if oldValue is null and this map does not
     *                                  permit null values
     * @throws IllegalArgumentException if some property of a specified key
     *                                  or value prevents it from being stored in HashMap.
     * @see java.util.Map
     * @see java.util.HashMap
     */
    public synchronized boolean replace(K key, V oldValue, V newValue) {
        boolean replaced = false;
        if (innerMap.get(key) == oldValue) {
            innerMap.replace(key, newValue);
            replaced = true;
        }
        return replaced;
    }

    /**
     * Calls {@link java.util.HashMap} {@code replace} method and notifies
     * Observers of this ObserverMap of changes
     * Observer of this entry exists.
     *
     * @param key   Key of the value being changed in HashMap.
     * @param value New value that the entry associated with the key is being changed
     *              to.
     * @throws ClassCastException       if class of specified key or value
     *                                  prevents it from being stored in HashMap
     * @throws NullPointerException     if specified key or newValue is null,
     *                                  and HashMap does not permit null keys or values
     * @throws NullPointerException     if oldValue is null and this map does not
     *                                  permit null values
     * @throws IllegalArgumentException if some property of a specified key
     *                                  or value prevents it from being stored in HashMap.
     * @see java.util.Map
     * @see java.util.HashMap
     */
    public synchronized V replace(K key, V value) {
        V curValue = innerMap.replace(key, value);
        setChanged();
        notifyObservers(key, value);
        return curValue;
    }

    //***************Observer Pattern starts******************//

    /**
     * Adds an observer to the set of observers for this object, provided that
     * it is not the same as an observer already in the set. The order in
     * which notifications will be delivered to multiple observers is not
     * specified. See the class comment.
     *
     * @param obs Observer to be added to the list of Objects that want to be
     *            informed on any changes that occur.
     */
    public void addObserver(EntryObserver<K, V> obs) {
        if (!observer.contains(obs)) {
            observer.add(obs);
        }
    }

    /**
     * Returns List of {@code EntryObservers}.
     *
     * @return List of Observers.
     */
    public List<EntryObserver<K, V>> getObservers() {
        return observer;
    }

    /**
     * Deletes all observers of this ObservableMap.
     */
    public boolean removeObservers() {
        observer.clear();
        return observer.isEmpty();
    }

    /**
     * Called every time an entry in the HashMap has changed and informs
     * Observers of this map of the changes over the {@code entryChanged}
     * method.
     *
     * @param key   Key associated with the changed value.
     * @param value The new value from the entry that has changed.
     * @see EntryObserver
     */
    private void notifyObservers(K key, V value) {
        changes--;
        if (!changed) {
            return;
        }
        if (!observer.isEmpty()) {
            for (EntryObserver<K, V> e : observer) {
                e.entryChanged(key, value);
            }
        }
        clearChanged();
    }

    /**
     * Marks this <tt>Observable</tt> object as having changed; the
     * <tt>hasChanged</tt> method will now return <tt>true</tt>.
     */
    private void setChanged() {
        changes++;
        changed = true;
    }

    /**
     * Indicates that this object has no longer changed, or that it has already
     * notified its observer of the most recent change, so that the
     * <tt>hasChanged</tt> method will now return <tt>false</tt>. This method is
     * called automatically by the <code>notifyObservers</code> method.
     */
    private void clearChanged() {
        if (changes == 0) {
            changed = false;
        }
    }

    /**
     * Tests if this object has changed.
     *
     * @return <code>true</code> if and only if the <code>setChanged</code>
     * method has been called more recently than the
     * <code>clearChanged</code> method on this object;
     * <code>false</code> otherwise.
     */
    public boolean hasChanged() {
        return changed;
    }

}
