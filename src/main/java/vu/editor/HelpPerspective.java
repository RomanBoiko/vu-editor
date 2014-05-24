package vu.editor;

import java.awt.event.KeyEvent;

public class HelpPerspective {

	private final KeyboardListener keyListener;
	private final Driver driver;
	private String helpText;

	HelpPerspective(Driver driver) {
		this.driver = driver;
		this.keyListener = new KeyboardListener(driver) {
			@Override
			protected void actionOnKeyPressed() {
				if (pushedKeys.contains(KeyEvent.VK_ESCAPE)) {
					driver.loadEditorView();
				}
			}
		};
	}

	void loadHelpView() {
		driver.inputArea().setEditable(false);
		driver.gui().replaceInputAreaKeyboardListenerWith(keyListener);
		helpText = helpText == null ? Streams.streamToString(this.getClass().getClassLoader().getResourceAsStream("help.txt")) : helpText;
		driver.text(helpText);
		driver.gui().mainFrame.setTitle("Help");
		driver.gui().statusBar.setText("Help");
	}
}
