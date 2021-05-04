package io.github.cubecreator.ui.editor

import io.github.cubecreator.plugin.PluginManager
import io.github.cubecreator.util.Utils
import org.apache.commons.io.IOUtils
import org.apache.logging.log4j.LogManager

import javax.swing.JPanel
import javax.swing.JToolBar
import java.awt.BorderLayout
import java.awt.Container
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.util.function.BiConsumer

/**
 * The base class for the editors used by Cube Creator. This class provides functions to save and load files
 */
abstract class AbstractEditor extends JPanel implements ActionListener {

    public static final String ACTION_SAVE = "AbstractEditor.ACTION_SAVE"

    private File file

    BiConsumer<AbstractEditor, Boolean> modificationListener

    /**
     * Constructor
     */
    AbstractEditor() {
        Container container = new Container()
        createEditor(container)
        JToolBar toolBar = new JToolBar()
        toolBar.setFloatable(false)
        createToolBar(toolBar)
        setLayout(new BorderLayout())
        add(toolBar, BorderLayout.NORTH)
        add(container, BorderLayout.CENTER)
    }

    protected void updateFile(File file) {
        this.file = file
    }

    /**
     * Saves the editor data to a file
     * @param file File to write
     * @throws IOException If the file could not be written
     */
    void save(File file) throws IOException {
        updateFile(file)
        save()
    }

    /**
     * Load data from a file
     * @param file File to read
     * @throws IOException If the file could not be read
     */
    void load(File file) throws IOException {
        updateFile(file)
        load()
    }

    /**
     * Saves the editor data to a file
     * @throws IOException If the file could not be written
     */
    void save() throws IOException {
        if (file == null) {
            throw new IOException("no file")
        }
        FileOutputStream stream = new FileOutputStream(file)
        IOUtils.write(getData(), stream)
        stream.close()
        if (modificationListener != null) {
            modificationListener.accept(this, false)
        }
        PluginManager.getInstance().onFileSaved(file)
    }

    /**
     * Load data from a file
     * @throws IOException If the file could not be read
     */
    void load() throws IOException {
        if (file == null) {
            throw new IOException("no file")
        }
        FileInputStream stream = new FileInputStream(file)
        setData(IOUtils.toByteArray(stream))
        stream.close()
    }

    /**
     * Gets the current editing file
     * @return The file
     */
    File getFile() {
        file
    }

    /**
     * Create a toolbar from this editor
     * @param toolBar The toolbar to load
     */
    protected void createToolBar(JToolBar toolBar) {}

    /**
     * Creates this editor
     * @param container The editor's container
     */
    protected abstract void createEditor(Container container)

    /**
     * Gets the current editor data as a byte array
     * @return The current editor data
     */
    protected abstract byte[] getData()

    /**
     * Load data into the editor
     * @param data The data to load
     */
    protected abstract void setData(byte[] data)

    /**
     * Handle an exception and log it to the log file
     * @param e Exception to log
     */
    protected void handle(Exception e) {
        LogManager.getLogger(getClass()).trace(Utils.dump(e))
    }

    protected void modificationPerformed() {
        if (modificationListener != null) {
            modificationListener.accept(this, true)
        }
    }

    @Override
    void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand() == ACTION_SAVE) {
            try {
                save()
            } catch (IOException e) {
                handle(e)
            }
        }
    }
}
