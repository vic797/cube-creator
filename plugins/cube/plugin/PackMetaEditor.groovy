import io.github.cubecreator.plugin.PluginEditor
import io.github.cubecreator.plugin.PluginContext
import io.github.cubecreator.util.FormLayout

import javax.swing.JSpinner
import javax.swing.JTextField
import javax.swing.JScrollPane
import javax.swing.SpinnerNumberModel
import javax.swing.border.EmptyBorder
import javax.swing.JLabel
import javax.swing.JButton
import javax.swing.JToolBar
import javax.swing.ImageIcon
import javax.swing.JPanel

import java.awt.Container
import java.awt.BorderLayout

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser

import java.nio.charset.StandardCharsets

class PackMetaEditor extends PluginEditor {

    private JSpinner format
    private JTextField description
    private JTextField author
    private JTextField name

    @Override
    protected void createEditor(Container container) {
        JPanel wrapper = new JPanel()
        wrapper.layout = new FormLayout(150)
        wrapper.border = new EmptyBorder(8, 8, 8, 8)
        description = new JTextField()
        format = new JSpinner(new SpinnerNumberModel(6, 1, 7, 1))
        author = new JTextField()
        name = new JTextField()
        wrapper.add(new JLabel("Name:"))
        wrapper.add(name)
        wrapper.add(new JLabel("Format:"))
        wrapper.add(format)
        wrapper.add(new JLabel("Description:"))
        wrapper.add(description)
        wrapper.add(new JLabel("Author:"))
        wrapper.add(author)

        container.layout = new BorderLayout()
        container.add(new JScrollPane(wrapper))
        
    }

    @Override
    protected byte[] getData() {
        JsonObject root = new JsonObject()
        JsonObject pack = new JsonObject()
        pack.addProperty("pack_format", (int) format.value)
        pack.addProperty("description", description.text)
        JsonObject cube = new JsonObject()
        cube.addProperty("author", author.text)
        cube.addProperty("name", name.text)
        root.add("pack", pack)
        root.add("cube", cube)
        Gson gson = new GsonBuilder().setPrettyPrinting().create()
        return gson.toJson(root).getBytes(StandardCharsets.UTF_8)
    }

    @Override
    protected void setData(byte[] data) {
        if (data.length == 0) return
        try {
            String json = new String(data, StandardCharsets.UTF_8)
            JsonObject meta = JsonParser.parseString(json).getAsJsonObject()
            if (meta.has("pack")) {
                JsonObject pack = meta.get("pack").getAsJsonObject()
                format.value = pack.get("pack_format").getAsInt()
                description.text = pack.get("description").getAsString()
            }
            if (meta.has("cube")) {
                JsonObject cube = meta.get("cube").getAsJsonObject()
                name.text = cube.get("name").getAsString()
                author.text = cube.get("author").getAsString()
            }
        } catch (e) {
            handle(e)
        }
    }

    @Override
    protected void createToolBar(JToolBar toolBar) {
        JButton button = new JButton()
        button.toolTipText = "Save"
        button.icon = new ImageIcon(new URL("cube://icons/icons8-save-16.png"))
        button.actionCommand = ACTION_SAVE
        button.addActionListener(this)
        toolBar.add(button)
    }

}