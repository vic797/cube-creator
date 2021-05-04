package io.github.cubecreator.components

import io.github.cubecreator.util.Utils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.apache.logging.log4j.LogManager
import org.commonmark.Extension
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.ext.ins.InsExtension
import org.commonmark.ext.task.list.items.TaskListItemsExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

import javax.swing.JEditorPane
import javax.swing.JOptionPane
import javax.swing.event.HyperlinkEvent
import javax.swing.text.html.HTMLEditorKit
import java.nio.charset.StandardCharsets

class MarkdownView extends JEditorPane {

    MarkdownView() {
        editable = false
        editorKit = new HTMLEditorKit()
        addHyperlinkListener(hyperlinkEvent -> {
            if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                Utils.openUrl(hyperlinkEvent.getURL().toString())
            }
        })
    }

    void setMarkdown(String markdown) {
        List<Extension> extensions = Arrays.asList(
                TablesExtension.create(),
                InsExtension.create(),
                StrikethroughExtension.create(),
                TaskListItemsExtension.create()
        )
        Parser parser = Parser.builder()
                .extensions(extensions)
                .build()
        HtmlRenderer renderer = HtmlRenderer.builder()
                .extensions(extensions)
                .build()
        String html = renderer.render(parser.parse(markdown))
        setText(String.format("<html><head></head><body>%s</body></html>", html))
    }

    void setMarkdown(final URL url) {
        new Thread(() -> {
            try {
                URLConnection connection = url.openConnection()
                connection.connect()
                InputStream stream = connection.getInputStream()
                String md = IOUtils.toString(stream, StandardCharsets.UTF_8)
                setMarkdown(md)
                stream.close()
            } catch (IOException e) {
                LogManager.getLogger(this.class).trace(Utils.dump(e))
                JOptionPane.showMessageDialog(null, String.format("Could not load %s", url), "Error", JOptionPane.ERROR_MESSAGE)
            }
        }).start()
    }

    void setMarkdown(File file) {
        try {
            setMarkdown(FileUtils.readFileToString(file, StandardCharsets.UTF_8))
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Could not load ${file}", "Error", JOptionPane.ERROR_MESSAGE)
        }
    }

}
