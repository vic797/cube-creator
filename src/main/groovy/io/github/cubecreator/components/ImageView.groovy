package io.github.cubecreator.components

import javax.imageio.ImageIO
import javax.swing.JComponent
import java.awt.Graphics
import java.awt.Image
import java.awt.Point
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import java.awt.event.MouseWheelEvent
import java.awt.event.MouseWheelListener

class ImageView extends JComponent implements MouseListener, MouseMotionListener, MouseWheelListener {

    private Image image
    private Point location
    private float zoom
    private int xOffset
    private int yOffset

    ImageView() {
        addMouseListener(this)
        addMouseMotionListener(this)
        addMouseWheelListener(this)
        location = new Point(0, 0)
        focusable = true
        xOffset = 0
        yOffset = 0
    }

    void setImage(InputStream stream) throws IOException {
        setImage(ImageIO.read(stream))
    }

    void setImage(Image image) {
        this.image = image
        zoom = 1f
        yOffset = 0
        xOffset = 0
        calculate()
        repaint()
    }

    Image getImage() {
        this.image
    }

    void setZoom(float zoom) {
        this.zoom = zoom
        calculate()
        repaint()
    }

    float getZoom() {
        this.zoom
    }

    void increaseZoom(float factor) {
        this.zoom += factor
        calculate()
        repaint()
    }

    void resetZoom() {
        this.zoom = 1f
        calculate()
        repaint()
    }

    void setOffset(int x, int y) {
        xOffset = x
        yOffset = y
        calculate()
        repaint()
    }

    int[] getOffset() {
        new int[] { xOffset, yOffset }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        calculate()
        graphics.drawImage(image, location.x as int, location.y as int, (image.getWidth(null) * zoom) as int, (image.getHeight(null) * zoom) as int, null)
        super.paintComponent(graphics)
    }

    @Override
    void mouseClicked(MouseEvent mouseEvent) {

    }

    @Override
    void mousePressed(MouseEvent mouseEvent) {
        if (mouseEvent.button == MouseEvent.BUTTON1) {
            requestFocusInWindow()
        } else if (mouseEvent.button == MouseEvent.BUTTON2) {
            zoom = 1f
            xOffset = 0
            yOffset = 0
            calculate()
            repaint()
        }
    }

    @Override
    void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    void mouseExited(MouseEvent mouseEvent) {

    }

    @Override
    void mouseDragged(MouseEvent mouseEvent) {

    }

    @Override
    void mouseMoved(MouseEvent mouseEvent) {

    }

    @Override
    void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
        if (mouseWheelEvent.isControlDown()) {
            zoom += mouseWheelEvent.wheelRotation / 10
        } else if (mouseWheelEvent.isAltDown()) {
            xOffset += mouseWheelEvent.wheelRotation * 10
        } else if (mouseWheelEvent.isShiftDown()){
            yOffset += mouseWheelEvent.wheelRotation * 10
        }
        calculate()
        repaint()
    }

    private void calculate() {
        int x = (width / 2) - ((image.getWidth(null) * zoom) / 2) as int
        int y = (height / 2) - ((image.getHeight(null) * zoom) / 2) as int
        location.x = x + xOffset
        location.y = y + yOffset
    }
}
