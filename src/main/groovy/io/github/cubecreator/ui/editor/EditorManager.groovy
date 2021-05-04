package io.github.cubecreator.ui.editor

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
    }

    static void registerEditor(String matcher, Class<? extends AbstractEditor> editor) {
        if (editorMap == null) {
            initializeEditors()
        }
        editorMap.put(matcher, editor)
    }

    static AbstractEditor getEditor(String path) {
        if (editorMap == null) {
            initializeEditors()
        }
        for (String regex : editorMap.keySet()) {
            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:${regex}")
            Path filePath = new File(path).toPath()
            if (matcher.matches(filePath)) {
                try {
                    return editorMap.get(regex).newInstance()
                } catch (e) {
                    LogManager.getLogger(EditorManager.getClass()).trace(Utils.dump(e))
                    return new CodeEditor()
                }
            }
        }
        new CodeEditor()
    }

}
