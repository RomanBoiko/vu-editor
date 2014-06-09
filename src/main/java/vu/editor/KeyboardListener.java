package vu.editor;

import static java.awt.event.KeyEvent.VK_ALT;
import static java.awt.event.KeyEvent.VK_H;
import static java.awt.event.KeyEvent.VK_E;
import static java.awt.event.KeyEvent.VK_Q;
import static java.lang.String.format;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class KeyboardListener implements KeyListener {
	private KeyEvent lastKeyPressedEvent = null;
	protected final Driver driver;
	
	protected final Set<Integer> pushedKeys = new HashSet<Integer>();

	public KeyboardListener(Driver driver) {
		this.driver = driver;
	}
	
	void reset() {
		pushedKeys.clear();
		assert pushedKeys.size() == 0;
	}

	@Override public void keyPressed(KeyEvent pressedKeyEvent) {
		lastKeyPressedEvent = pressedKeyEvent;
		pushedKeys.add(pressedKeyEvent.getKeyCode());
		Editor.log(format("=>key pressed: char='%s', code='%d')", pressedKeyEvent.getKeyChar(), pressedKeyEvent.getKeyCode()));
		Editor.log(format("=>active keys: %s", pushedKeys.toString()));
		if (shortcutDetected(VK_ALT, VK_Q)) {
			driver.exit();
		} else if (shortcutDetected(VK_ALT, VK_H)) {
			driver.loadHelpView();
		} else if (shortcutDetected(VK_ALT, VK_E)) {
			driver.loadFileExplorerView();
		} else {
			actionOnKeyPressed();
		}
	}

	protected abstract void actionOnKeyPressed();

	protected final void stopLastKeyPressedEventPropagation() {
		lastKeyPressedEvent.consume();
	}

	@Override public void keyReleased(KeyEvent releasedKeyEvent) {
		pushedKeys.remove(releasedKeyEvent.getKeyCode());
	}

	@Override public void keyTyped(KeyEvent typedKeyEvent) {
		//if character created as a result
	}
	
	protected boolean shortcutDetected(Integer... keys) {
		return keys.length == pushedKeys.size() && pushedKeys.containsAll(Arrays.asList(keys));
	}
}
