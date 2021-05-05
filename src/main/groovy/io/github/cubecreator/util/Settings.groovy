package io.github.cubecreator.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.commons.io.FileUtils

/**
 * A wrapper around the Properties class that supports primitive types and arrays
 */
class Settings {

    static Settings get(String name) {
        Settings settings = new Settings()
        File f = new File(".", name)
        if (!f.exists()) {
            try {
                FileUtils.touch(f)
            } catch (e) {}
        }
        try {
            settings.loadSettings(f)
        } catch (e) {}
        settings.file = f
        return settings
    }

    private Properties properties
    private Gson gson
    private File file

    Settings() {
        properties = new Properties()
        gson = new Gson()
    }

    void loadSettings(InputStream stream) throws IOException {
        properties.load(stream)
    }

    void loadSettings(URL url) throws IOException {
        InputStream stream = url.openStream()
        loadSettings(stream)
        stream.close()
    }

    void loadSettings(File file) throws IOException {
        loadSettings(file.toURI().toURL())
    }

    void saveSettings(OutputStream stream) throws IOException {
        properties.store(stream, null)
    }

    void saveSettings(File file) throws IOException {
        FileOutputStream stream = new FileOutputStream(file)
        saveSettings(stream)
        stream.close()
    }

    void saveSettings() throws IOException, NullPointerException {
        if (file == null) {
            throw new NullPointerException()
        }
        saveSettings(file)
    }

    boolean has(String key) {
        return properties.hasProperty(key)
    }

    void put(String key, boolean value) {
        properties.setProperty(key, String.valueOf(value))
    }

    void put(String key, String value) {
        properties.setProperty(key, value)
    }

    void put(String key, String[] value) {
        properties.put(key, gson.toJson(value))
    }

    void put(String key, int value) {
        properties.put(key, String.valueOf(value))
    }

    void put(String key, int[] value) {
        properties.put(key, gson.toJson(value))
    }

    void put(String key, double value) {
        properties.put(key, String.valueOf(value))
    }

    void put(String key, double[] value) {
        properties.put(key, gson.toJson(value))
    }

    void put(String key, float value) {
        properties.put(key, String.valueOf(value))
    }

    void put(String key, float[] value) {
        properties.put(key, gson.toJson(value))
    }

    void put(String key, long value) {
        properties.put(key, String.valueOf(value))
    }

    void put(String key, long[] value) {
        properties.put(key, gson.toJson(value))
    }

    void put(String key, short value) {
        properties.put(key, String.valueOf(value))
    }

    void put(String key, short[] value) {
        properties.put(key, gson.toJson(value))
    }

    void put(String key, byte value) {
        properties.put(key, String.valueOf(value))
    }

    void put(String key, byte[] value) {
        properties.put(key, gson.toJson(value))
    }

    void put(String key, char value) {
        properties.put(key, String.valueOf(value))
    }

    void put(String key, char[] value) {
        properties.put(key, gson.toJson(value))
    }

    boolean get(String key, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(key, String.valueOf(defaultValue)))
    }

    String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue)
    }

    String[] get(String key, String[] defaultValue) {
        String array = properties.getProperty(key, null)
        if (array == null) {
            return defaultValue
        } else {
            return gson.fromJson(array, new TypeToken<String[]>() {}.getType())
        }
    }

    int get(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)))
        } catch(e) {
            return defaultValue
        }
    }

    Integer[] get(String key, Integer[] defaultValue) {
        String array = properties.getProperty(key, null)
        if (array == null) {
            return defaultValue
        } else {
            return gson.fromJson(array, new TypeToken<Integer[]>() {}.getType())
        }
    }

    double get(String key, double defaultValue) {
        try {
            return Double.parseDouble(properties.getProperty(key, String.valueOf(defaultValue)))
        } catch(e) {
            return defaultValue
        }
    }

    Double[] get(String key, Double[] defaultValue) {
        String array = properties.getProperty(key, null)
        if (array == null) {
            return defaultValue
        } else {
            return gson.fromJson(array, new TypeToken<Double[]>() {}.getType())
        }
    }

    float get(String key, float defaultValue) {
        try {
            return Float.parseFloat(properties.getProperty(key, String.valueOf(defaultValue)))
        } catch(e) {
            return defaultValue
        }
    }

    Float[] get(String key, Float[] defaultValue) {
        String array = properties.getProperty(key, null)
        if (array == null) {
            return defaultValue
        } else {
            return gson.fromJson(array, new TypeToken<Float[]>() {}.getType())
        }
    }

    long get(String key, long defaultValue) {
        try {
            return Long.parseLong(properties.getProperty(key, String.valueOf(defaultValue)))
        } catch(e) {
            return defaultValue
        }
    }

    Long[] get(String key, Long[] defaultValue) {
        String array = properties.getProperty(key, null)
        if (array == null) {
            return defaultValue
        } else {
            return gson.fromJson(array, new TypeToken<Long[]>() {}.getType())
        }
    }

    short get(String key, short defaultValue) {
        try {
            return Short.parseShort(properties.getProperty(key, String.valueOf(defaultValue)))
        } catch(e) {
            return defaultValue
        }
    }

    Short[] get(String key, Short[] defaultValue) {
        String array = properties.getProperty(key, null)
        if (array == null) {
            return defaultValue
        } else {
            return gson.fromJson(array, new TypeToken<Short[]>() {}.getType())
        }
    }

    byte get(String key, byte defaultValue) {
        try {
            return Byte.parseByte(properties.getProperty(key, String.valueOf(defaultValue)))
        } catch(e) {
            return defaultValue
        }
    }

    Byte[] get(String key, Byte[] defaultValue) {
        String array = properties.getProperty(key, null)
        if (array == null) {
            return defaultValue
        } else {
            return gson.fromJson(array, new TypeToken<Byte[]>() {}.getType())
        }
    }

    char get(String key, char defaultValue) {
        try {
            return properties.getProperty(key, String.valueOf(defaultValue)).charAt(0)
        } catch(e) {
            return defaultValue
        }
    }

    Character[] get(String key, Character[] defaultValue) {
        String array = properties.getProperty(key, null)
        if (array == null) {
            return defaultValue
        } else {
            return gson.fromJson(array, new TypeToken<Character[]>() {}.getType())
        }
    }

}
