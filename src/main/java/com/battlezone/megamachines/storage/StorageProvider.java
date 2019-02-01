package com.battlezone.megamachines.storage;

/**
 * an interface to define a provider capable for storing and retrieving data in a key-value pair format
 */
public interface StorageProvider {

    void setValue(String key, String value);

    void setValue(String key, int value);

    void setValue(String key, float value);

    void setValue(String key, double value);

    void setValue(String key, long value);

    void setValue(String key, boolean value);

    String getString(String key, String def);

    int getInt(String key, int def);

    float getFloat(String key, float def);

    double getDouble(String key, double def);

    long getLong(String key, long def);

    boolean getBoolean(String key, boolean def);

    void clearAll();
}
