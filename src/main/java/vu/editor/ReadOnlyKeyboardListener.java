package vu.editor;

import java.awt.event.KeyEvent;

public class ReadOnlyKeyboardListener extends KeyboardListener {
	
	ReadOnlyKeyboardListener(Driver driver) {
		super(driver);
	}

	@Override
	protected void actionOnKeyPressed() {
		if (pushedKeys.contains(KeyEvent.VK_ESCAPE)) {
			driver.loadPreviousEditableResource();
		}
	}
}
