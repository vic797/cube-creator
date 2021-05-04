package io.github.cubecreator.ui.editor

import io.github.cubecreator.completion.JsonCompletionProvider
import io.github.cubecreator.components.MarkdownView
import io.github.cubecreator.ui.dialog.MarkdownDialog
import io.github.cubecreator.ui.menu.MenuBuilder
import io.github.cubecreator.util.SimpleDocumentListener
import io.github.cubecreator.util.Utils
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
import javax.swing.JScrollPane
import javax.swing.JSplitPane
import javax.swing.JToolBar
import javax.swing.UIManager
import javax.swing.event.DocumentEvent
import javax.swing.text.BadLocationException
import java.awt.BorderLayout
import java.awt.Container
import java.awt.Dialog
import java.awt.event.ActionEvent
import java.nio.charset.StandardCharsets

class MarkdownEditor extends AbstractEditor implements SearchListener {

    public static final String ACTION_UNDO = "MarkdownEditor.ACTION_UNDO"
    public static final String ACTION_REDO = "MarkdownEditor.ACTION_REDO"
    public static final String ACTION_COPY = "MarkdownEditor.ACTION_COPY"
    public static final String ACTION_CUT = "MarkdownEditor.ACTION_CUT"
    public static final String ACTION_PASTE = "MarkdownEditor.ACTION_PASTE"
    public static final String ACTION_SELECT_ALL = "MarkdownEditor.ACTION_SELECT_ALL"
    public static final String ACTION_FIND = "MarkdownEditor.ACTION_FIND"
    public static final String ACTION_REPLACE = "MarkdownEditor.ACTION_REPLACE"
    public static final String ACTION_REFRESH = "MarkdownEditor.ACTION_REFRESH"
    public static final String ACTION_ADD_HEADER = "MarkdownEditor.ACTION_ADD_HEADER"
    public static final String ACTION_ADD_HEADER2 = "MarkdownEditor.ACTION_ADD_HEADER2"
    public static final String ACTION_ADD_HEADER3 = "MarkdownEditor.ACTION_ADD_HEADER3"
    public static final String ACTION_ADD_HEADER4 = "MarkdownEditor.ACTION_ADD_HEADER4"
    public static final String ACTION_ADD_HEADER5 = "MarkdownEditor.ACTION_ADD_HEADER5"
    public static final String ACTION_ADD_HEADER6 = "MarkdownEditor.ACTION_ADD_HEADER6"
    public static final String ACTION_ADD_BOLD = "MarkdownEditor.ACTION_ADD_BOLD"
    public static final String ACTION_ADD_ITALIC = "MarkdownEditor.ACTION_ADD_ITALIC"
    public static final String ACTION_ADD_STRIKETHROUGH = "MarkdownEditor.ACTION_ADD_STRIKETHROUGH"
    public static final String ACTION_ADD_CODE = "MarkdownEditor.ACTION_ADD_CODE"
    public static final String ACTION_ADD_BULLETED_LIST = "MarkdownEditor.ACTION_ADD_BULLETED_LIST"
    public static final String ACTION_ADD_NUMBERED_LIST = "MarkdownEditor.ACTION_ADD_NUMBERED_LIST"
    public static final String ACTION_ADD_TABLE = "MarkdownEditor.ACTION_ADD_TABLE"
    public static final String ACTION_ADD_LINK = "MarkdownEditor.ACTION_ADD_LINK"
    public static final String ACTION_ADD_IMAGE = "MarkdownEditor.ACTION_ADD_IMAGE"

    private RSyntaxTextArea textArea
    private MarkdownView markdownView
    private JLabel caretPos
    private FindDialog findDialog
    private ReplaceDialog replaceDialog

    MarkdownEditor() {
        super()
        findDialog = new FindDialog((Dialog) null, this)
        replaceDialog = new ReplaceDialog((Dialog) null, this)
        replaceDialog.searchContext = findDialog.searchContext
    }

    @Override
    protected void createEditor(Container container) {
        container.setLayout(new BorderLayout())

        caretPos = new JLabel("1:1")

        textArea = new RSyntaxTextArea()
        textArea.syntaxEditingStyle = SyntaxConstants.SYNTAX_STYLE_MARKDOWN
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

        markdownView = new MarkdownView()

        JSplitPane splitPane = new JSplitPane()
        splitPane.leftComponent = new RTextScrollPane(textArea, true)
        splitPane.rightComponent = new JScrollPane(markdownView)
        splitPane.setDividerLocation(0.5d)

        container.add(splitPane, BorderLayout.CENTER)

        JToolBar status = new JToolBar()
        status.setFloatable(false)
        status.add(caretPos)
        container.add(status, BorderLayout.SOUTH)
        loadCompletions()
    }

    @Override
    void save() throws IOException {
        super.save()
        markdownView.setMarkdown(textArea.text)
    }

    @Override
    void load() throws IOException {
        super.load()
        markdownView.setMarkdown(textArea.text)
    }

    @Override
    protected byte[] getData() {
        return textArea.getText().getBytes(StandardCharsets.UTF_8)
    }

    @Override
    protected void setData(byte[] data) {
        textArea.setText(new String(data, StandardCharsets.UTF_8))
        textArea.setCaretPosition(0)
        markdownView.setMarkdown(new String(data, StandardCharsets.UTF_8))
    }

    @Override
    protected void createToolBar(JToolBar toolBar) {
        try {
            URL url = new URL("cube://config/menu/markdown-editor.json")
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
            case ACTION_REFRESH: {
                markdownView.setMarkdown(textArea.text)
                break
            }
            case ACTION_ADD_HEADER: {
                addCharactersAndMove("# ", 0)
                break
            }
            case ACTION_ADD_HEADER2: {
                addCharactersAndMove("## ", 0)
                break
            }
            case ACTION_ADD_HEADER3: {
                addCharactersAndMove("### ", 0)
                break
            }
            case ACTION_ADD_HEADER4: {
                addCharactersAndMove("#### ", 0)
                break
            }
            case ACTION_ADD_HEADER5: {
                addCharactersAndMove("##### ", 0)
                break
            }
            case ACTION_ADD_HEADER6: {
                addCharactersAndMove("###### ", 0)
                break
            }
            case ACTION_ADD_BOLD: {
                addCharactersAndMove("****", 2)
                break
            }
            case ACTION_ADD_ITALIC: {
                addCharactersAndMove("**", 1)
                break
            }
            case ACTION_ADD_STRIKETHROUGH: {
                addCharactersAndMove("~~~~", 2)
                break
            }
            case ACTION_ADD_CODE: {
                addCharactersAndMove("``", 1)
                break
            }
            case ACTION_ADD_BULLETED_LIST: {
                addCharactersAndMove("- ", 0)
                break
            }
            case ACTION_ADD_NUMBERED_LIST: {
                addCharactersAndMove("1. ", 0)
                break
            }
            case ACTION_ADD_TABLE: {
                String src = MarkdownDialog.showTableDialog(null)
                try {
                    textArea.document.insertString(textArea.caretPosition, src, null)
                } catch (BadLocationException ignored) {}
                break
            }
            case ACTION_ADD_LINK: {
                String src = MarkdownDialog.showLinkDialog(null)
                try {
                    textArea.document.insertString(textArea.caretPosition, src, null)
                } catch (BadLocationException ignored) {}
                break
            }
            case ACTION_ADD_IMAGE: {
                String src = MarkdownDialog.showImageDialog(null)
                try {
                    textArea.document.insertString(textArea.caretPosition, src, null)
                } catch (BadLocationException ignored) {}
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

    private void addCharactersAndMove(String chars, int movementCount) {
        try {
            textArea.document.insertString(textArea.caretPosition, chars, null)
            textArea.caretPosition = textArea.caretPosition - movementCount
        } catch (BadLocationException ignored) {}
    }

    void loadCompletions() {
        LogManager.getLogger(getClass()).info("Loading Markdown template completions")
        JsonCompletionProvider provider = new JsonCompletionProvider()
        provider.setAutoActivationRules(true, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz");
        provider.loadFromJson(new URL("cube://autocomplete/markdown.json"))
        AutoCompletion completion = new AutoCompletion(provider)
        completion.listCellRenderer = new CompletionCellRenderer()
        completion.parameterAssistanceEnabled = true
        completion.autoActivationEnabled = true
        completion.showDescWindow = true
        completion.autoCompleteSingleChoices = false
        completion.install(textArea)
    }
}
