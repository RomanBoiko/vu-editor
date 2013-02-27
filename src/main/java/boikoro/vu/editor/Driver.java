package boikoro.vu.editor;

import java.io.IOException;

public class Driver {
	private final Gui gui;

	public Driver(Gui gui) {
		this.gui = gui;
	}

	public void save() {
		try {
			gui.saveCurrentText();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
