package vu.editor;

import java.io.IOException;

public class Driver {
	private final Gui gui;
	private EditableFile resourceUnderEdit;

	public Driver(Gui gui) {
		this.gui = gui;
	}

	public void save() {
		try {
			resourceUnderEdit.saveText(gui.getCurrentText());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void loadResource(EditableFile resource) throws IOException {
		gui.setCurrentText(resource.getText());
		gui.setTitle(resource.getFileName());
		gui.setStatusBarText(resource.getPath());
		resourceUnderEdit = resource;
	}
}
