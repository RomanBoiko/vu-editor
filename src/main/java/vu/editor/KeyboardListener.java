package vu.editor;

import static java.awt.event.KeyEvent.*;
import static java.lang.String.format;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

public class KeyboardListener implements KeyListener{

	private final Driver driver;
	
	private final Set<Integer> pushedKeys = new HashSet<Integer>();

	public KeyboardListener(Driver driver) {
		this.driver = driver;
	}

	@Override public void keyPressed(KeyEvent pressedKeyEvent) {
		pushedKeys.add(pressedKeyEvent.getKeyCode());
		System.out.println(
				format("=>key pressed, char='%s', code='%d')",
						pressedKeyEvent.getKeyChar(),
						pressedKeyEvent.getKeyCode()));
		System.out.println(
				format("=>active keys: %s", pushedKeys.toString()));
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
		}

	}

	@Override public void keyReleased(KeyEvent releasedKeyEvent) {
		pushedKeys.remove(releasedKeyEvent.getKeyCode());
	}

	@Override public void keyTyped(KeyEvent typedKeyEvent) {
		//if character created as a result
		
	}
}
