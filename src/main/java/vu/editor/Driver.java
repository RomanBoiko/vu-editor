package vu.editor;

import javax.swing.JTextArea;

public class Driver {
	private Gui gui;
	private final HelpPerspective helpPerspective;
	private final EditorPerspective editorPerspective;

	public Driver() {
		this.helpPerspective = new HelpPerspective(this);
		this.editorPerspective = new EditorPerspective(this);
		this.gui = new Gui();
	}

	void showGui() {
		this.gui.show();
	}

	void loadEditorView(EditableFile resource) {
		editorPerspective.loadResource(resource);
	}
	void loadEditorView() {
		editorPerspective.loadPreviousEditableResource();
	}
	void loadHelpView() {
		this.helpPerspective.loadHelpView();
	}

	void text(String text) {
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

	protected JTextArea inputArea() {
		return gui.inputArea;
	}

	Gui gui() {
		return gui;
	}

}
