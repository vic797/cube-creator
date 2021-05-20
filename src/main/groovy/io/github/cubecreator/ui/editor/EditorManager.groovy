package io.github.cubecreator.ui.editor


import io.github.cubecreator.plugin.PluginManager
import io.github.cubecreator.ui.dialog.BaseDialog
import io.github.cubecreator.ui.dialog.EditorChooserDialog
import io.github.cubecreator.util.AbstractObjectDescriptor
import io.github.cubecreator.util.Utils
import org.apache.logging.log4j.LogManager

import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.PathMatcher

final class EditorManager {

    private static LinkedHashMap<String, Class<? extends AbstractEditor>> editorMap

    static void initializeEditors() {
        editorMap = new LinkedHashMap<>()
        editorMap.put("**/*.png", ImageEditor.class)
        editorMap.put("**/*.jpg", ImageEditor.class)
        editorMap.put("**/*.gif", ImageEditor.class)
        editorMap.put("**/*.md", MarkdownEditor.class)
        editorMap.put("**/*.markdown", MarkdownEditor.class)
        editorMap.put("**/tags/**/*.json", CodeEditor.class)
    }

    static void registerEditor(String matcher, Class<? extends AbstractEditor> editor) {
        if (editorMap == null) {
            initializeEditors()
        }
        editorMap.put(matcher, editor)
    }

    static AbstractEditor getEditor(String path) {
        List<AbstractObjectDescriptor<Class<? extends AbstractEditor>>> editors = new ArrayList<>()
        editors.addAll(PluginManager.getInstance().getEditors(path))
        if (editorMap == null) {
            initializeEditors()
        }
        for (String regex : editorMap.keySet()) {
            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:${regex}")
            Path filePath = new File(path).toPath()
            if (matcher.matches(filePath)) {
                editors.add(new DefaultEditorDescriptor(editorMap.get(regex)))
            }
        }
        print(editors.size())
        if (editors.size() == 0) {
            return new CodeEditor()
        }
        if (editors.size() == 1) {
            try {
                return editors.get(0).object.newInstance()
            } catch(e) {
                LogManager.getLogger(this.class).trace(Utils.dump(e))
                return new CodeEditor()
            }
        }
        EditorChooserDialog dialog = new EditorChooserDialog()
        dialog.setItems(editors)
        dialog.showDialog(null)
        if (dialog.result == BaseDialog.RESULT_OK) {
            try {
                return editors.get(dialog.selectedIndex()).object.newInstance()
            } catch(e) {
                LogManager.getLogger(this.class).trace(Utils.dump(e))
                return new CodeEditor()
            }
        }
        return new CodeEditor()
    }

}
