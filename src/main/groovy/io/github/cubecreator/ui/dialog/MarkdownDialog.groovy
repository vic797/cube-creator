package io.github.cubecreator.ui.dialog

import io.github.cubecreator.util.FormLayout

import javax.swing.Box
import javax.swing.JCheckBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSpinner
import javax.swing.JTextField
import javax.swing.SpinnerNumberModel
import javax.swing.border.EmptyBorder
import java.awt.Component
import java.awt.Container
import java.awt.Dimension

final class MarkdownDialog {

    static String showTableDialog(Component anchor) {
        BaseDialog dialog = new BaseDialog()
        dialog.title = "Add table"
        dialog.addActionButton("Cancel", BaseDialog.RESULT_CANCEL)
        dialog.addActionButton("Add", BaseDialog.RESULT_OK)

        JPanel container = new JPanel()
        container.layout = new FormLayout(150)
        container.border = new EmptyBorder(8, 8, 8, 8)

        container.add(new JLabel("Columns:"))
        JSpinner columns = new JSpinner(new SpinnerNumberModel(4, 2, 10, 1))
        container.add(columns)

        container.add(new JLabel("Rows:"))
        JSpinner rows = new JSpinner(new SpinnerNumberModel(4, 2, 10, 1))
        container.add(rows)

        container.add(Box.createHorizontalBox())
        JCheckBox headers = new JCheckBox("Add headers")
        container.add(headers)

        dialog.content = container
        dialog.size = new Dimension(300, 175)
        dialog.showDialog(anchor)

        if (dialog.result == BaseDialog.RESULT_OK) {
            StringBuilder builder = new StringBuilder()
            builder.append(System.lineSeparator())
            int cols = (int) columns.value
            int row = (int) rows.value
            if (headers.selected) {
                builder.append("|")
                String titles = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                for (int c = 0; c < cols; c++) {
                    builder.append("${titles.charAt(c)}|")
                }
                builder.append(System.lineSeparator())
                builder.append("|")
                for (int c = 0; c < cols; c++) {
                    builder.append("-|")
                }
                builder.append(System.lineSeparator())
            }
            for (int r = 0; r < row; r++) {
                builder.append("|")
                for (int c = 0; c < cols; c++) {
                    builder.append(" |")
                }
                builder.append(System.lineSeparator())
            }
            builder.append(System.lineSeparator())
            return builder.toString()
        }
        return ""
    }

    static String showLinkDialog(Component anchor) {
        BaseDialog dialog = new BaseDialog()
        dialog.title = "Add link"
        dialog.addActionButton("Cancel", BaseDialog.RESULT_CANCEL)
        dialog.addActionButton("Add", BaseDialog.RESULT_OK)

        JPanel container = new JPanel()
        container.layout = new FormLayout(150)
        container.border = new EmptyBorder(8, 8, 8, 8)

        container.add(new JLabel("Text:"))
        JTextField text = new JTextField()
        container.add(text)

        container.add(new JLabel("Link:"))
        JTextField link = new JTextField()
        container.add(link)

        dialog.content = container
        dialog.size = new Dimension(400, 150)
        dialog.showDialog(anchor)

        if (dialog.result == BaseDialog.RESULT_OK) {
            return "[${text.text}](${link.text})"
        }
        return ""
    }

    static String showImageDialog(Component anchor) {
        BaseDialog dialog = new BaseDialog()
        dialog.title = "Add image"
        dialog.addActionButton("Cancel", BaseDialog.RESULT_CANCEL)
        dialog.addActionButton("Add", BaseDialog.RESULT_OK)

        JPanel container = new JPanel()
        container.layout = new FormLayout(150)
        container.border = new EmptyBorder(8, 8, 8, 8)

        container.add(new JLabel("Alt text:"))
        JTextField text = new JTextField()
        container.add(text)

        container.add(new JLabel("Link:"))
        JTextField link = new JTextField()
        container.add(link)

        dialog.content = container
        dialog.size = new Dimension(400, 150)
        dialog.showDialog(anchor)

        if (dialog.result == BaseDialog.RESULT_OK) {
            return "![${text.text}](${link.text})"
        }
        return ""
    }

}
