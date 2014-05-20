package vu.editor;

import java.io.IOException;

import javax.swing.JTextArea;

public class Driver {
	private final KeyboardListener keyboardListener;
	private EditableFile resourceUnderEdit;
	private Gui gui;

	public Driver() {
		this.keyboardListener = new KeyboardListener(this);
		this.gui = new Gui(keyboardListener);
	}

	void showGui() {
		this.gui.show();
	}
	

	void save() {
		resourceUnderEdit.saveText(text());
	}

	void loadResource(EditableFile resource) throws IOException {
		text(resource.getText());
		gui.mainFrame.setTitle(resource.getFileName());
		gui.statusBar.setText(resource.getPath());
		resourceUnderEdit = resource;
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

}


