package io.github.cubecreator.plugin

/**
 * A message sent by a plugin to another plugin. This message might include extra information.
 */
class PluginMessage {

    private HashMap<String, Object> extras;

    /**
     * The action name
     */
    final String action
    /**
     * The target plugin ID. Use this in case a plugin must send a message to a specific plugin
     */
    String targetId

    /**
     * Constructor
     * @param action The action name
     */
    PluginMessage(String action) {
        extras = new HashMap<>()
        this.action = action
    }

    void putExtra(String name, String value) {
        extras.put(name, value)
    }

    void putExtra(String name, Number value) {
        extras.put(name, value)
    }

    void putExtra(String name, Boolean value) {
        extras.put(name, value)
    }

    void putExtra(String name, Object value) {
        extras.put(name, value)
    }

    String getStringExtra(String name) {
        return (String) extras.get(name)
    }

    Number getNumberExtra(String name) {
        return (Number) extras.get(name)
    }

    String getBooleanExtra(String name) {
        return (Boolean) extras.get(name)
    }

    Object getObjectExtra(String name) {
        return extras.get(name)
    }

}
