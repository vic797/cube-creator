package io.github.cubecreator.ui.editor

import io.github.cubecreator.components.ImageView
import io.github.cubecreator.ui.menu.MenuBuilder
import io.github.cubecreator.util.EventTransport
import io.github.cubecreator.util.Utils
import org.apache.logging.log4j.LogManager
import org.greenrobot.eventbus.EventBus

import javax.imageio.ImageIO
import javax.swing.JToolBar
import java.awt.BorderLayout
import java.awt.Container
import java.awt.Graphics2D
import java.awt.Image
import java.awt.event.ActionEvent
import java.awt.image.BufferedImage

class ImageEditor extends AbstractEditor {

    static final ACTION_ZOOM_IN = "ImageEditor.ACTION_ZOOM_IN"
    static final ACTION_ZOOM_OUT = "ImageEditor.ACTION_ZOOM_OUT"
    static final ACTION_ZOOM_RESET = "ImageEditor.ACTION_ZOOM_RESET"
    static final ACTION_CENTER = "ImageEditor.ACTION_CENTER"
    static final ACTION_EDIT_MCMETA = "ImageEditor.ACTION_EDIT_MCMETA"

    private ImageView imageView

    @Override
    protected void createEditor(Container container) {
        imageView = new ImageView()
        container.setLayout(new BorderLayout())
        container.add(imageView, BorderLayout.CENTER)
    }

    @Override
    protected byte[] getData() {
        Image img = imageView.getImage()
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB)
        Graphics2D bGr = bimage.createGraphics()
        bGr.drawImage(img, 0, 0, null)
        bGr.dispose()
        ByteArrayOutputStream stream = new ByteArrayOutputStream()
        ImageIO.write(bimage, "PNG", stream)
        stream.toByteArray()
    }

    @Override
    protected void setData(byte[] data) {
        imageView.setImage(new ByteArrayInputStream(data))
    }

    @Override
    protected void createToolBar(JToolBar toolBar) {
        try {
            URL url = new URL("cube://config/menu/image-editor.json")
            MenuBuilder.createMenu(toolBar, url, this)
        } catch (IOException e) {
            LogManager.getLogger(getClass()).trace(Utils.dump(e))
        }
    }

    @Override
    void actionPerformed(ActionEvent actionEvent) {
        switch (actionEvent.actionCommand) {
            case ACTION_ZOOM_IN: {
                imageView.increaseZoom(0.1f)
                break
            }
            case ACTION_ZOOM_OUT: {
                imageView.increaseZoom(-0.1f)
                break
            }
            case ACTION_ZOOM_RESET: {
                imageView.resetZoom()
                break
            }
            case ACTION_CENTER: {
                imageView.setOffset(0, 0)
                break
            }
            case ACTION_EDIT_MCMETA: {
                File meta = new File(file.toString() + ".mcmeta")
                if (meta.exists()) {
                    EventBus.getDefault().post(new EventTransport(EventTransport.EVENT_OPEN_FILE, meta))
                }
                break
            }
            default: {
                super.actionPerformed(actionEvent)
                break
            }
        }
    }
}
