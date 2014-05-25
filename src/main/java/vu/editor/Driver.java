package vu.editor;

import javax.swing.JTextArea;
import javax.swing.event.CaretListener;
import javax.swing.text.Highlighter;

public class Driver {
	private final Gui gui = new Gui();
	private final HelpPerspective helpPerspective = new HelpPerspective(this);
	private final EditorPerspective editorPerspective = new EditorPerspective(this);
	private final FileExplorerPerspective fileExplorerPerspective = new FileExplorerPerspective(this);
	private Perspective currentPerspective = new Perspective() { };

	void showGui() {
		this.gui.show();
	}

	void loadEditorView(EditableFile resource) {
		setCurrentPerspective(editorPerspective);
		editorPerspective.loadResource(resource);
	}
	void loadEditorView() {
		setCurrentPerspective(editorPerspective);
		editorPerspective.loadPreviousEditableResource();
	}
	void loadHelpView() {
		setCurrentPerspective(helpPerspective);
		this.helpPerspective.loadHelpView();
	}
	void loadFileExplorerView() {
		setCurrentPerspective(fileExplorerPerspective);
		this.fileExplorerPerspective.loadExplorerView();
	}
	private void setCurrentPerspective(Perspective newPerspective) {
		currentPerspective.actionOnExitFromPerspective();
		currentPerspective = newPerspective;
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
	
	void setTitle(String title) {
		gui.mainFrame.setTitle(title);
	}
	
	void setStatusBarText(String message) {
		gui.statusBar.setText(message);
	}

	void makeInputAreaEditable(boolean editable) {
		inputArea().setEditable(editable);
		inputArea().getCaret().setVisible(editable);//to fix bug #4
	}

	void setInputAreaKeyListener(KeyboardListener keyListener) {
		gui.setInputAreaKeyListener(keyListener);
	}

	Highlighter inputAreaHighlighter() {
		return inputArea().getHighlighter();
	}
	
	void setInputAreaCaretListener(CaretListener caretListener) {
		gui.setInputAreaCaretListener(caretListener);
	}

	void removeInputAreaCaretListener() {
		gui.removeInputAreaCaretListener();
	}

	protected JTextArea inputArea() {
		return gui.inputArea;
	}
}
