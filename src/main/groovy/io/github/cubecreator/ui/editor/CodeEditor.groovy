package io.github.cubecreator.ui.editor

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.github.cubecreator.completion.JsonCompletionProvider
import io.github.cubecreator.ui.menu.MenuBuilder
import io.github.cubecreator.util.SimpleDocumentListener
import io.github.cubecreator.util.Utils
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import org.apache.logging.log4j.LogManager
import org.fife.rsta.ui.search.FindDialog
import org.fife.rsta.ui.search.ReplaceDialog
import org.fife.rsta.ui.search.SearchEvent
import org.fife.rsta.ui.search.SearchListener
import org.fife.ui.autocomplete.AutoCompletion
import org.fife.ui.autocomplete.CompletionCellRenderer
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea
import org.fife.ui.rsyntaxtextarea.SyntaxConstants
import org.fife.ui.rtextarea.RTextScrollPane
import org.fife.ui.rtextarea.SearchContext
import org.fife.ui.rtextarea.SearchEngine
import org.fife.ui.rtextarea.SearchResult

import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JToolBar
import javax.swing.UIManager
import javax.swing.event.DocumentEvent
import java.awt.BorderLayout
import java.awt.Container
import java.awt.Dialog
import java.awt.event.ActionEvent
import java.nio.charset.StandardCharsets

class CodeEditor extends AbstractEditor implements SearchListener {

    public static final String ACTION_UNDO = "CodeEditor.ACTION_UNDO"
    public static final String ACTION_REDO = "CodeEditor.ACTION_REDO"
    public static final String ACTION_COPY = "CodeEditor.ACTION_COPY"
    public static final String ACTION_CUT = "CodeEditor.ACTION_CUT"
    public static final String ACTION_PASTE = "CodeEditor.ACTION_PASTE"
    public static final String ACTION_SELECT_ALL = "CodeEditor.ACTION_SELECT_ALL"
    public static final String ACTION_FIND = "CodeEditor.ACTION_FIND"
    public static final String ACTION_REPLACE = "CodeEditor.ACTION_REPLACE"

    private RSyntaxTextArea textArea
    private LinkedHashMap<String, String> extensionMap
    private JLabel caretPos
    private FindDialog findDialog
    private ReplaceDialog replaceDialog

    CodeEditor() {
        super()
        try {
            Gson gson = new Gson()
            String extensions = IOUtils.toString(new URL("cube://config/extension-map.json"), StandardCharsets.UTF_8)
            extensionMap = gson.fromJson(extensions, new TypeToken<LinkedHashMap<String, String>>() {}.getType())
        } catch (IOException e) {
            LogManager.getLogger(getClass()).trace(Utils.dump(e))
            extensionMap = new LinkedHashMap<>()
        }
        findDialog = new FindDialog((Dialog) null, this)
        replaceDialog = new ReplaceDialog((Dialog) null, this)
        replaceDialog.searchContext = findDialog.searchContext
    }

    void setSyntaxFromFileName(String name) {
        String ext = FilenameUtils.getExtension(name)
        textArea.setSyntaxEditingStyle(extensionMap.getOrDefault(ext, SyntaxConstants.SYNTAX_STYLE_NONE))
    }

    @Override
    protected void updateFile(File file) {
        super.updateFile(file)
        String ext = FilenameUtils.getExtension(getFile().getName())
        textArea.syntaxEditingStyle  = extensionMap.getOrDefault(ext, SyntaxConstants.SYNTAX_STYLE_NONE)
        if (textArea.syntaxEditingStyle.endsWith("mcfunction")) {
            loadCompletions("cube://autocomplete/mcfunction.json")
        }
    }

    @Override
    protected void createEditor(Container container) {
        container.setLayout(new BorderLayout())

        caretPos = new JLabel("1:1")

        textArea = new RSyntaxTextArea()
        textArea.animateBracketMatching = true
        textArea.autoIndentEnabled = true
        textArea.bracketMatchingEnabled = true
        textArea.closeCurlyBraces = true
        textArea.codeFoldingEnabled = true
        textArea.closeMarkupTags = true
        textArea.markOccurrences = true
        textArea.document.addDocumentListener(new SimpleDocumentListener() {
            @Override
            void update(DocumentEvent event) {
                modificationPerformed()
            }
        })
        textArea.addCaretListener(caretEvent -> caretPos.setText(String.format("%d:%d", textArea.getCaretLineNumber() + 1, textArea.getCaretOffsetFromLineStart() + 1)))

        container.add(new RTextScrollPane(textArea, true), BorderLayout.CENTER)

        JToolBar status = new JToolBar()
        status.setFloatable(false)
        status.add(caretPos)
        container.add(status, BorderLayout.SOUTH)
    }

    @Override
    protected byte[] getData() {
        return textArea.getText().getBytes(StandardCharsets.UTF_8)
    }

    @Override
    protected void setData(byte[] data) {
        textArea.setText(new String(data, StandardCharsets.UTF_8))
        textArea.setCaretPosition(0)
    }

    @Override
    protected void createToolBar(JToolBar toolBar) {
        try {
            URL url = new URL("cube://config/menu/code-editor.json")
            MenuBuilder.createMenu(toolBar, url, this)
        } catch (IOException e) {
            LogManager.getLogger(getClass()).trace(Utils.dump(e))
        }
    }

    @Override
    void actionPerformed(ActionEvent actionEvent) {
        switch (actionEvent.getActionCommand()) {
            case ACTION_UNDO: {
                textArea.undoLastAction()
                break
            }
            case ACTION_REDO: {
                textArea.redoLastAction()
                break
            }
            case ACTION_COPY: {
                textArea.copy()
                break
            }
            case ACTION_CUT: {
                textArea.cut()
                break
            }
            case ACTION_PASTE: {
                textArea.paste()
                break
            }
            case ACTION_SELECT_ALL: {
                textArea.selectAll()
                break
            }
            case ACTION_FIND: {
                findDialog.setVisible(true)
                break
            }
            case ACTION_REPLACE: {
                replaceDialog.setVisible(true)
                break
            }
            default: {
                super.actionPerformed(actionEvent)
                break
            }
        }
    }

    @Override
    void searchEvent(SearchEvent e) {
        SearchEvent.Type type = e.getType()
        SearchContext context = e.getSearchContext()
        switch (type) {
            case SearchEvent.Type.MARK_ALL: {
                SearchEngine.markAll(textArea, context)
                break
            }
            case SearchEvent.Type.FIND: {
                SearchResult result = SearchEngine.find(textArea, context)
                if (!result.wasFound() || result.isWrapped()) {
                    UIManager.getLookAndFeel().provideErrorFeedback(textArea)
                }
                break
            }
            case SearchEvent.Type.REPLACE: {
                SearchResult result = SearchEngine.replace(textArea, context)
                if (!result.wasFound() || result.isWrapped()) {
                    UIManager.getLookAndFeel().provideErrorFeedback(textArea)
                }
                break
            }
            case SearchEvent.Type.REPLACE_ALL: {
                SearchResult result = SearchEngine.replaceAll(textArea, context)
                JOptionPane.showMessageDialog(null, "${result.count} occurrences replaced.", "Find and Replace", JOptionPane.INFORMATION_MESSAGE)
                break
            }
            default: {
                SearchEngine.markAll(textArea, context)
                break
            }
        }
    }

    @Override
    String getSelectedText() {
        return textArea.getSelectedText()
    }

    void loadCompletions(String url) {
        LogManager.getLogger(getClass()).info("Loading completions from ${url}")
        JsonCompletionProvider provider = new JsonCompletionProvider()
        provider.setAutoActivationRules(true, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz@");
        provider.loadFromJson(new URL(url))
        AutoCompletion completion = new AutoCompletion(provider)
        completion.listCellRenderer = new CompletionCellRenderer()
        completion.parameterAssistanceEnabled = true
        completion.autoActivationEnabled = true
        completion.showDescWindow = true
        completion.autoCompleteSingleChoices = false
        completion.install(textArea)
    }
}
