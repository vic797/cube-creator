package io.github.cubecreator.plugin

import groovy.transform.PackageScope
import io.github.cubecreator.util.Settings

/**
 * {@inheritDoc}
 */
class PluginSettings extends Settings {

    /**
     * The settings used by this plugin also require a {@link PluginContext} to access the plugin's files
     */
    final PluginContext context

    @PackageScope
    PluginSettings(PluginContext context) {
        this.context = context
    }

    /**
     * Loads the settings for this plugin
     */
    void loadSettings() {
        loadSettings(new File(context.info.location, "settings.properties"))
    }

    /**
     * Saves the settings for this plugin
     */
    void saveSettings() {
        saveSettings(new File(context.info.location, "settings.properties"))
    }
}
