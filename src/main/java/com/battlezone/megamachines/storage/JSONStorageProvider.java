package com.battlezone.megamachines.storage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * An implementation of StorageProvider which uses JSON as the underlying data store
 */
public class JSONStorageProvider extends StorageProvider {

    private JSONObject storedSettings;

    JSONStorageProvider() {
        storedSettings = new JSONObject();
        reload();
    }

    private Path getPath() {
        return (new File("prefs.json").toPath());
    }

    @Override
    public void save() {
        // write the json to disk
        String jsonString = storedSettings.toString();
        try {
            Files.writeString((getPath()), jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reload() {
        // load in json from disk
        try {
            String json = Files.readString((getPath()));
            this.storedSettings = new JSONObject(json);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setValue(String key, String value) {
        storedSettings.put(key, value);
    }

    @Override
    public void setValue(String key, int value) {
        storedSettings.put(key, value);
    }

    @Override
    public void setValue(String key, float value) {
        storedSettings.put(key, value);
    }

    @Override
    public void setValue(String key, double value) {
        storedSettings.put(key, value);
    }

    @Override
    public void setValue(String key, long value) {
        storedSettings.put(key, value);
    }

    @Override
    public void setValue(String key, boolean value) {
        storedSettings.put(key, value);
    }

    @Override
    public String getString(String key, String def) {
        return storedSettings.optString(key, def);
    }

    @Override
    public int getInt(String key, int def) {
        return storedSettings.optInt(key, def);
    }

    @Override
    public float getFloat(String key, float def) {
        return storedSettings.optFloat(key, def);
    }

    @Override
    public double getDouble(String key, double def) {
        return storedSettings.optDouble(key, def);
    }

    @Override
    public long getLong(String key, long def) {
        return storedSettings.optLong(key, def);
    }

    @Override
    public boolean getBoolean(String key, boolean def) {
        return storedSettings.optBoolean(key, def);
    }

    @Override
    public void clearAll() {
        storedSettings = new JSONObject();
    }

}
