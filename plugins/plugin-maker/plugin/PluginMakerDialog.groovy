import io.github.cubecreator.plugin.PluginDialog
import io.github.cubecreator.plugin.PluginContext

import io.github.cubecreator.util.FormLayout
import io.github.cubecreator.components.FileField

import javax.swing.JLabel
import javax.swing.JTextField
import javax.swing.JPanel
import javax.swing.border.EmptyBorder
import javax.swing.JFileChooser
import java.nio.charset.StandardCharsets

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser

import org.apache.commons.io.FileUtils

class PluginMakerDialog extends PluginDialog {

    private JTextField nameField
    private JTextField displayNameField
    private JTextField authorField
    private JTextField versionField
    private JTextField uuidField
    private JTextField linkField
    private JTextField descriptionField
    private JTextField entryField
    private FileField pathField
    private File dataDir;

    PluginMakerDialog(PluginContext context, File dataDir) {
        super(context)
        this.dataDir = dataDir

        nameField = new JTextField()
        displayNameField = new JTextField()
        authorField = new JTextField()
        versionField = new JTextField()
        uuidField = new JTextField()
        linkField = new JTextField()
        descriptionField = new JTextField()
        entryField = new JTextField()
        pathField = new FileField()

        versionField.text = "1.0"
        uuidField.text = UUID.randomUUID().toString()
        uuidField.editable = false
        entryField.text = "Main.groovy"
        pathField.selectionMode = JFileChooser.DIRECTORIES_ONLY

        JPanel wrapper = new JPanel()
        wrapper.layout = new FormLayout(100)
        wrapper.border = new EmptyBorder(8, 8, 8, 8)

        wrapper.add(new JLabel("Name:"))
        wrapper.add(nameField)

        wrapper.add(new JLabel("Display name:"))
        wrapper.add(displayNameField)
        
        wrapper.add(new JLabel("Author:"))
        wrapper.add(authorField)
        
        wrapper.add(new JLabel("Version:"))
        wrapper.add(versionField)
        
        wrapper.add(new JLabel("ID:"))
        wrapper.add(uuidField)
        
        wrapper.add(new JLabel("Link:"))
        wrapper.add(linkField)
        
        wrapper.add(new JLabel("Description:"))
        wrapper.add(descriptionField)
        
        wrapper.add(new JLabel("Entry:"))
        wrapper.add(entryField)
        
        wrapper.add(new JLabel("Path:"))
        wrapper.add(pathField)

        setContent(wrapper)
        setSize(500, 400)
        addActionButton("Cancel", RESULT_CANCEL)
        addActionButton("Create", RESULT_OK)
    }

    File createPlugin() throws IOException {
        JsonObject plugin = new JsonObject()
        plugin.addProperty("name", nameField.text)
        plugin.addProperty("display_name", displayNameField.text)
        plugin.addProperty("author", authorField.text)
        plugin.addProperty("version", versionField.text)
        plugin.addProperty("id", uuidField.text)
        plugin.addProperty("link", linkField.text)
        plugin.addProperty("description", descriptionField.text)
        plugin.addProperty("entry", entryField.text)
        plugin.add("menu", new JsonArray())

        File root = new File(pathField.selectedFile, nameField.text.toLowerCase().replace(" ", "-"))
        if (!root.mkdirs()) {
            throw new IOException("Could not create directory")
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create()
        File pluginFile = new File(root, "plugin-info.json")
        FileUtils.writeStringToFile(pluginFile, gson.toJson(plugin), StandardCharsets.UTF_8)
        File pluginDir = new File(root, "plugin")
        if (!pluginDir.mkdirs()) {
            throw new IOException("Could not create directory")
        }
        File entry = new File(pluginDir, entryField.text)
        String code = FileUtils.readFileToString(new File(dataDir, "BaseScript.groovy"), "UTF-8")
        FileUtils.writeStringToFile(entry, code, "UTF-8")
        File data = new File(root, "data")
        if (!data.mkdirs()) {
            throw new IOException("Could not create directory")
        }
        return root
    }

}