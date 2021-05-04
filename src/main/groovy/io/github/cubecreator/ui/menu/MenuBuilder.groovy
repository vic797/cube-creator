package io.github.cubecreator.ui.menu

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.cubecreator.util.Utils
import org.apache.commons.io.IOUtils
import org.apache.logging.log4j.LogManager

import javax.swing.ImageIcon
import javax.swing.JButton
import javax.swing.JMenu
import javax.swing.JMenuBar
import javax.swing.JMenuItem
import javax.swing.JPopupMenu
import javax.swing.JToolBar
import javax.swing.KeyStroke
import java.awt.event.ActionListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.nio.charset.StandardCharsets
import java.util.function.Consumer

final class MenuBuilder {

    static Consumer<JMenu> menuPreprocessor

    private static List<MenuItem> getMenu(URL url) throws IOException {
        LogManager.getLogger(getClass()).info("Generating menu. Attempting to load \"${url}\"")
        String json = IOUtils.toString(url, StandardCharsets.UTF_8)
        Gson gson = new Gson()
        List<MenuItem> items = gson.fromJson(json, new TypeToken<ArrayList<MenuItem>>() {}.getType())
        LogManager.getLogger(getClass()).info("Generated menu. Successfully loaded \"${url}\"")
        items
    }

    static void createMenu(JPopupMenu menu, URL url, ActionListener listener) {
        List<MenuItem> items = getMenu(url)
        for (MenuItem item : items) {
            if (item.getText() == "-") {
                menu.addSeparator()
            } else {
                if (item.menu.size() == 0) {
                    JMenuItem menuItem = new JMenuItem(item.getText())
                    menuItem.name = item.name
                    menuItem.accelerator = KeyStroke.getKeyStroke(item.getAccelerator())
                    menuItem.actionCommand = item.action
                    if (item.iconUrl != null) {
                        try {
                            menuItem.icon = new ImageIcon(new URL(item.getIconUrl()))
                        } catch (MalformedURLException e) {
                            LogManager.getLogger(getClass()).trace(Utils.dump(e))
                            menuItem.icon = null
                        }
                    }
                    menuItem.addActionListener(listener)
                    menu.add(menuItem)
                } else {
                    JMenu subMenuItem = new JMenu(item.getText())
                    subMenuItem.name = item.name
                    createMenu(subMenuItem, item, listener)
                    if (item.isEnablePreprocessor() && menuPreprocessor != null) {
                        menuPreprocessor.accept(subMenuItem)
                    }
                    menu.add(subMenuItem)
                }
            }
        }
    }

    static void createMenu(JMenu menu, MenuItem[] subMenu, ActionListener listener) {
        for (int i = 0; i < subMenu.length; i++) {
            MenuItem item = subMenu[i]
            if (item.getText() == "-") {
                menu.addSeparator()
            } else {
                if (item.menu.size() == 0) {
                    JMenuItem menuItem = new JMenuItem(item.getText())
                    menuItem.name = item.name
                    menuItem.accelerator = KeyStroke.getKeyStroke(item.getAccelerator())
                    menuItem.actionCommand = item.action
                    if (item.iconUrl != null) {
                        try {
                            menuItem.icon = new ImageIcon(new URL(item.getIconUrl()))
                        } catch (MalformedURLException e) {
                            LogManager.getLogger(getClass()).trace(Utils.dump(e))
                            menuItem.icon = null
                        }
                    }
                    menuItem.addActionListener(listener)
                    menu.add(menuItem)
                } else {
                    JMenu subMenuItem = new JMenu(item.getText())
                    subMenuItem.name = item.name
                    createMenu(subMenuItem, item, listener)
                    if (item.isEnablePreprocessor() && menuPreprocessor != null) {
                        menuPreprocessor.accept(subMenuItem)
                    }
                    menu.add(subMenuItem)
                }
            }
        }
    }

    static void createMenu(JMenu menu, MenuItem subMenu, ActionListener listener) {
        for (int i = 0; i < subMenu.menu.size(); i++) {
            MenuItem item = subMenu.menu.get(i)
            if (item.text == "-") {
                menu.addSeparator()
            } else {
                if (item.menu.size() == 0) {
                    JMenuItem menuItem = new JMenuItem(item.text)
                    menuItem.name = item.name
                    menuItem.accelerator = KeyStroke.getKeyStroke(item.accelerator)
                    menuItem.actionCommand = item.action
                    if (item.iconUrl != null) {
                        try {
                            menuItem.icon = new ImageIcon(new URL(item.getIconUrl()))
                        } catch (MalformedURLException e) {
                            LogManager.getLogger(getClass()).trace(Utils.dump(e))
                            menuItem.icon = null
                        }
                    }
                    menuItem.addActionListener(listener)
                    menu.add(menuItem)
                } else {
                    JMenu subMenuItem = new JMenu(item.getText())
                    subMenuItem.name = item.name
                    createMenu(subMenuItem, item, listener)
                    if (item.isEnablePreprocessor() && menuPreprocessor != null) {
                        menuPreprocessor.accept(subMenuItem)
                    }
                    menu.add(subMenuItem)
                }
            }
        }
    }

    static void createMenu(JPopupMenu menu, MenuItem subMenu, ActionListener listener) {
        for (int i = 0; i < subMenu.menu.size(); i++) {
            MenuItem item = subMenu.menu.get(i)
            if (item.text == "-") {
                menu.addSeparator()
            } else {
                if (item.menu.size() == 0) {
                    JMenuItem menuItem = new JMenuItem(item.text)
                    menuItem.name = item.name
                    menuItem.accelerator = KeyStroke.getKeyStroke(item.accelerator)
                    menuItem.actionCommand = item.action
                    if (item.iconUrl != null) {
                        try {
                            menuItem.icon = new ImageIcon(new URL(item.getIconUrl()))
                        } catch (MalformedURLException e) {
                            LogManager.getLogger(getClass()).trace(Utils.dump(e))
                            menuItem.icon = null
                        }
                    }
                    menuItem.addActionListener(listener)
                    menu.add(menuItem)
                } else {
                    JMenu subMenuItem = new JMenu(item.getText())
                    subMenuItem.name = item.name
                    createMenu(subMenuItem, item, listener)
                    if (item.isEnablePreprocessor() && menuPreprocessor != null) {
                        menuPreprocessor.accept(subMenuItem)
                    }
                    menu.add(subMenuItem)
                }
            }
        }
    }

    static void createMenu(JMenuBar menuBar, URL url, ActionListener listener) throws IOException {
        List<MenuItem> items = getMenu(url)
        for (MenuItem item : items) {
            JMenu menu = new JMenu(item.getText())
            menu.setName(item.getName())
            createMenu(menu, item, listener)
            if (item.isEnablePreprocessor() && menuPreprocessor != null) {
                menuPreprocessor.accept(menu)
            }
            menuBar.add(menu)
        }
    }

    static void createMenu(JToolBar toolBar, URL url, ActionListener listener) throws IOException {
        List<MenuItem> items = getMenu(url)
        for (MenuItem item : items) {
            if (item.getText() != null && item.getText() == "-") {
                toolBar.addSeparator()
            } else {
                JButton button = new JButton()
                button.text = item.text
                button.toolTipText = item.toolTip
                button.icon = new ImageIcon(new URL(item.getIconUrl()))
                if (item.menu.size() != 0) {
                    JPopupMenu popupMenu = new JPopupMenu()
                    createMenu(popupMenu, item, listener)
                    button.addMouseListener(new MouseAdapter() {
                        @Override
                        void mouseClicked(MouseEvent mouseEvent) {
                            popupMenu.show((JButton) mouseEvent.source, mouseEvent.x, mouseEvent.y)
                        }
                    })
                } else {
                    button.actionCommand = item.getAction()
                    button.addActionListener(listener)
                }
                button.name = item.name
                toolBar.add(button)
            }
        }
    }

}
