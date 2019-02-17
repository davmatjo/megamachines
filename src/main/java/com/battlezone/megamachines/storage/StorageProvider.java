package com.battlezone.megamachines.storage;

import com.battlezone.megamachines.math.Vector3f;

/**
 * an interface to define a provider capable of storing and retrieving data in a key-value pair format
 */
public abstract class StorageProvider {

    public abstract void setValue(String key, String value);

    public abstract void setValue(String key, int value);

    public abstract void setValue(String key, float value);

    public abstract void setValue(String key, double value);

    public abstract void setValue(String key, long value);

    public abstract void setValue(String key, boolean value);

    public void setValue(String key, Vector3f value) {
        setValue(key + "x", value.x);
        setValue(key + "y", value.y);
        setValue(key + "z", value.z);
    }

    public abstract String getString(String key, String def);

    public abstract int getInt(String key, int def);

    public abstract float getFloat(String key, float def);

    public abstract double getDouble(String key, double def);

    public abstract long getLong(String key, long def);

    public abstract boolean getBoolean(String key, boolean def);

    public Vector3f getVector3f(String key, Vector3f def) {
        float x = getFloat(key + "x", def.x);
        float y = getFloat(key + "y", def.y);
        float z = getFloat(key + "z", def.z);
        return new Vector3f(x, y, z);
    }

    public abstract void clearAll();

    public abstract void save();

    public abstract void reload();
}
