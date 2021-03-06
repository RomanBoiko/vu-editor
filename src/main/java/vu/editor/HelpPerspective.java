package vu.editor;

import java.awt.event.KeyEvent;

public class HelpPerspective extends Perspective {

	private final KeyboardListener keyListener;
	private final Driver driver;
	private String helpText;

	HelpPerspective(Driver driver) {
		this.driver = driver;
		this.keyListener = new KeyboardListener(driver) {
			@Override
			protected void actionOnKeyPressed() {
				if (shortcutDetected(KeyEvent.VK_ESCAPE)) {
					driver.loadEditorView();
				}
			}
		};
	}

	void loadHelpView() {
		driver.makeInputAreaEditable(false);
		driver.setInputAreaKeyListener(keyListener);
		driver.removeInputAreaCaretListener();
		helpText = helpText == null ? Streams.streamToString(this.getClass().getClassLoader().getResourceAsStream("help.txt")) : helpText;
		driver.setText(helpText);
		driver.setTitle("Help");
		driver.setStatusBarText("Press 'Esc' to hide");
	}
}
