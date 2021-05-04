//file:noinspection GrMethodMayBeStatic
package io.github.cubecreator.plugin

import com.google.gson.Gson
import io.github.cubecreator.util.FileComparator
import io.github.cubecreator.util.Utils
import org.apache.commons.io.FileUtils
import org.apache.logging.log4j.LogManager

import javax.swing.JOptionPane

final class PluginManager {

    private static PluginManager instance

    static PluginManager getInstance() {
        if (instance == null) {
            instance = new PluginManager()
        }
        instance
    }

    private final LinkedHashMap<String, PluginInfo> plugins
    private final PluginContext defaultContext

    private PluginManager() {
        plugins = new LinkedHashMap<>()
        defaultContext = new PluginContext()
    }

    Collection<String> getIds() {
        plugins.keySet()
    }

    Collection<PluginInfo> getPlugins() {
        plugins.values()
    }

    PluginInfo getPlugin(String id) {
        plugins.get(id)
    }

    PluginContext getDefaultContext() {
        defaultContext
    }

    void unloadPlugins() {
        for (PluginInfo plugin : plugins.values()) {
            unloadPlugin(plugin, defaultContext)
        }
    }

    void loadPlugins() {
        File directory = new File(".", "plugins")
        File[] files = directory.listFiles()
        if (files != null) {
            Arrays.sort(files, new FileComparator())
            Gson gson = new Gson()
            for (File file : files) {
                if (file.isDirectory()) {
                    LogManager.getLogger(getClass()).info("Loading plugin ${file.name}")
                    File infoFile = new File(file, "plugin-info.json")
                    if (infoFile.exists()) {
                        try {
                            String json = FileUtils.readFileToString(infoFile, "UTF-8")
                            PluginInfo info = gson.fromJson(json, PluginInfo.class)
                            if (plugins.containsKey(info.id)) {
                                String ext = plugins.get(info.id).name
                                LogManager.getLogger(getClass()).error("Duplicated plugin id ${info.id} for ${ext} and ${info.name}. If you created this plugin (${info.name}) try generating a new UUID, if not advice the developer.")
                                throw new DuplicatedPluginIDException(info.id)
                            }
                            info.context = defaultContext.deriveContext(info)
                            info.setLocation(file)
                            loadPlugin(info, info.context)
                            plugins.put(info.getId(), info)
                            LogManager.getLogger(getClass()).info("Loaded plugin ${info.name} (${info.id})")
                        } catch (e) {
                            LogManager.getLogger(getClass()).error("Failed to load plugin ${file.name}")
                            LogManager.getLogger(getClass()).trace(Utils.dump(e))
                        }
                    } else {
                        LogManager.getLogger(getClass()).error("Skipped ${file.name} (not a plugin)")
                    }
                }
            }
        }
    }

    void unloadPlugin(PluginInfo plugin, PluginContext pluginContext) {
        LogManager.getLogger(getClass()).info("Unloading plugin ${plugin.name} (${plugin.id})")
        plugin.plugin.unload(pluginContext)
    }

    void loadPlugin(PluginInfo plugin, PluginContext pluginContext) throws IOException {
        File main = new File(plugin.location, "plugin" + File.separator + plugin.entry)
        if (main.exists()) {
            plugin.initialize()
            plugin.plugin.load(pluginContext)
        } else {
            main = new File(plugin.location, "plugin" + File.separator + plugin.entry + ".groovy")
            if (main.exists()) {
                plugin.initialize()
                plugin.plugin.load(pluginContext)
            } else {
                LogManager.getLogger(getClass()).error("Could not load plugin ${plugin.name}: missing entry file")
            }
        }
    }

    void onFileOpened(File file) {
        for (PluginInfo info : plugins.values()) {
            info.plugin.onFileOpened(new PluginEvent<File>(file))
        }
    }

    void onFileClosed(File file) {
        for (PluginInfo info : plugins.values()) {
            info.plugin.onFileClosed(new PluginEvent<File>(file))
        }
    }

    void onFileSaved(File file) {
        for (PluginInfo info : plugins.values()) {
            info.plugin.onFileSaved(new PluginEvent<File>(file))
        }
    }

    void onPackOpened(File file) {
        for (PluginInfo info : plugins.values()) {
            info.plugin.onPackOpened(new PluginEvent<File>(file))
        }
    }

    void onPackClosed(File file) {
        for (PluginInfo info : plugins.values()) {
            info.plugin.onPackClosed(new PluginEvent<File>(file))
        }
    }

}
