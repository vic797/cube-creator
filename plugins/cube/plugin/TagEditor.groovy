import io.github.cubecreator.plugin.PluginEditor
import io.github.cubecreator.plugin.PluginContext

import javax.swing.JCheckBox
import javax.swing.JTextArea
import javax.swing.JScrollPane
import javax.swing.JButton
import javax.swing.JToolBar
import javax.swing.ImageIcon

import java.awt.BorderLayout
import java.awt.Container

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser

import java.nio.charset.StandardCharsets

class TagEditor extends PluginEditor {

    private JCheckBox replace
    private JTextArea values

    @Override
    protected void createEditor(Container container) {
        container.layout = new BorderLayout()
        values = new JTextArea()
        container.add(new JScrollPane(values), BorderLayout.CENTER)
    }

    @Override
    protected byte[] getData() {
        JsonObject tag = new JsonObject()
        tag.addProperty("replace", replace.selected)
        JsonArray vals = new JsonArray()
        String[] lines = values.text.split(System.lineSeparator())
        for (int i = 0; i < lines.length; i++) {
            vals.add(lines[i])
        }
        tag.add("values", vals)
        Gson gson = new GsonBuilder().setPrettyPrinting().create()
        return gson.toJson(tag).getBytes(StandardCharsets.UTF_8)
    }

    @Override
    protected void setData(byte[] data) {
        String json = new String(data, StandardCharsets.UTF_8)
        JsonObject tag = JsonParser.parseString(json).getAsJsonObject()
        if (tag.has("replace")) {
            replace.selected = tag.get("replace").getAsBoolean()
        }
        if (tag.has("values")) {
            JsonArray vals = tag.get("values").getAsJsonArray()
            String[] valueArray = new String[vals.size()]
            for (int i = 0; i < vals.size(); i++) {
                valueArray[i] = vals.get(i).getAsString()
            }
            values.text = String.join(System.lineSeparator(), valueArray)
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
        replace = new JCheckBox("Replace values")
        toolBar.addSeparator()
        toolBar.add(replace)
    }

}