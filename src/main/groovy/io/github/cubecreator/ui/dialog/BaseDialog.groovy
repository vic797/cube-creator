package io.github.cubecreator.ui.dialog

import javax.swing.AbstractAction
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.InputMap
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JDialog
import javax.swing.JPanel
import javax.swing.KeyStroke
import javax.swing.border.EmptyBorder
import java.awt.BorderLayout
import java.awt.Component
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent

/**
 * A dialog that uses some common options like closing the dialog with <kbd>Esc</kbd> and a <code>result</code> field.
 */
class BaseDialog extends JDialog {

    public static final int RESULT_CANCEL = -1
    public static final int RESULT_OK = 0

    /**
     * The result of this dialog
     */
    int result
    private JPanel content
    private JPanel buttons

    /**
     * Constructor
     */
    BaseDialog() {
        content = new JPanel()
        content.layout = new BorderLayout()

        buttons = new JPanel()
        buttons.layout = new BoxLayout(buttons, BoxLayout.X_AXIS)
        buttons.border = new EmptyBorder(8, 8, 8, 8)
        buttons.add(Box.createHorizontalGlue())

        content.add(buttons, BorderLayout.SOUTH)
        setContentPane(content)

        InputMap map = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
        map.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "CLOSE_DIALOG")
        getRootPane().getActionMap().put("CLOSE_DIALOG", new AbstractAction() {
            @Override
            void actionPerformed(ActionEvent actionEvent) {
                result = RESULT_CANCEL
                setVisible(false)
                dispose()
            }
        })
        addWindowListener(new WindowAdapter() {
            @Override
            void windowClosing(WindowEvent windowEvent) {
                result = RESULT_CANCEL
            }
        })
        resizable = false
    }

    /**
     * Adds an action button at the bottom of the dialog. This action closes the dialog and sets a result that can be used
     * to perform an action
     * @param title The text of this button
     * @param result The result from this dialog
     */
    void addActionButton(String title, int result) {
        JButton button = new JButton(title)
        button.addActionListener(actionEvent -> {
            this.result = result
            setVisible(false)
        })
        buttons.add(Box.createHorizontalStrut(8))
        buttons.add(button)
    }

    /**
     * Sets the content of this dialog
     * @param component The component to display
     */
    void setContent(Component component) {
        content.add(component, BorderLayout.CENTER)
    }

    /**
     * Shows this dialog
     * @param parent The parent component to align this dialog to
     */
    void showDialog(Component parent) {
        modal = true
        locationRelativeTo = parent
        visible = true
    }

}
