package io.github.cubecreator.components

import javax.swing.JLabel
import java.awt.Color
import java.awt.Cursor
import java.awt.Desktop
import java.awt.Font
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.font.TextAttribute

class LinkLabel extends JLabel {

    URI link

    LinkLabel(String label, URI link) {
        this()
        setLink(link)
        setText(label)
    }

    LinkLabel(URI link) {
        this()
        setLink(link)
        setText(link.toString())
    }

    LinkLabel() {
        Font font = getFont()
        Map attributes = font.getAttributes()
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON)
        setFont(font.deriveFont(attributes))
        setForeground(Color.BLUE)
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR))
        addMouseListener(new MouseAdapter() {
            @Override
            void mouseClicked(MouseEvent mouseEvent) {
                if (link != null) {
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(link)
                        } catch (IOException ignored) { }
                    }
                }
            }
        })
    }

}
