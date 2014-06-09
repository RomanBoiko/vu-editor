package vu.editor;

import java.awt.Color;

import javax.swing.JTextArea;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

import vu.editor.Buffer.BufferState;

public class Driver {
	private final Gui gui = new Gui();
	private final HelpPerspective helpPerspective = new HelpPerspective(this);
	private final EditorPerspective editorPerspective = new EditorPerspective(this);
	private final FileExplorerPerspective fileExplorerPerspective = new FileExplorerPerspective(this);
	private final BuffersPerspective buffersPerspective = new BuffersPerspective(this);
	private final Buffers buffers = new Buffers();
	private Perspective currentPerspective = new Perspective() { };

	void showGui() {
		this.gui.show();
	}

	private void setCurrentPerspective(Perspective newPerspective) {
		currentPerspective.actionOnExitFromPerspective();
		currentPerspective = newPerspective;
	}
	void loadEditorView(Buffer resource) {
		setCurrentPerspective(editorPerspective);
		editorPerspective.loadResource(resource);
		buffers.addCurrentBuffer(resource);
	}
	void loadEditorView() {
		setCurrentPerspective(editorPerspective);
		editorPerspective.loadResource(buffers.currentBuffer());
	}
	void loadHelpView() {
		setCurrentPerspective(helpPerspective);
		this.helpPerspective.loadHelpView();
	}
	void loadFileExplorerView() {
		setCurrentPerspective(fileExplorerPerspective);
		this.fileExplorerPerspective.loadExplorerView();
	}
	void loadBuffersView() {
		setCurrentPerspective(buffersPerspective);
		this.buffersPerspective.loadBuffersView();
	}

	void setText(String text) {
		inputArea().setText(text);
	}

	String text() {
		return inputArea().getText();
	}

	int selectionStart() {
		return inputArea().getSelectionStart();
	}

	int selectionEnd() {
		return inputArea().getSelectionEnd();
	}

	void replaceRange(String replacement, int start, int end) {
		inputArea().replaceRange(replacement, start, end);
	}
	void insert(String textToInsert, int position) {
		inputArea().insert(textToInsert, position);
	}
	void setCursorPosition(int position) {
		inputArea().setCaretPosition(position);
	}
	void setSelectionStart(int position) {
		inputArea().setSelectionStart(position);
	}
	void setSelectionEnd(int position) {
		inputArea().setSelectionEnd(position);
	}
	
	void setTitle(String title) {
		gui.mainFrame.setTitle(title);
	}

	private static final HighlightPainter STATUS_BAR_READONLY_HIGHLIGHTER = new DefaultHighlighter.DefaultHighlightPainter(new Color(20, 20, 20));
	private static final String HINT_SHORTCUTS = "Help: 'Alt+H'";
	void setStatusBarText(String message) {
		gui.statusBar.setText(HINT_SHORTCUTS + ' ' + message);
		try {
			gui.statusBar.getHighlighter().addHighlight(0, HINT_SHORTCUTS.length(), STATUS_BAR_READONLY_HIGHLIGHTER);
		} catch (BadLocationException e) {
			throw new RuntimeException(e);
		}
	}

	void makeInputAreaEditable(boolean editable) {
		inputArea().setEditable(editable);
		inputArea().getCaret().setVisible(editable);//to fix bug #4
	}

	void setInputAreaKeyListener(KeyboardListener keyListener) {
		gui.setInputAreaKeyListener(keyListener);
	}

	void setInputAreaCaretListener(CaretListener caretListener) {
		gui.setInputAreaCaretListener(caretListener);
	}

	void removeInputAreaCaretListener() {
		gui.removeInputAreaCaretListener();
	}

	Highlighter inputAreaHighlighter() {
		return inputArea().getHighlighter();
	}

	protected JTextArea inputArea() {
		return gui.inputArea;
	}

	//Buffers
	String buffersAsString() {
		return buffers.asString();
	}
	void setCurrentBufferText() {
		buffers.setCurrentBufferText(text());
	}
	void saveCurrentBuffer() {
		buffers.saveCurrentBuffer(text());
	}

	void loadBufferIntoEditor() {
		Buffer buffer = buffers.selectBufferAsCurrent(Texts.currentRow(this));
		loadEditorView(buffer);
	}
	void addCurrentBuffer(Buffer buffer) {
		buffers.addCurrentBuffer(buffer);
	}
	void closeCurrentBuffer() {
		buffers.closeCurrentBuffer();
		editorPerspective.loadResource(buffers.currentBuffer());
	}

	void undo() {
		BufferState state = buffers.currentBuffer().rollbackToPreviousState();
		setText(state.text);
		setCursorPosition(state.caretPosition);
	}
	void redo() {
		BufferState state = buffers.currentBuffer().forwardToUndoneChange();
		setText(state.text);
		setCursorPosition(state.caretPosition);
	}

	void recordNewBufferState() {
		String text = text();
		if (!buffers.currentBuffer().isStateUpToDate(text)) {
			buffers.currentBuffer().recordNewState(text, selectionStart());
		}
	}
}
