package io.github.cubecreator.plugin

import com.google.gson.annotations.SerializedName
import groovy.transform.PackageScope
import io.github.cubecreator.ui.menu.MenuItem

import javax.imageio.ImageIO
import javax.swing.Icon
import javax.swing.ImageIcon

/**
 * This object holds the information about this plugin in the <code>plugin-info.json</code>. The following is a trimmed version of the <b>Core</b> plugin.
 *
 * <pre><code>
 *     {
 *          "name": "Cube Creator Core Plugin",
 *          "display_name": "Core Plugin",
 *          "author": "The Cube Creator Author",
 *          "version": "1.0",
 *          "id": "2f1dd1d7-8c9c-4343-b2d0-d718b4341539",
 *          "link": "https://cubecreator.github.io/",
 *          "description": "This is the default plugin with the basic editors used to create data packs.",
 *          "entry": "Main.groovy",
 *          "menu": []
 *     }
 */
final class PluginInfo {

    /**
     * The name of this plugin
     */
    String name
    /**
     * The name to show on the menu for this plugin
     */
    @SerializedName("display_name")
    String displayName
    /**
     * The version of this plugin
     */
    String version
    /**
     * The author name of this plugin
     */
    String author
    /**
     * The ID of this plugin, must be an <a href="https://en.wikipedia.org/wiki/Universally_unique_identifier" target="_blank">UUID</a>
     */
    String id
    /**
     * The menu for this plugin
     */
    MenuItem[] menu
    /**
     * The home page of this plugin
     */
    String link
    /**
     * The description of this plugin
     */
    String description
    /**
     * The main entry file for this plugin
     */
    String entry
    private transient File location
    /**
     * The location of the <code>data</code> directory of this plugin
     */
    transient File data
    /**
     * The small icon used on this plugin
     */
    transient Icon smallIcon
    /**
     * The icon used on this plugin
     */
    transient Icon icon
    private transient Plugin plugin
    /**
     * The context of this plugin
     */
    transient PluginContext context

    PluginInfo() {
        id = UUID.randomUUID().toString()
        menu = new MenuItem[0]
    }

    File getLocation() {
        return location
    }

    @PackageScope
    void setLocation(File location) {
        this.location = location
        try {
            smallIcon = new ImageIcon(ImageIO.read(new File(location, "icon_small.png")))
        } catch (IOException ignored) {}
        try {
            icon = new ImageIcon(ImageIO.read(new File(location, "icon.png")))
        } catch (IOException ignored) {}
        data = new File(location, "data")
        if (!data.exists()) {
            data = null
        }
    }

    Plugin getPlugin() {
        plugin
    }

    void initialize() {
        GroovyScriptEngine engine = new GroovyScriptEngine(new String[] {
            location.toString() + File.separator + "plugin"
        })
        Binding binding = new Binding()
        plugin = (Plugin) engine.run(entry, binding)
        plugin.info = this
    }

}
