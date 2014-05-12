package vu.editor;

import java.io.IOException;

public class Driver {
	private final Gui gui;

	public Driver(Gui gui) {
		this.gui = gui;
		this.gui.setCurrentText(createTextForEditor());
	}

	public void save() {
		try {
			resourceUnderEdit.saveText(gui.getCurrentText());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private String createTextForEditor() {
		String text = "";
		for (int i = 0; i < 100; i++) {
			text += "asd" + i + "\n";
		}
		return text;
	}
	
	private EditableFile resourceUnderEdit;
	
	public void loadResource(EditableFile resource) throws IOException {
		gui.setCurrentText(resource.getText());
		gui.setTitle(resource.getFileName());
		gui.setStatusBarText(resource.getPath());
		resourceUnderEdit = resource;
	}
}