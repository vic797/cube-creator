package io.github.cubecreator.plugin

/**
 * This is the base class for all plugins. The <code>entry</code> class must extend this class.
 *
 * Plugins get a {@link PluginContext} exclusive to this plugin. This context allows the plugin to actually work
 * by providing utility functions, settings and information from the <code>plugin-info.json</code> file
 *
 * @see PluginContext
 * @see PluginInfo
 * @see PluginSettings
 */
class Plugin {

    /**
     * Keeps all the information about this plugin <code>plugin-info.json</code> file
     */
    PluginInfo info

    /**
     * This method is called when the plugin is loaded. All plugins are loaded before the main window is shown
     * @param context The context of this plugin
     */
    void load(PluginContext context) {}

    /**
     * This method is called when a plugin is unloaded. Should be used in case any cleanups need to be done
     * @param context the context of this plugin
     */
    void unload(PluginContext context) {}

    /**
     * This function is called when a file is opened by the Workbench
     * @param event The event parameters
     */
    void onFileOpened(PluginEvent<File> event) {}

    /**
     * This function is called when a file is closed by the Workbench
     * @param event The event parameters
     */
    void onFileClosed(PluginEvent<File> event) {}

    /**
     * This function is called when a file is saved by the Workbench
     * @param event The event parameters
     */
    void onFileSaved(PluginEvent<File> event) {}

    /**
     * This function is called when a data pack or resource pack is loaded by the Workbench
     * @param event The event parameters
     */
    void onPackOpened(PluginEvent<File> event) {}

    /**
     * This function is called when a data pack or resource pack is loaded by the Workbench
     * @param event The event parameters
     */
    void onPackClosed(PluginEvent<File> event) {}

    /**
     * This function is called when a message is received by this plugin. Messages are sent by {@link PluginContext#broadcast(PluginMessage)}
     *
     * @see PluginMessage
     * @param message the message received by this plugin
     */
    void onMessageReceived(PluginMessage message) {}

    void editSettings() {}

}