package io.github.cubecreator

import com.formdev.flatlaf.FlatDarculaLaf
import com.formdev.flatlaf.FlatDarkLaf
import com.formdev.flatlaf.FlatIntelliJLaf
import com.formdev.flatlaf.FlatLightLaf
import io.github.cubecreator.plugin.PluginManager
import io.github.cubecreator.ui.Splash
import io.github.cubecreator.ui.Workbench
import io.github.cubecreator.ui.editor.EditorManager
import io.github.cubecreator.uri.DataURLStreamHandlerFactory
import io.github.cubecreator.util.Settings
import io.github.cubecreator.util.Utils
import org.apache.logging.log4j.LogManager
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory

import javax.swing.SwingUtilities

final class Launcher {

    @SuppressWarnings('SpellCheckingInspection')
    static void main(String[] args) {
        Thread.defaultUncaughtExceptionHandler = (Thread thread, Throwable throwable) -> {
            LogManager.getLogger(Launcher.class).trace(Utils.dump(throwable))
        }
        String theme = Settings.get("config.properties").get("theme", "light")
        switch (theme) {
            case "light": {
                FlatLightLaf.install()
                break
            }
            case "dark": {
                FlatDarkLaf.install()
                break
            }
            case "idea": {
                FlatIntelliJLaf.install()
                break
            }
            case "darkula": {
                FlatDarculaLaf.install()
                break
            }
            default: {
                FlatLightLaf.install()
                break
            }
        }
        Locale.setDefault(Locale.ENGLISH)
        URL.setURLStreamHandlerFactory(new DataURLStreamHandlerFactory())
        AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance()
        atmf.putMapping("text/mcfunction", "io.github.cubecreator.util.FunctionTokenMaker")
        SwingUtilities.invokeLater(() -> {
            final Splash splash = new Splash()
            Thread t = new Thread(() -> {
                splash.setMessage("Preparing editors")
                EditorManager.initializeEditors()
                splash.setMessage("Loading plugins")
                PluginManager.getInstance().loadPlugins()
                splash.setMessage("Loading")
                SwingUtilities.invokeLater(() -> {
                    Workbench workbench = new Workbench()
                    workbench.setVisible(true)
                    splash.toFront()
                    splash.visible = false
                    splash.dispose()
                })
            })
            t.daemon = true
            t.start()
            splash.visible = true
        })
    }

}
