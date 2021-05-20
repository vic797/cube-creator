package io.github.cubecreator.ui

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import io.github.cubecreator.components.FileExplorer
import io.github.cubecreator.components.MarkdownView
import io.github.cubecreator.components.TabControl
import io.github.cubecreator.plugin.PluginInfo
import io.github.cubecreator.plugin.PluginManager
import io.github.cubecreator.plugin.PluginRequest
import io.github.cubecreator.ui.dialog.BaseDialog
import io.github.cubecreator.ui.dialog.PackCreator
import io.github.cubecreator.ui.dialog.PluginAboutDialog
import io.github.cubecreator.ui.editor.AbstractEditor
import io.github.cubecreator.ui.editor.CodeEditor
import io.github.cubecreator.ui.editor.EditorManager
import io.github.cubecreator.ui.editor.PluginEditor
import io.github.cubecreator.ui.menu.MenuBuilder
import io.github.cubecreator.util.EventTransport
import io.github.cubecreator.util.Settings
import io.github.cubecreator.util.Utils
import org.apache.commons.io.IOUtils
import org.apache.logging.log4j.LogManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.JOptionPane
import javax.swing.JPopupMenu
import javax.swing.JScrollPane
import javax.swing.JSplitPane
import javax.swing.WindowConstants
import javax.swing.filechooser.FileNameExtensionFilter
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Container
import java.awt.Dimension
import java.awt.Point
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.nio.charset.StandardCharsets
import java.util.function.BiConsumer

final class Workbench extends JFrame implements ActionListener {

    public static final String ACTION_NEW_FILE = "Workbench.ACTION_NEW_FILE"
    public static final String ACTION_NEW_PACK = "Workbench.ACTION_NEW_PACK"
    public static final String ACTION_OPEN_FILE = "Workbench.ACTION_OPEN_FILE"
    public static final String ACTION_OPEN_PACK = "Workbench.ACTION_OPEN_PACK"
    public static final String ACTION_SAVE_ALL = "Workbench.ACTION_SAVE_ALL"
    public static final String ACTION_SAVE_AS = "Workbench.ACTION_SAVE_AS"
    public static final String ACTION_CLOSE_FILE = "Workbench.ACTION_CLOSE_FILE"
    public static final String ACTION_CLOSE_ALL_FILES = "Workbench.ACTION_CLOSE_ALL_FILES"
    public static final String ACTION_CLOSE_PACK = "Workbench.ACTION_CLOSE_PACK"
    public static final String ACTION_EXIT = "Workbench.ACTION_EXIT"
    public static final String ACTION_OPEN_HELP = "Workbench.ACTION_OPEN_HELP"
    public static final String ACTION_OPEN_ABOUT = "Workbench.ACTION_OPEN_ABOUT"
    public static final String ACTION_MANAGE_PLUGINS = "Workbench.ACTION_MANAGE_PLUGINS"

    private final TabControl control
    private AbstractEditor selectedEditor
    private final FileExplorer explorer
    private final Settings settings
    private final JSplitPane splitPane

    Workbench() {
        settings = Settings.get("config.properties")

        Container container = new Container()
        container.setLayout(new BorderLayout())

        try {
            JMenuBar menuBar = new JMenuBar()
            MenuBuilder.setMenuPreprocessor(menu -> {
                if (menu.getName() == "plugin_menu_item") {
                    List<PluginInfo> plugins = new ArrayList<>(PluginManager.getInstance().getPlugins())
                    if (plugins.size() > 0) {
                        menu.addSeparator()
                        for (int i = 0; i < plugins.size(); i++) {
                            final PluginInfo info = plugins.get(i)
                            JMenu pluginMenu = new JMenu()
                            pluginMenu.text = info.displayName
                            pluginMenu.icon = info.smallIcon
                            MenuBuilder.createMenu(pluginMenu, info.getMenu(), info.context)
                            if (pluginMenu.getItemCount() != 0) {
                                pluginMenu.addSeparator()
                            }
                            JMenuItem settingItem = new JMenuItem("Settings")
                            settingItem.addActionListener(actionEvent -> info.plugin.editSettings())
                            pluginMenu.add(settingItem)
                            JMenuItem aboutItem  = new JMenuItem("About")
                            aboutItem.addActionListener(actionEvent -> {
                                PluginAboutDialog dialog = new PluginAboutDialog(info)
                                dialog.showDialog(this)
                            })
                            pluginMenu.add(aboutItem)
                            menu.add(pluginMenu)
                        }
                    }
                }
            })
            MenuBuilder.createMenu(menuBar, new URL("cube://config/menu/menu-bar.json"), this)
            setJMenuBar(menuBar)
        } catch (IOException e) {
            LogManager.getLogger(getClass()).trace(Utils.dump(e))
        }

        control = new TabControl()
        control.addChangeListener(changeEvent -> {
            if (control.getSelectedComponent() instanceof AbstractEditor) {
                selectedEditor = (AbstractEditor) control.getSelectedComponent()
            } else {
                selectedEditor = null
            }
        })
        control.setCloseListener((tabbedPane, integer) -> {
            if (tabbedPane.getComponentAt(integer) instanceof AbstractEditor) {
                AbstractEditor editor = (AbstractEditor) tabbedPane.getComponentAt(integer)
                PluginManager.getInstance().onFileClosed(editor.file)
            }
            tabbedPane.removeTabAt(integer)
        })

        explorer = new FileExplorer()
        JPopupMenu popupMenu = new JPopupMenu()
        MenuBuilder.createMenu(popupMenu, new URL("cube://config/menu/explorer-menu.json"), explorer)
        explorer.popupMenu = popupMenu
        explorer.setFileSelectionListener(file -> {
            try {
                AbstractEditor editor = EditorManager.getEditor(file.toString())
                bindModificationListener(editor)
                editor.load(file)
                control.addTab(file.getName(), editor)
                PluginManager.getInstance().onFileOpened(editor.file)
            } catch (IOException e) {
                LogManager.getLogger(getClass()).trace(Utils.dump(e))
                JOptionPane.showMessageDialog(this, "Could not load file ${file}", "Error", JOptionPane.ERROR_MESSAGE)
            }
        })

        splitPane = new JSplitPane()
        splitPane.setRightComponent(control)
        splitPane.setLeftComponent(new JScrollPane(explorer))
        splitPane.dividerLocation = settings.get("divider-location", 250)
        container.add(splitPane, BorderLayout.CENTER)

        title = "Cube Creator"
        setContentPane(container)
        boolean max = settings.get("maximized", false)
        int width = settings.get("width", 800)
        int height = settings.get("height", 600)
        size = new Dimension(width, height)
        if (max) {
            extendedState = extendedState | MAXIMIZED_BOTH
        }
        if (settings.has("x") && settings.has("y")) {
            location = new Point(settings.get("x", 0), settings.get("y", 0))
        } else {
            setLocationRelativeTo(null)
        }
        addWindowListener(new WindowAdapter() {
            @Override
            void windowOpened(WindowEvent windowEvent) {
                LogManager.getLogger(getClass()).info("Registering to Event Bus")
                EventBus.getDefault().register(Workbench.this)
                if (settings.has("opened-pack")) {
                    LogManager.getLogger(getClass()).info("Restoring session")
                    String packPath = settings.get("opened-pack")
                    File pack = new File(packPath)
                    explorer.setRoot(pack)
                    PluginManager.getInstance().onPackOpened(pack)
                }
            }

            @Override
            void windowClosing(WindowEvent windowEvent) {
                settings.put("maximized", (extendedState & MAXIMIZED_BOTH) == MAXIMIZED_BOTH)
                if ((extendedState & MAXIMIZED_BOTH) != MAXIMIZED_BOTH) {
                    settings.put("width", width)
                    settings.put("height", height)
                    settings.put("x", location.x as int)
                    settings.put("y", location.y as int)
                }
                settings.put("divider-location", splitPane.dividerLocation)
                if (explorer.root != null) {
                    settings.put("opened-pack", explorer.root.toString())
                } else {
                    settings.remove("opened-pack")
                }
                LogManager.getLogger(getClass()).info("Saving settings")
                settings.saveSettings()
                new Thread(() -> {
                    LogManager.getLogger(getClass()).info("Unloading all plugins")
                    PluginManager.getInstance().unloadPlugins()
                    LogManager.getLogger(getClass()).info("Unregistering from Event Bus")
                    EventBus.getDefault().unregister(Workbench.this)
                }).start()
            }
        })
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
    }

    @SuppressWarnings('unused')
    @Subscribe
    void pluginMessageReceived(PluginRequest request) {
        switch (request.request) {
            case PluginRequest.REQUEST_PACK: {
                request.response(explorer.root)
                break
            }
            case PluginRequest.REQUEST_FILES: {
                List<File> files = new ArrayList<>()
                for (int i = 0; i < control.getTabCount(); i++) {
                    Component component = control.getComponentAt(i)
                    if (component instanceof AbstractEditor) {
                        files.add(component.file)
                    }
                }
                request.response(Collections.unmodifiableList(files))
                break
            }
        }
    }

    @SuppressWarnings('unused')
    @Subscribe
    void handlePluginEvent(EventTransport transport) {
        switch (transport.getEvent()) {
            case EventTransport.EVENT_OPEN_FILE: {
                File file = new File((String) transport.getParams()[0])
                try {
                    AbstractEditor editor = EditorManager.getEditor(file.toString())
                    bindModificationListener(editor)
                    editor.load(file)
                    control.addTab(file.getName(), editor)
                    PluginManager.getInstance().onFileOpened(editor.file)
                } catch (IOException e) {
                    LogManager.getLogger(getClass()).trace(Utils.dump(e))
                    JOptionPane.showMessageDialog(this, "Could not load file ${file}", "Error", JOptionPane.ERROR_MESSAGE)
                }
                break
            }
            case EventTransport.EVENT_OPEN_MARKDOWN: {
                openMarkdown((String) transport.getParams()[0], (String) transport.getParams()[1])
                break
            }
            case EventTransport.EVENT_OPEN_PACK: {
                if (explorer.getRoot() != null) {
                    while (control.getTabCount() != 0) {
                        control.removeTabAt(0, false)
                    }
                    PluginManager.getInstance().onPackClosed(explorer.getRoot())
                    explorer.setRoot(null)
                }
                File pack = new File((String) transport.getParams()[0])
                explorer.setRoot(pack)
                PluginManager.getInstance().onPackOpened(pack)
                break
            }
        }
    }

    private void openFile() {
        JFileChooser chooser = new JFileChooser()
        chooser.fileHidingEnabled = false
        chooser.acceptAllFileFilterUsed = false
        chooser.multiSelectionEnabled = true
        try {
            JsonArray array = JsonParser.parseString(IOUtils.toString(new URL("cube://config/file-filters.json"), StandardCharsets.UTF_8)).getAsJsonArray()
            Gson gson = new Gson()
            for (int i = 0; i < array.size(); i++) {
                JsonObject filter = array.get(i).getAsJsonObject()
                String[] extension = gson.fromJson(filter.get("extensions"), new TypeToken<String[]>() {}.getType())
                chooser.addChoosableFileFilter(new FileNameExtensionFilter(filter.get("text").getAsString(), extension))
            }
        } catch (IOException e) {
            LogManager.getLogger(getClass()).trace(Utils.dump(e))
        }
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            for (File file : chooser.getSelectedFiles()) {
                try {
                    AbstractEditor editor = EditorManager.getEditor(file.toString())
                    bindModificationListener(editor)
                    editor.load(file)
                    control.addTab(file.getName(), editor)
                    PluginManager.getInstance().onFileOpened(file)
                } catch (IOException e) {
                    LogManager.getLogger(getClass()).trace(Utils.dump(e))
                    JOptionPane.showMessageDialog(this, "Could not load file ${file}", "Error", JOptionPane.ERROR_MESSAGE)
                }
            }
        }
    }

    private void saveAllFiles() {
        for (int i = 0; i < control.getTabCount(); i++) {
            Component component = control.getComponentAt(i)
            if (component instanceof AbstractEditor) {
                try {
                    ((AbstractEditor) component).save()
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, String.format("Could not save file %s", e.getMessage()), "Error", JOptionPane.ERROR_MESSAGE)
                }
            }
        }
    }

    private void saveFileAs() {
        JFileChooser chooser = new JFileChooser()
        chooser.fileHidingEnabled = false
        chooser.acceptAllFileFilterUsed = false
        chooser.multiSelectionEnabled = false
        try {
            JsonArray array = JsonParser.parseString(IOUtils.toString(new URL("cube://config/file-filters.json"), StandardCharsets.UTF_8)).getAsJsonArray()
            Gson gson = new Gson()
            for (int i = 0; i < array.size(); i++) {
                JsonObject filter = array.get(i).getAsJsonObject()
                String[] extension = gson.fromJson(filter.get("extensions"), new TypeToken<String[]>() {}.getType())
                chooser.addChoosableFileFilter(new FileNameExtensionFilter(filter.get("text").getAsString(), extension))
            }
        } catch (IOException e) {
            LogManager.getLogger(getClass()).trace(Utils.dump(e))
        }
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                selectedEditor.save(chooser.getSelectedFile())
                control.setTitleAt(control.indexOfComponent(selectedEditor), chooser.getSelectedFile().getName())
            } catch (IOException e) {
                LogManager.getLogger(getClass()).trace(Utils.dump(e))
                JOptionPane.showMessageDialog(this, String.format("Could not save file %s", chooser.getSelectedFile()), "Error", JOptionPane.ERROR_MESSAGE)
            }
        }
    }

    private void openPack() {
        JFileChooser chooser = new JFileChooser()
        chooser.fileHidingEnabled = false
        chooser.acceptAllFileFilterUsed = false
        chooser.multiSelectionEnabled = false
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Pack", "mcmeta"))
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            if (explorer.getRoot() != null) {
                while (control.getTabCount() != 0) {
                    control.removeTabAt(0, false)
                }
                PluginManager.getInstance().onPackClosed(explorer.getRoot())
                explorer.setRoot(null)
            }
            File pack = chooser.getSelectedFile()
            explorer.setRoot(pack.getParentFile())
            PluginManager.getInstance().onPackOpened(pack.getParentFile())
        }
    }

    private void openMarkdown(String title, String path) {
        try {
            MarkdownView view = new MarkdownView()
            URL url = new URL(path)
            view.setMarkdown(url)
            control.addTab(title, new JScrollPane(view))
        } catch (MalformedURLException e) {
            LogManager.getLogger(getClass()).trace(Utils.dump(e))
            JOptionPane.showMessageDialog(this, "Could not load ${path}", "Error", JOptionPane.ERROR_MESSAGE)
        }
    }

    private void createNewPack() {
        try {
            PackCreator creator = new PackCreator()
            creator.showDialog(this)
            if (creator.getResult() == BaseDialog.RESULT_OK) {
                if (explorer.getRoot() != null) {
                    while (control.getTabCount() != 0) {
                        control.removeTabAt(0, false)
                    }
                    PluginManager.getInstance().onPackClosed(explorer.getRoot())
                    explorer.setRoot(null)
                }
                File packFile = creator.getPackFile()
                explorer.setRoot(packFile)
                PluginManager.getInstance().onPackOpened(packFile)
            }
        } catch (IOException e) {
            LogManager.getLogger(getClass()).trace(Utils.dump(e))
            JOptionPane.showMessageDialog(this, "Could not create pack", "Error", JOptionPane.ERROR_MESSAGE)
        }
    }

    @Override
    void actionPerformed(ActionEvent actionEvent) {
        switch (actionEvent.getActionCommand()) {
            case ACTION_NEW_FILE: {
                String name = JOptionPane.showInputDialog(this, "Write the name for this file:")
                if (name != null) {
                    CodeEditor editor = new CodeEditor()
                    editor.setSyntaxFromFileName(name)
                    bindModificationListener(editor)
                    control.addTab(name, editor)
                }
                break
            }
            case ACTION_NEW_PACK: {
                createNewPack()
                break
            }
            case ACTION_OPEN_FILE: {
                openFile()
                break
            }
            case ACTION_OPEN_PACK: {
                openPack()
                break
            }
            case ACTION_SAVE_ALL: {
                saveAllFiles()
                break
            }
            case ACTION_SAVE_AS: {
                saveFileAs()
                break
            }
            case ACTION_CLOSE_FILE: {
                if (selectedEditor != null) {
                    control.removeTabAt(control.indexOfComponent(selectedEditor), false)
                }
                break
            }
            case ACTION_CLOSE_ALL_FILES: {
                while (control.getTabCount() != 0) {
                    control.removeTabAt(0, false)
                }
                break
            }
            case ACTION_CLOSE_PACK: {
                while (control.getTabCount() != 0) {
                    control.removeTabAt(0, false)
                }
                PluginManager.getInstance().onPackClosed(explorer.getRoot())
                explorer.setRoot(null)
                break
            }
            case ACTION_EXIT: {
                dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING))
                break
            }
            case ACTION_OPEN_HELP: {
                openMarkdown("Help", "cube://docs/help.md")
                break
            }
            case ACTION_OPEN_ABOUT: {
                openMarkdown("About", "cube://docs/about.md")
                break
            }
            case ACTION_MANAGE_PLUGINS: {
                control.addTab("Plugins", new PluginEditor())
                break
            }
            default: {
                if (selectedEditor != null) {
                    selectedEditor.actionPerformed(actionEvent)
                }
                break
            }
        }
    }

    private void bindModificationListener(AbstractEditor editor) {
        editor.modificationListener = new BiConsumer<AbstractEditor, Boolean>() {
            @Override
            void accept(AbstractEditor abstractEditor, Boolean aBoolean) {
                try {
                    control.setHighlightedAt(control.indexOfComponent(abstractEditor), aBoolean)
                } catch (ignored) {}
            }
        }
    }
}
