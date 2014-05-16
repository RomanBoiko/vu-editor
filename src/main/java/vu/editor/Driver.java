package vu.editor;

import java.io.IOException;

public class Driver {
	private final KeyboardListener keyboardListener;
	private EditableFile resourceUnderEdit;
	private Gui gui;

	public Driver() {
		this.keyboardListener = new KeyboardListener(this);
		this.gui = new Gui(keyboardListener);
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
		gui.inputArea.setText(text);
	}

	String text() {
		return gui.inputArea.getText();
	}

	int cursor() {
		return gui.inputArea.getSelectionStart();
	}

	public void replaceRange(String replacement, int start, int end) {
		gui.inputArea.replaceRange(replacement, start, end);
	}
}


