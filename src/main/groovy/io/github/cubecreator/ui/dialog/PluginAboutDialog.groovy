package io.github.cubecreator.ui.dialog

import io.github.cubecreator.components.LinkLabel
import io.github.cubecreator.plugin.PluginInfo
import io.github.cubecreator.util.FormLayout

import javax.swing.Box
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextField
import javax.swing.border.EmptyBorder
import java.awt.BorderLayout
import java.awt.Container

final class PluginAboutDialog extends BaseDialog {

    PluginAboutDialog(PluginInfo plugin) {
        Container container = new Container()
        container.layout = new BorderLayout()

        JPanel wrapper = new JPanel()
        wrapper.border = new EmptyBorder(8, 8, 8, 8)
        wrapper.layout = new FormLayout(100)

        wrapper.add(Box.createHorizontalBox())
        wrapper.add(new JLabel(plugin.name))

        wrapper.add(new JLabel("Id:"))

        JTextField idField = new JTextField()
        idField.text = plugin.id
        idField.editable = false
        wrapper.add(idField)

        wrapper.add(new JLabel("Author:"))
        wrapper.add(new JLabel(plugin.author))

        wrapper.add(new JLabel("Version:"))
        wrapper.add(new JLabel(plugin.version))

        wrapper.add(new JLabel("Website:"))
        try {
            wrapper.add(new LinkLabel(new URI(plugin.link)))
        } catch (URISyntaxException e) {
            wrapper.add(new JLabel("Not available"))
        }

        wrapper.add(new JLabel("Description:"))
        wrapper.add(new JLabel(plugin.description))

        container.add(new JLabel(plugin.icon, JLabel.CENTER), BorderLayout.NORTH)
        container.add(new JScrollPane(wrapper), BorderLayout.CENTER)

        setTitle(plugin.getDisplayName())
        addActionButton("Close", RESULT_OK)
        setContent(container)
        setSize(500, 500)
    }

}
