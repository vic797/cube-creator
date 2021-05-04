package io.github.cubecreator.components

import javax.swing.tree.DefaultMutableTreeNode

class FileNode extends DefaultMutableTreeNode {

    private final File file

    FileNode(File file) {
        this.file = file
    }

    File getFile() {
        return file
    }

    @Override
    @Deprecated
    void setUserObject(Object o) { }

    @Override
    @Deprecated
    Object getUserObject() {
        return null
    }

    @Override
    boolean isLeaf() {
        return file.isFile()
    }

    @Override
    boolean getAllowsChildren() {
        return file.isDirectory()
    }

    @Override
    @Deprecated
    void setAllowsChildren(boolean b) { }

}
