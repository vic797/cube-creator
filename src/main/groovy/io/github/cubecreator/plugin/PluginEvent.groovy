package io.github.cubecreator.plugin

/**
 * An event parameter sent to plugins when the event is triggered by the Workbench
 * @param <T> The object sent with this event
 */
class PluginEvent<T> {

    final T object

    /**
     * Constructor for this event
     * @param object The object to be sent
     */
    PluginEvent(T object) {
        this.object = object;
    }

}
