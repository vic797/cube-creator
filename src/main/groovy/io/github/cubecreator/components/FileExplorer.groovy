package io.github.cubecreator.components

import io.github.cubecreator.util.FileComparator
import io.github.cubecreator.util.Utils
import org.apache.commons.io.FileUtils
import org.apache.logging.log4j.LogManager

import javax.swing.ImageIcon
import javax.swing.JFileChooser
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPopupMenu
import javax.swing.JTree
import javax.swing.event.TreeExpansionEvent
import javax.swing.event.TreeWillExpandListener
import javax.swing.filechooser.FileSystemView
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeCellRenderer
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.ExpandVetoException
import javax.swing.tree.TreeCellRenderer
import javax.swing.tree.TreePath
import java.awt.Component
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetDragEvent
import java.awt.dnd.DropTargetDropEvent
import java.awt.dnd.DropTargetEvent
import java.awt.dnd.DropTargetListener
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.util.function.Consumer

class FileExplorer extends JTree implements TreeWillExpandListener, MouseListener, TreeCellRenderer, ActionListener, DropTargetListener {

    static final String ACTION_NEW_FILE = "FileExplorer.ACTION_NEW_FILE"
    static final String ACTION_NEW_DIRECTORY = "FileExplorer.ACTION_NEW_DIRECTORY"
    static final String ACTION_RENAME = "FileExplorer.ACTION_RENAME"
    static final String ACTION_DELETE = "FileExplorer.ACTION_DELETE"
    //static final String ACTION_OPEN_TEXT = "FileExplorer.ACTION_OPEN_TEXT"
    static final String ACTION_COPY = "FileExplorer.ACTION_COPY"
    static final String ACTION_MOVE = "FileExplorer.ACTION_MOVE"

    private File root
    private final DefaultTreeCellRenderer renderer
    private FileNode rootNode
    private DefaultTreeModel model
    Consumer<File> fileSelectionListener
    JPopupMenu popupMenu

    FileExplorer() {
        renderer = new DefaultTreeCellRenderer()
        addMouseListener(this)
        addTreeWillExpandListener(this)
        setModel(null)
        cellRenderer = this
        //rootVisible = false
        dragEnabled = true
        //noinspection GroovyUnusedAssignment
        def dt = new DropTarget(this, this)
    }

    void setRoot(File file) {
        this.root = file
        rootNode = new FileNode(file)
        model = new DefaultTreeModel(rootNode)
        setModel(model)
        if (root != null) {
            loadDirectory(root, rootNode)
        }
        componentPopupMenu = popupMenu
    }

    File getRoot() {
        return root
    }

    @Override
    void mouseClicked(MouseEvent mouseEvent) {
        if (fileSelectionListener != null && mouseEvent.getClickCount() == 2) {
            TreePath path = getPathForLocation(mouseEvent.x, mouseEvent.y)
            if (path != null && path.getLastPathComponent() instanceof FileNode) {
                setSelectionPath(path)
                FileNode node = (FileNode) path.getLastPathComponent()
                if (node.getFile().isFile()) {
                    fileSelectionListener.accept(node.getFile())
                }
            }
        }
    }

    @Override
    void mousePressed(MouseEvent mouseEvent) {
        if (mouseEvent.isPopupTrigger() && popupMenu != null) {
            TreePath path = getPathForLocation(mouseEvent.x, mouseEvent.y)
            if (path != null && path.getLastPathComponent() instanceof FileNode) {
                setSelectionPath(path)
                popupMenu.show(this, mouseEvent.getX(), mouseEvent.getY())
            }
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
    void treeWillExpand(TreeExpansionEvent treeExpansionEvent) throws ExpandVetoException {
        TreePath path = treeExpansionEvent.getPath()
        if (path != null && path.getLastPathComponent() instanceof FileNode) {
            FileNode node = (FileNode) path.getLastPathComponent()
            node.removeAllChildren()
            loadDirectory(node.getFile(), node)
        }
    }

    @Override
    void treeWillCollapse(TreeExpansionEvent treeExpansionEvent) throws ExpandVetoException {

    }

    @Override
    Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        JLabel label = (JLabel) renderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus)
        if (value instanceof FileNode) {
            File file = ((FileNode) value).getFile()
            label.setText(file.getName())
            if (file.isFile()) {
                label.icon = new ImageIcon(new URL("cube://icons/icons8-file-16.png"))
            } else {
                if (expanded) {
                    label.icon = new ImageIcon(new URL("cube://icons/icons8-opened-folder-16.png"))
                } else {
                    label.icon = new ImageIcon(new URL("cube://icons/icons8-folder-16.png"))
                }
            }
        }
        return label
    }

    void loadDirectory(File root, DefaultMutableTreeNode node) {
        File[] files = root.listFiles()
        if (files != null) {
            Arrays.sort(files, new FileComparator())
            for (int f = 0; f < files.length; f++) {
                node.add(new FileNode(files[f]))
            }
            model.reload(node)
        }
    }

    @Override
    void actionPerformed(ActionEvent actionEvent) {
        if (selectionPath != null) {
            if (selectionPath.lastPathComponent instanceof FileNode) {
                FileNode node = (FileNode) selectionPath.lastPathComponent
                switch (actionEvent.actionCommand) {
                    case ACTION_NEW_FILE: {
                        String name = JOptionPane.showInputDialog(null, "Type a name for the new file")
                        if (name) {
                            try {
                                if (node.file.isFile()) {
                                    File file = new File(node.file.parentFile, name)
                                    FileUtils.touch(file)
                                } else {
                                    File file = new File(node.file, name)
                                    FileUtils.touch(file)
                                }
                            } catch (e) {
                                LogManager.getLogger(getClass()).trace(Utils.dump(e))
                                JOptionPane.showMessageDialog(null, "Could not create file ${name}", "Error", JOptionPane.ERROR_MESSAGE)
                            }
                        }
                        break
                    }
                    case ACTION_NEW_DIRECTORY: {
                        String name = JOptionPane.showInputDialog(null, "Type a name for the new folder")
                        if (name) {
                            if (node.file.isFile()) {
                                File file = new File(node.file.parentFile, name)
                                if (!file.mkdir()) {
                                    JOptionPane.showMessageDialog(null, "Could not create folder ${name}", "Error", JOptionPane.ERROR_MESSAGE)
                                }
                            } else {
                                File file = new File(node.file, name)
                                if (!file.mkdir()) {
                                    JOptionPane.showMessageDialog(null, "Could not create folder ${name}", "Error", JOptionPane.ERROR_MESSAGE)
                                }
                            }
                        }
                        break
                    }
                    case ACTION_RENAME: {
                        String name = JOptionPane.showInputDialog(null, "Type a new name:", node.file.name)
                        if (name || name != node.file.name) {
                            try {
                                if (node.file.isFile()) {
                                    FileUtils.moveFile(node.file, new File(node.file.parentFile, name))
                                } else {
                                    FileUtils.moveDirectory(node.file, new File(node.file.parentFile, name))
                                }
                            } catch (IOException e) {
                                LogManager.getLogger(getClass()).trace(Utils.dump(e))
                                JOptionPane.showMessageDialog(null, "Could not rename ${node.file.name} to ${name}", "Error", JOptionPane.ERROR_MESSAGE)
                            }
                        }
                        break
                    }
                    case ACTION_DELETE: {
                        int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete ${node.file.name}", "Delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
                        if (option == JOptionPane.YES_OPTION) {
                            if (node.file.isFile()) {
                                if (!node.file.delete()) {
                                    JOptionPane.showMessageDialog(null, "Could not delete ${node.file.name}", "Error", JOptionPane.ERROR_MESSAGE)
                                }
                            } else {
                                try {
                                    FileUtils.deleteDirectory(node.file)
                                } catch (IOException e) {
                                    LogManager.getLogger(getClass()).trace(Utils.dump(e))
                                    JOptionPane.showMessageDialog(null, "Could not delete ${node.file.name}", "Error", JOptionPane.ERROR_MESSAGE)
                                }
                            }
                        }
                        break
                    }
                    case ACTION_COPY: {
                        copyFile(true)
                        break
                    }
                    case ACTION_MOVE: {
                        copyFile(false)
                        break
                    }
                }
                collapsePath(selectionPath)
            }
        }
    }

    @Override
    void dragEnter(DropTargetDragEvent dropTargetDragEvent) {
        requestFocusInWindow()
    }

    @Override
    void dragOver(DropTargetDragEvent dropTargetDragEvent) {
        int x = dropTargetDragEvent.location.x as int
        int y = dropTargetDragEvent.location.y as int
        setSelectionPath(getPathForLocation(x, y))
        expandPath(getPathForLocation(x, y))
    }

    @Override
    void dropActionChanged(DropTargetDragEvent dropTargetDragEvent) {

    }

    @Override
    void dragExit(DropTargetEvent dropTargetEvent) {

    }

    @Override
    void drop(DropTargetDropEvent dropTargetDropEvent) {
        if (getSelectionPath() == null) {
            return
        }
        try {
            dropTargetDropEvent.acceptDrop(DnDConstants.ACTION_COPY)
            List entries = (List) dropTargetDropEvent.getTransferable().getTransferData(DataFlavor.javaFileListFlavor)
            final File selected = ((FileNode) getSelectionPath().getLastPathComponent()).file
            entries.forEach(object -> {
                try {
                    File file = (File) object
                    if (file.isFile()) {
                        if (selected.isFile()) {
                            FileUtils.copyFile(file, new File(selected.getParentFile(), file.getName()))
                        } else {
                            FileUtils.copyFile(file, new File(selected, file.getName()))
                        }
                    } else {
                        if (selected.isFile()) {
                            FileUtils.copyDirectory(file, new File(selected.getParentFile(), file.getName()))
                        } else {
                            FileUtils.copyDirectory(file, new File(selected, file.getName()))
                        }
                    }
                } catch (IOException e) {
                    LogManager.getLogger(getClass()).trace(Utils.dump(e))
                }
            })
            collapsePath(getSelectionPath())
        } catch (e) {
            LogManager.getLogger(getClass()).trace(Utils.dump(e))
        }
    }

    private void copyFile(boolean keepOld) {
        if (getSelectionPath() != null) {
            JFileChooser chooser = new JFileChooser(new FileSystemView() {
                @Override
                File createNewFolder(File file) throws IOException {
                    return new File(file, "New directory")
                }

                @Override
                File[] getRoots() {
                    return new File[] { root }
                }

                @Override
                File getHomeDirectory() {
                    return root
                }
            })
            chooser.selectedFile = root
            chooser.fileHidingEnabled = false
            chooser.approveButtonText = "OK"
            chooser.multiSelectionEnabled = false
            chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                try {
                    File selected = ((FileNode) getSelectionPath().getLastPathComponent()).file
                    if (keepOld) {
                        if (selected.isFile()) {
                            FileUtils.copyFile(selected, new File(chooser.selectedFile, selected.name))
                        } else {
                            FileUtils.copyDirectory(selected, new File(chooser.selectedFile, selected.name))
                        }
                    } else {
                        if (selected.isFile()) {
                            FileUtils.moveFile(selected, new File(chooser.selectedFile, selected.name))
                        } else {
                            FileUtils.moveDirectory(selected, new File(chooser.selectedFile, selected.name))
                        }
                    }
                } catch (IOException e) {
                    LogManager.getLogger(getClass()).trace(Utils.dump(e))
                }
                collapsePath(getSelectionPath())
            }
        }
    }
}
