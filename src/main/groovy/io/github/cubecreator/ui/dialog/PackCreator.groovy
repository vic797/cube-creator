package io.github.cubecreator.ui.dialog

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import io.github.cubecreator.components.FileField
import io.github.cubecreator.util.FormLayout
import org.apache.commons.io.FileUtils

import javax.swing.JFileChooser
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSpinner
import javax.swing.JTextField
import javax.swing.SpinnerNumberModel
import javax.swing.border.EmptyBorder
import java.nio.charset.StandardCharsets

final class PackCreator extends BaseDialog {

    private final JTextField name
    private final JTextField description
    private final JSpinner format
    private final JTextField author
    private final FileField fileField

    PackCreator() {
        JPanel container = new JPanel()
        container.border = new EmptyBorder(8, 8, 8, 8)
        container.layout = new FormLayout(150)

        name = new JTextField()
        description = new JTextField()
        format = new JSpinner(new SpinnerNumberModel(6, 1, 7, 1))
        author = new JTextField()
        fileField = new FileField()

        fileField.selectionMode = JFileChooser.DIRECTORIES_ONLY

        container.add(new JLabel("Name:"))
        container.add(name)

        container.add(new JLabel("Description:"))
        container.add(description)

        container.add(new JLabel("Format:"))
        container.add(format)

        container.add(new JLabel("Author:"))
        container.add(author)

        container.add(new JLabel("Path:"))
        container.add(fileField)

        setContent(container)
        setSize(500, 300)
        setTitle("New pack")
        addActionButton("Cancel", RESULT_CANCEL)
        addActionButton("Create", RESULT_OK)
    }

    File getPackFile() throws IOException {
        JsonObject meta = new JsonObject()
        JsonObject pack = new JsonObject()
        pack.addProperty("pack_format", (int) format.value)
        pack.addProperty("description", description.text)
        meta.add("pack", pack)
        JsonObject cube = new JsonObject()
        cube.addProperty("author", author.text)
        cube.addProperty("name", name.text)
        meta.add("cube", cube)
        File root = new File(fileField.selectedFile, name.text)
        if (!root.mkdirs()) {
            throw new IOException("Could not create directory")
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create()
        File packFile = new File(root, "pack.mcmeta")
        FileUtils.writeStringToFile(packFile, gson.toJson(meta), StandardCharsets.UTF_8)
        File data = new File(root, "data")
        if (!data.mkdirs()) {
            throw new IOException("Could not create directory")
        }
        root
    }
}
