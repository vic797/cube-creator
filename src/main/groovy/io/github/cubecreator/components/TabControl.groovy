package io.github.cubecreator.components

import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.Icon
import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTabbedPane
import java.awt.Component
import java.util.function.BiConsumer

class TabControl extends JTabbedPane {

    BiConsumer<JTabbedPane, Integer> closeListener

    TabControl() {
        setTabLayoutPolicy(SCROLL_TAB_LAYOUT)
    }

    @Override
    void insertTab(String s, Icon icon, Component component, String s1, int i) {
        super.insertTab(s, icon, component, s1, i)
        TabItem item = new TabItem(s, icon, component, this)
        setTabComponentAt(i, item)
        setSelectedIndex(i)
    }

    @Override
    void setTitleAt(int i, String s) {
        super.setTitleAt(i, s)
        TabItem item = (TabItem) getTabComponentAt(i)
        item.setTitle(s)
    }

    @Override
    void setIconAt(int i, Icon icon) {
        super.setIconAt(i, icon)
        TabItem item = (TabItem) getTabComponentAt(i)
        item.setIcon(icon)
    }

    void setHighlightedAt(int index, boolean highlighted) {
        TabItem item = (TabItem) getTabComponentAt(index)
        item.highlighted = highlighted
    }

    boolean isHighlightedAt(int index) {
        TabItem item = (TabItem) getTabComponentAt(index)
        return item.highlighted
    }

    void removeTabAt(int i, boolean ignore) {
        if (closeListener == null || ignore) {
            super.removeTabAt(i)
        } else {
            closeListener.accept(this, i)
        }
    }

    private static class TabItem extends JPanel {

        private final JLabel label
        private String title
        private boolean highlight

        private TabItem(String title, Icon icon, Component component, TabControl parent) {
            this.title = title
            this.label = new JLabel()
            this.label.icon = icon
            JButton button = new JButton()
            try {
                button.icon = new ImageIcon(new URL("cube://icons/icons8-close-window-16.png"))
            } catch (MalformedURLException e) {
                button.text = "X"
            }
            button.borderPainted = false
            button.focusPainted = false
            button.contentAreaFilled = false
            button.addActionListener(actionEvent -> parent.removeTabAt(parent.indexOfComponent(component)))
            layout = new BoxLayout(this, BoxLayout.X_AXIS)
            add(this.label)
            add(Box.createHorizontalStrut(8))
            add(button)
            opaque = false
            updateTitle()
        }

        void setTitle(String title) {
            this.title = title
            updateTitle()
        }

        String getTitle() {
            return title
        }

        void setHighlighted(boolean highlight) {
            this.highlight = highlight
            updateTitle()
        }

        boolean getHighlighted() {
            return highlight
        }

        void setIcon(Icon icon) {
            this.label.setIcon(icon)
        }

        Icon getIcon() {
            return this.label.getIcon()
        }

        void updateTitle() {
            if (highlight) {
                label.setText("<html><b>${title}</b></html>")
            } else {
                label.setText(title)
            }
        }

    }

}
