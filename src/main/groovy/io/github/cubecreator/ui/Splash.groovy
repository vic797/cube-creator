package io.github.cubecreator.ui

import io.github.cubecreator.components.ImageView

import javax.swing.ImageIcon
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.border.EmptyBorder
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Container
import java.awt.Dimension
import java.awt.Font

class Splash extends JDialog {

    private JLabel state

    Splash() {
        Container container = new Container()
        container.layout = new BorderLayout()

        ImageView view = new ImageView()
        try {
            view.setImage(getClass().getClassLoader().getResourceAsStream("splash.jpg"))
        } catch (e) {
            e.printStackTrace()
        }
        view.layout = new BorderLayout()
        container.add(view, BorderLayout.CENTER)

        JLabel program = new JLabel("Cube Creator")
        program.font = program.font.deriveFont(Font.BOLD, 80f)
        program.icon = new ImageIcon(new URL("cube://icons/icons8-cube-80.png"))
        program.border = new EmptyBorder(12, 12, 12, 12)
        program.foreground = Color.WHITE
        view.add(program, BorderLayout.NORTH)

        state = new JLabel("Loading...")
        state.foreground = Color.WHITE
        state.border = new EmptyBorder(8, 8, 8, 8)
        view.add(state, BorderLayout.SOUTH)

        title = "Cube Creator"
        undecorated = true
        modal = true
        resizable = false
        size = new Dimension(640, 427)
        contentPane = container
        locationRelativeTo = null
    }

    void setMessage(String msg) {
        state.text = msg
    }

}
