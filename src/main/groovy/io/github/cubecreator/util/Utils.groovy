package io.github.cubecreator.util

import javax.swing.JOptionPane
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection

final class Utils {

    private Utils() {}

    static String dump(Throwable throwable) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream()
            PrintStream printStream = new PrintStream(stream)
            throwable.printStackTrace(printStream)
            printStream.flush()
            return stream.toString()
        } catch (e) {
            return ""
        }
    }

    static void openUrl(String url) {
        if (Desktop.isDesktopSupported()) {
            String[] choices = new String[] {
                "Cancel",
                "Copy",
                "Open"
            }
            int choice = JOptionPane.showOptionDialog(null, "Do you want Cube Creator to open this website?\n\n${url}", "Open website", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, choices, choices[0])
            switch (choice) {
                case 0: {
                    break
                }
                case 1: {
                    Clipboard clipboard = Toolkit.defaultToolkit.getSystemClipboard()
                    StringSelection selection = new StringSelection(url)
                    clipboard.setContents(selection, selection)
                    break
                }
                case 2: {
                    Desktop desktop = Desktop.getDesktop()
                    try {
                        desktop.browse(new URI(url))
                    } catch (IOException | URISyntaxException ignored) {
                    }
                    break
                }
            }
        }
    }

}
