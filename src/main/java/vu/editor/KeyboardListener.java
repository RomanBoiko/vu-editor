package vu.editor;

import static java.lang.String.format;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

public abstract class KeyboardListener implements KeyListener {
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
		pushedKeys.add(pressedKeyEvent.getKeyCode());
		System.out.println(
				format("=>key pressed, char='%s', code='%d')",
						pressedKeyEvent.getKeyChar(),
						pressedKeyEvent.getKeyCode()));
		System.out.println(
				format("=>active keys: %s", pushedKeys.toString()));
		actionOnKeyPressed();
	}

	protected abstract void actionOnKeyPressed();

	@Override public void keyReleased(KeyEvent releasedKeyEvent) {
		pushedKeys.remove(releasedKeyEvent.getKeyCode());
	}

	@Override public void keyTyped(KeyEvent typedKeyEvent) {
		//if character created as a result
	}
}
