package org.gortz.greeniot.smartcityiot.dto.listitems;

import java.util.Map;

import lombok.AllArgsConstructor;

/**
 * A key value container for Spinners
 *
 * @param <K> Set key type
 * @param <V> Set value type
 */
@AllArgsConstructor(suppressConstructorProperties = true)
public class SpinnerItemEntry<K,V> implements Map.Entry {
    /**
     * Key
     */
    private K key;

    /**
     * Value
     */
    private V value;

    /**
     * Get key
     *
     * @return key
     */
    @Override
    public K getKey() {
        return key;
    }

    /**
     * Get value
     *
     * @return value
     */
    @Override
    public V getValue() {
        return value;
    }

    /**
     * Set value
     * @param value Set new value
     * @return Old value
     */
    @Override
    public V setValue(Object value) {
        V tempValue = this.value;
        this.value = (V)value;
        return tempValue;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
