package vu.editor;

import java.io.IOException;

public class Driver {
	private final Gui gui;
	private final KeyboardListener keyboardListener;
	private EditableFile resourceUnderEdit;

	public Driver() {
		this.keyboardListener = new KeyboardListener(this);
		this.gui = new Gui(keyboardListener);
		this.gui.show();
	}

	public void save() {
		resourceUnderEdit.saveText(gui.getCurrentText());
	}
	public void formatXml() {
		gui.setCurrentText(Formatters.formatXml(gui.getCurrentText()));
	}

	public void loadResource(EditableFile resource) throws IOException {
		gui.setCurrentText(resource.getText());
		gui.setTitle(resource.getFileName());
		gui.setStatusBarText(resource.getPath());
		resourceUnderEdit = resource;
	}
}


