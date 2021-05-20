package io.github.cubecreator.plugin

import io.github.cubecreator.util.AbstractObjectDescriptor

class PluginObject<T> extends AbstractObjectDescriptor<T> {

    final String pluginId

    PluginObject(T object, String id) {
        super(object)
        this.pluginId = id
    }

    @Override
    String describe() {
        return PluginManager.getInstance().getPlugin(pluginId).displayName
    }
}
