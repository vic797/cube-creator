//file:noinspection GrMethodMayBeStatic
package io.github.cubecreator.plugin

import io.github.cubecreator.ui.editor.CodeEditor
import io.github.cubecreator.ui.editor.EditorManager
import io.github.cubecreator.ui.menu.MenuBuilder
import io.github.cubecreator.util.EventTransport
import io.github.cubecreator.util.Utils
import org.apache.logging.log4j.LogManager
import org.greenrobot.eventbus.EventBus

import javax.swing.JPopupMenu
import javax.swing.JToolBar
import java.awt.Desktop
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.PathMatcher

/**
 * This class provides utility functions and access to settings and the information about plugins. Every plugin gets a single context.
 *
 * @see Plugin
 * @see PluginSettings
 * @see PluginInfo
 * @see PluginAction
 */
final class PluginContext implements ActionListener {

    private final LinkedHashMap<String, PluginAction> actionMap
    private final HashMap<String, Class<? extends PluginEditor>> editors
    /**
     * Holds information about this plugin
     */
    final PluginInfo info
    /**
     * Holds settings for this plugin
     */
    final PluginSettings settings

    PluginContext() {
        this(null)
    }

    PluginContext(PluginInfo info) {
        this.info = info
        this.settings = info == null ? null : new PluginSettings(info.context)
        actionMap = new LinkedHashMap<>()
        editors = new HashMap<>()
    }

    /**
     * Creates a new context including the registered action on the current plugin
     * @param info A plugin information
     * @return A new context extended from this context
     */
    PluginContext deriveContext(PluginInfo info) {
        PluginContext context = new PluginContext(info)
        context.actionMap.putAll(this.actionMap)
        context
    }

    /**
     * Registers a new action used from the plugin's menu
     * @param name The name of this action
     * @param action The action to perform
     */
    void registerAction(String name, PluginAction action) {
        actionMap.put(name, action)
    }

    /**
     * Triggers a registered action
     * @param name The name of the action to trigger
     */
    void triggerAction(String name) {
        actionMap.get(name).performAction(this)
    }

    /**
     * Requests the Workbench to load a resource pack or data pack
     * @param pack Path to the pack to load
     */
    void openPack(String pack) {
        EventTransport transport = new EventTransport(EventTransport.EVENT_OPEN_PACK, pack)
        EventBus.getDefault().post(transport)
    }

    /**
     * Request the System to open a directory in the default File Explorer
     * @param dir Path to open in the File Explorer
     */
    void openDirectory(String dir) {
        Desktop desktop = Desktop.getDesktop()
        try {
            desktop.open(new File(dir))
        } catch (IOException e) {
            LogManager.getLogger(getClass()).trace(Utils.dump(e))
        }
    }

    /**
     * Requests the Workbench to open an editor for a file
     * @param path Path of the file to edit
     */
    void openFile(String path) {
        EventTransport transport = new EventTransport(EventTransport.EVENT_OPEN_FILE, path)
        EventBus.getDefault().post(transport)
    }

    /**
     * Requests the Workbench to open a markdown viewer
     * @param url Path of the file to show
     */
    void showMarkdownDocument(String url) {
        EventTransport transport = new EventTransport(EventTransport.EVENT_OPEN_MARKDOWN, url)
        EventBus.getDefault().post(transport)
    }

    /**
     * Registers a new editor
     * @param matcher Regular expression to match a path
     * @param editor The editor to use to edit a file
     */
    void registerEditor(String matcher, Class<? extends PluginEditor> editor) {
        editors.put(matcher, editor)
    }

    PluginObject<Class<? extends PluginEditor>> getEditor(String path) {
        for (String regex : editors.keySet()) {
            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:${regex}")
            Path filePath = new File(path).toPath()
            if (matcher.matches(filePath)) {
                try {
                    return new PluginObject<Class<? extends PluginEditor>>(editors.get(regex), info.id)
                } catch (e) {
                    LogManager.getLogger(EditorManager.getClass()).trace(Utils.dump(e))
                }
            }
        }
        return null
    }

    /**
     * Creates a menu to a JPopupMenu from a Json file
     * @param menu The target menu
     * @param file The json file to load
     * @param listener The listener used to for the items of this menu
     */
    void inflateMenu(JPopupMenu menu, File file, ActionListener listener) {
        MenuBuilder.createMenu(menu, file.toURI().toURL(), listener)
    }

    /**
     * Creates a menu to a JToolBar form a Json file
     * @param menu The target toolbar
     * @param file The Json file to load
     * @param listener The listener used to for the items of this menu
     */
    void inflateMenu(JToolBar menu, File file, ActionListener listener) {
        MenuBuilder.createMenu(menu, file.toURI().toURL(), listener)
    }

    /**
     * Sends a message to all the plugins and the Workbench
     * @param message The message to send
     */
    void broadcast(PluginMessage message) {
        if (message.targetId) {
            PluginInfo info = PluginManager.getInstance().getPlugin(message.targetId)
            if (info != null) {
                info.plugin.onMessageReceived(message)
            }
        } else {
            for (PluginInfo info : PluginManager.getInstance().getPlugins()) {
                info.plugin.onMessageReceived(message)
            }
            EventBus.getDefault().post(message)
        }
    }

    /**
     * Requests information from the Workbench
     * @param request The request to send
     */
    void request(PluginRequest request) {
        EventBus.getDefault().post(request)
    }

    /**
     * Handles exceptions. Used to write to the Cube Creator's log file
     * @param info The plugin information
     * @param t The exception to handle
     */
    void handle(PluginInfo info, Throwable t) {
        LogManager.getLogger("Plugin[${info.id}]").trace(Utils.dump(t))
    }

    @Override
    void actionPerformed(ActionEvent actionEvent) {
        if (actionMap.containsKey(actionEvent.getActionCommand())) {
            actionMap.get(actionEvent.getActionCommand()).performAction(this)
        }
    }
}
