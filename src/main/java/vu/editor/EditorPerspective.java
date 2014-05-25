package vu.editor;

import static java.awt.event.KeyEvent.VK_ALT;
import static java.awt.event.KeyEvent.VK_CONTROL;
import static java.awt.event.KeyEvent.VK_D;
import static java.awt.event.KeyEvent.VK_C;
import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_F;
import static java.awt.event.KeyEvent.VK_J;
import static java.awt.event.KeyEvent.VK_S;
import static java.awt.event.KeyEvent.VK_SHIFT;
import static java.awt.event.KeyEvent.VK_UP;
import static java.awt.event.KeyEvent.VK_W;

public class EditorPerspective extends Perspective {

	private final Driver driver;
	private EditableFile resourceUnderEdit;
	private final KeyboardListener keyListener;

	public EditorPerspective(Driver driver) {
		this.driver = driver;
		this.keyListener = new KeyboardListener(driver) {
			@Override protected void actionOnKeyPressed() {
				if (pushedKeys.contains(VK_CONTROL) && pushedKeys.contains(VK_S)) {
					EditorPerspective.this.save();
				} else if (pushedKeys.contains(VK_CONTROL) && pushedKeys.contains(VK_SHIFT) && pushedKeys.contains(VK_F)) {
					TextActions.formatXml(driver);
				} else if (pushedKeys.contains(VK_CONTROL) && pushedKeys.contains(VK_D)) {
					TextActions.deleteLine(driver);
				} else if (pushedKeys.contains(VK_ALT) && pushedKeys.contains(VK_DOWN)) {
					TextActions.moveLinesDown(driver);
				} else if (pushedKeys.contains(VK_ALT) && pushedKeys.contains(VK_UP)) {
					TextActions.moveLinesUp(driver);
				} else if (pushedKeys.contains(VK_ALT) && pushedKeys.contains(VK_W)) {
					TextActions.showOrHideWhitespacesAndHighlights(driver);
				} else if (pushedKeys.contains(VK_CONTROL) && pushedKeys.contains(VK_J)) {
					TextActions.joinLines(driver);
				} else if (shortcutDetected(VK_ALT, VK_C)) {
					TextActions.toUpperCase(driver);
				} else if (shortcutDetected(VK_ALT, VK_SHIFT, VK_C)) {
					TextActions.toLowerCase(driver);
				}
			}
		};
	}
	
	void loadResource(EditableFile resource) {
		driver.makeInputAreaEditable(true);
		driver.setInputAreaKeyListener(keyListener);
		driver.setText(resource.getText());
		driver.setTitle(resource.getFileName());
		driver.setStatusBarText(resource.getPath());
		resourceUnderEdit = resource;
	}
	void loadPreviousEditableResource() {
		loadResource(resourceUnderEdit);
	}
	private void save() {
		resourceUnderEdit.saveText(driver.text());
	}
	
	@Override void actionOnExitFromPerspective() {
		resourceUnderEdit.setText(driver.text());
	}
}