package io.github.cubecreator.util

import java.awt.Component
import java.awt.Container
import java.awt.Dimension
import java.awt.Insets
import java.awt.LayoutManager

class FormLayout implements LayoutManager {

    private final int labelWidth

    FormLayout(int labelWidth) {
        this.labelWidth = labelWidth
    }

    @Override
    void addLayoutComponent(String s, Component component) {

    }

    @Override
    void removeLayoutComponent(Component component) {

    }

    @Override
    Dimension preferredLayoutSize(Container container) {
        synchronized (container.getTreeLock()) {
            Dimension size = container.getSize()
            size.height = 0
            size.width = labelWidth * 2
            Component[] components = container.getComponents()
            int index = 0
            while (index < components.length) {
                if (index % 2 != 0) {
                    Dimension dimension = components[index].getPreferredSize()
                    size.height += dimension.height
                }
                index++
            }
            size
        }
    }

    @Override
    Dimension minimumLayoutSize(Container container) {
        Dimension dimension = container.getSize()
        dimension.height = 0
        dimension
    }

    @Override
    void layoutContainer(Container container) {
        synchronized (container.getTreeLock()) {
            Dimension size = container.getSize()
            Insets insets = container.getInsets()
            int x = insets.left
            int y = insets.top
            Component[] components = container.getComponents()
            int index = 0
            while (index < components.length) {
                Dimension dimension = components[index].getPreferredSize()
                if (index % 2 == 0) {
                    components[index].setBounds(x, y, labelWidth, (int) dimension.height)
                    x += labelWidth + 8
                } else {
                    int width = (int) (size.width - insets.left - x)
                    components[index].setBounds(x, y, width, (int) dimension.height)
                    y += dimension.height + 8
                    x = insets.left
                }
                index++
            }
        }
    }
}
