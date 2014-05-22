package vu.editor;

import java.io.InputStream;
import java.util.Scanner;

import javax.swing.JTextArea;

public class Driver {
	private EditableFile resourceUnderEdit;
	private Gui gui;

	private final KeyboardListener editableAreaKeyListener;
	private final KeyboardListener readOnlyAreaKeyListener;
	
	public Driver() {
		this.editableAreaKeyListener = new EditableAreaKeyboardListener(this);
		this.readOnlyAreaKeyListener = new ReadOnlyKeyboardListener(this);
		this.gui = new Gui();
		editableState();
	}

	void showGui() {
		this.gui.show();
	}

	void save() {
		resourceUnderEdit.saveText(text());
	}

	void loadResource(EditableFile resource) {
		editableState();
		text(resource.getText());
		gui.mainFrame.setTitle(resource.getFileName());
		gui.statusBar.setText(resource.getPath());
		resourceUnderEdit = resource;
	}
	void loadPreviousEditableResource() {
		loadResource(resourceUnderEdit);
	}

	void loadHelpView() {
		readOnlyState();
		text(streamToString(this.getClass().getClassLoader().getResourceAsStream("help.txt")));
		gui.mainFrame.setTitle("Help");
		gui.statusBar.setText("Help");
	}
	
	static String streamToString(InputStream stream) {
		Scanner scanner = new Scanner(stream).useDelimiter("\\A");
		return scanner.hasNext() ? scanner.next() : "";
	}
	
	private void editableState() {
		inputArea().setEditable(true);
		gui.replaceInputAreaKeyboardListenerWith(editableAreaKeyListener);
	}
	private void readOnlyState() {
		inputArea().setEditable(false);
		gui.replaceInputAreaKeyboardListenerWith(readOnlyAreaKeyListener);
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
