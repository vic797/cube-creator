package io.github.cubecreator.ui.editor

import io.github.cubecreator.components.LinkLabel
import io.github.cubecreator.plugin.PluginInfo
import io.github.cubecreator.plugin.PluginManager
import io.github.cubecreator.ui.menu.MenuBuilder
import io.github.cubecreator.util.FormLayout
import io.github.cubecreator.util.Utils
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.exception.ZipException
import org.apache.commons.io.FileUtils
import org.apache.logging.log4j.LogManager

import javax.swing.DefaultListCellRenderer
import javax.swing.DefaultListModel
import javax.swing.JFileChooser
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JSplitPane
import javax.swing.JTextField
import javax.swing.JToolBar
import javax.swing.ProgressMonitor
import javax.swing.border.EmptyBorder
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener
import javax.swing.filechooser.FileNameExtensionFilter
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Container
import java.awt.event.ActionEvent

class PluginEditor extends AbstractEditor implements ListSelectionListener {

    static final String ACTION_ADD_PLUGIN = "PluginEditor.ACTION_ADD_PLUGIN"
    static final String ACTION_DELETE_PLUGIN = "PluginEditor.ACTION_DELETE_PLUGIN"

    private DefaultListModel<PluginInfo> pluginsModel
    //private JLabel iconLabel
    private JTextField idField
    private JLabel nameLabel
    private JLabel displayLabel
    private JLabel authorLabel
    private JLabel versionLabel
    private LinkLabel linkLabel
    private JLabel descriptionLabel
    private JList<PluginInfo> pluginsList

    @Override
    protected void createEditor(Container container) {
        pluginsList = new JList<>()
        pluginsModel = new DefaultListModel<>()
        for (PluginInfo info : PluginManager.getInstance().getPlugins()) {
            pluginsModel.addElement(info)
        }
        pluginsList.model = pluginsModel
        pluginsList.cellRenderer = new PluginInfoRenderer()
        pluginsList.addListSelectionListener(this)

        JSplitPane splitPane = new JSplitPane()
        splitPane.leftComponent = new JScrollPane(pluginsList)

        JPanel pluginActions = new JPanel()
        pluginActions.border = new EmptyBorder(8, 8, 8, 8)
        pluginActions.layout = new FormLayout(100)

        //iconLabel = new JLabel()
        idField = new JTextField()
        nameLabel = new JLabel(" ")
        displayLabel = new JLabel(" ")
        authorLabel = new JLabel(" ")
        versionLabel = new JLabel(" ")
        linkLabel = new LinkLabel(" ", null)
        descriptionLabel = new JLabel(" ")

        idField.editable = false

        pluginActions.add(new JLabel("Name:"))
        pluginActions.add(nameLabel)
        pluginActions.add(new JLabel("Display name:"))
        pluginActions.add(displayLabel)
        pluginActions.add(new JLabel("Id:"))
        pluginActions.add(idField)
        pluginActions.add(new JLabel("Author:"))
        pluginActions.add(authorLabel)
        pluginActions.add(new JLabel("Version:"))
        pluginActions.add(versionLabel)
        pluginActions.add(new JLabel("Website:"))
        pluginActions.add(linkLabel)
        pluginActions.add(new JLabel("Description:"))
        pluginActions.add(descriptionLabel)

        splitPane.rightComponent = new JScrollPane(pluginActions)

        container.layout = new BorderLayout()
        container.add(splitPane, BorderLayout.CENTER)
    }

    @Override
    protected byte[] getData() {
        return new byte[0]
    }

    @Override
    protected void setData(byte[] data) {}

    @Override
    protected void createToolBar(JToolBar toolBar) {
        try {
            MenuBuilder.createMenu(toolBar, new URL("cube://config/menu/plugin-manager.json"), this)
        } catch (IOException e) {
            LogManager.getLogger(getClass()).trace(Utils.dump(e))
        }
    }

    @Override
    void valueChanged(ListSelectionEvent listSelectionEvent) {
        PluginInfo info = pluginsModel.get(pluginsList.selectedIndex)
        idField.text = info.id
        nameLabel.text = info.name
        authorLabel.text = info.author
        versionLabel.text = info.version
        linkLabel.link = new URI(info.link)
        linkLabel.text = info.link
        descriptionLabel.text = info.description
        displayLabel.text = info.displayName
    }

    @Override
    void actionPerformed(ActionEvent actionEvent) {
        switch (actionEvent.getActionCommand()) {
            case ACTION_ADD_PLUGIN: {
                addPlugin()
                break
            }
            case ACTION_DELETE_PLUGIN: {
                deletePlugin()
                break
            }
        }
    }

    private void addPlugin() {
        JFileChooser chooser = new JFileChooser()
        chooser.multiSelectionEnabled = true
        chooser.acceptAllFileFilterUsed = false
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Cube Creator Plugin", "ccep"))
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            installPlugins(chooser.selectedFiles)
        }
    }

    private void deletePlugin() {
        PluginInfo info = pluginsModel.getElementAt(pluginsList.selectedIndex)
        int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to uninstall ${info.name}", "Delete plugin", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
        if (result == JOptionPane.YES_OPTION) {
            info.plugin.unload(info.context)
            FileUtils.deleteDirectory(info.location)
            pluginsModel.removeElement(info)
            JOptionPane.showMessageDialog(null, "Please restart Cube Creator to apply changes", "Plugins", JOptionPane.INFORMATION_MESSAGE)
        }
    }

    private void installPlugins(final File[] files) {
        final ProgressMonitor monitor = new ProgressMonitor(null, "Installing plugins", "", 0, 100)
        monitor.setProgress(0)
        new Thread(() -> {
            for (int i = 0; i < files.length; i++) {
                File directory = new File(".", "plugins")
                try {
                    File ccep = files[i]
                    monitor.setNote("Installing plugin ${ccep.name}")
                    ZipFile zipFile = new ZipFile(ccep)
                    if (zipFile.isEncrypted()) {
                        LogManager.getLogger(getClass()).error("Could not extract plugin with password ${ccep.name}")
                    }
                    File out = new File(directory, ccep.name)
                    if (out.exists()) {
                        out = new File(directory, ccep.name + "-" + UUID.randomUUID().toString())
                    }
                    zipFile.extractAll(out.toString())
                } catch (ZipException e) {
                    LogManager.getLogger(getClass()).trace(Utils.dump(e))
                }
                monitor.setProgress(((i * 100) / files.length) as int)
            }
            JOptionPane.showMessageDialog(null, "Please restart Cube Creator to apply changes", "Plugins", JOptionPane.INFORMATION_MESSAGE)
        }).start()
    }

    static class PluginInfoRenderer extends DefaultListCellRenderer {

        @Override
        Component getListCellRendererComponent(JList<?> jList, Object o, int i, boolean b, boolean b1) {
            JLabel label = (JLabel) super.getListCellRendererComponent(jList, o, i, b, b1)
            if (o instanceof PluginInfo) {
                PluginInfo info = (PluginInfo) o
                label.text = info.name
                label.icon = info.smallIcon
            }
            return label
        }
    }
}
