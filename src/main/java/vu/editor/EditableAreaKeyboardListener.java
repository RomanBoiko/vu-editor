package vu.editor;

import static java.awt.event.KeyEvent.VK_ALT;
import static java.awt.event.KeyEvent.VK_CONTROL;
import static java.awt.event.KeyEvent.VK_D;
import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_F;
import static java.awt.event.KeyEvent.VK_H;
import static java.awt.event.KeyEvent.VK_J;
import static java.awt.event.KeyEvent.VK_S;
import static java.awt.event.KeyEvent.VK_SHIFT;
import static java.awt.event.KeyEvent.VK_UP;
import static java.awt.event.KeyEvent.VK_W;

public class EditableAreaKeyboardListener extends KeyboardListener {

	EditableAreaKeyboardListener(Driver driver) {
		super(driver);
	}
	
	@Override protected void actionOnKeyPressed() {
		if(pushedKeys.contains(VK_CONTROL) && pushedKeys.contains(VK_S)) {
			driver.save();
		} else if(pushedKeys.contains(VK_CONTROL) && pushedKeys.contains(VK_SHIFT) && pushedKeys.contains(VK_F)) {
			TextActions.formatXml(driver);
		} else if(pushedKeys.contains(VK_CONTROL) && pushedKeys.contains(VK_D)) {
			TextActions.deleteLine(driver);
		} else if(pushedKeys.contains(VK_ALT) && pushedKeys.contains(VK_DOWN)) {
			TextActions.moveLinesDown(driver);
		} else if(pushedKeys.contains(VK_ALT) && pushedKeys.contains(VK_UP)) {
			TextActions.moveLinesUp(driver);
		} else if(pushedKeys.contains(VK_ALT) && pushedKeys.contains(VK_W)) {
			TextActions.showOrHideWhitespacesAndHighlights(driver);
		} else if(pushedKeys.contains(VK_CONTROL) && pushedKeys.contains(VK_J)) {
			TextActions.joinLines(driver);
		} else if(pushedKeys.contains(VK_ALT) && pushedKeys.contains(VK_H)) {
			driver.loadHelpView();
		}
	}
}
