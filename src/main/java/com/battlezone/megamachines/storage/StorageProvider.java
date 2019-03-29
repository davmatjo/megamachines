package com.battlezone.megamachines.storage;

import com.battlezone.megamachines.math.Vector3f;

/**
 * an interface to define a provider capable of storing and retrieving data in a key-value pair format
 */
public abstract class StorageProvider {

    /**
     * Store a String in the users preferences
     *
     * @param key   The key used to later retrieve this data
     * @param value The data to store
     */
    public abstract void setValue(String key, String value);

    /**
     * Store an int in the users preferences
     *
     * @param key   The key used to later retrieve this data
     * @param value The data to store
     */
    public abstract void setValue(String key, int value);

    /**
     * Store a float in the users preferences
     *
     * @param key   The key used to later retrieve this data
     * @param value The data to store
     */
    public abstract void setValue(String key, float value);

    /**
     * Store a double in the users preferences
     *
     * @param key   The key used to later retrieve this data
     * @param value The data to store
     */
    public abstract void setValue(String key, double value);

    /**
     * Store a long in the users preferences
     *
     * @param key   The key used to later retrieve this data
     * @param value The data to store
     */
    public abstract void setValue(String key, long value);

    /**
     * Store a boolean in the users preferences
     *
     * @param key   The key used to later retrieve this data
     * @param value The data to store
     */
    public abstract void setValue(String key, boolean value);

    /**
     * Store a Vector3f in the users preferences
     *
     * @param key   The key used to later retrieve this data
     * @param value The data to store
     */
    public void setValue(String key, Vector3f value) {
        setValue(key + "x", value.x);
        setValue(key + "y", value.y);
        setValue(key + "z", value.z);
    }

    /**
     * Retrieve a String from the users preferences
     *
     * @param key The key used when storing the data
     */
    public abstract String getString(String key, String def);

    /**
     * Retrieve an int from the users preferences
     *
     * @param key The key used when storing the data
     */
    public abstract int getInt(String key, int def);

    /**
     * Retrieve a float from the users preferences
     *
     * @param key The key used when storing the data
     */
    public abstract float getFloat(String key, float def);

    /**
     * Retrieve a double from the users preferences
     *
     * @param key The key used when storing the data
     */
    public abstract double getDouble(String key, double def);

    /**
     * Retrieve a long from the users preferences
     *
     * @param key The key used when storing the data
     */
    public abstract long getLong(String key, long def);

    /**
     * Retrieve a boolean from the users preferences
     *
     * @param key The key used when storing the data
     */
    public abstract boolean getBoolean(String key, boolean def);

    /**
     * Retrieve a {@link Vector3f} from the users preferences
     *
     * @param key The key used when storing the data
     */
    public Vector3f getVector3f(String key, Vector3f def) {
        float x = getFloat(key + "x", def.x);
        float y = getFloat(key + "y", def.y);
        float z = getFloat(key + "z", def.z);
        return new Vector3f(x, y, z);
    }

    /**
     * Delete all data from the preferences
     */
    public abstract void clearAll();

    /**
     * Persist the preferences to disk
     */
    public abstract void save();

    /**
     * Reload preferences from disk. Unsaved changes will be lost.
     */
    public abstract void reload();
}
