package io.github.cubecreator.components

import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JFileChooser
import javax.swing.JTextField
import javax.swing.filechooser.FileFilter
import java.awt.BorderLayout

class FileField extends JComponent {

    private final JTextField pathField
    final List<FileFilter> filterList
    File selectedFile
    int selectionMode
    boolean fileHidingEnabled

    FileField() {
        setLayout(new BorderLayout())
        pathField = new JTextField()
        filterList = new ArrayList<>()
        JButton button = new JButton("…")
        button.addActionListener(actionEvent -> {
            JFileChooser chooser = new JFileChooser()
            chooser.setFileSelectionMode(selectionMode)
            chooser.setAcceptAllFileFilterUsed(false)
            for (FileFilter filter : filterList) {
                chooser.addChoosableFileFilter(filter)
            }
            chooser.setMultiSelectionEnabled(false)
            chooser.setFileHidingEnabled(fileHidingEnabled)
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                setSelectedFile(chooser.getSelectedFile())
            }
        })
        add(pathField, BorderLayout.CENTER)
        add(button, BorderLayout.EAST)
    }

    void setSelectedFile(File selectedFile) {
        this.selectedFile = selectedFile
        pathField.setText(selectedFile.toString())
    }

}
