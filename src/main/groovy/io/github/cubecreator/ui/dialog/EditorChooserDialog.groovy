package io.github.cubecreator.ui.dialog

import io.github.cubecreator.util.ObjectDescriptor

import javax.swing.DefaultListCellRenderer
import javax.swing.DefaultListModel
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JScrollPane
import javax.swing.ListSelectionModel
import java.awt.Component

class EditorChooserDialog extends BaseDialog {

    private JList<ObjectDescriptor> itemList

    EditorChooserDialog() {
        setSize(400, 400)
        title = "Select an editor"
        itemList = new JList<>()
        itemList.selectionMode = ListSelectionModel.SINGLE_SELECTION
        itemList.cellRenderer = new DefaultListCellRenderer() {
            @Override
            Component getListCellRendererComponent(JList<?> jList, Object o, int i, boolean b, boolean b1) {
                JLabel label = (JLabel) super.getListCellRendererComponent(jList, o, i, b, b1)
                if (o instanceof ObjectDescriptor) {
                    label.text = o.describe()
                }
                label
            }
        }
        setContent(new JScrollPane(itemList))
        addActionButton("Select", RESULT_OK)
        addActionButton("Default", RESULT_CANCEL)
    }

    void setItems(Collection<? extends ObjectDescriptor> items)  {
        DefaultListModel<? extends ObjectDescriptor> model = new DefaultListModel<>()
        for (ObjectDescriptor descriptor : items) {
            model.addElement(descriptor)
        }
        itemList.setModel(model)
    }

    int selectedIndex() {
        return itemList.selectedIndex
    }

}
