package boikoro.vu.editor;

import static java.lang.String.format;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class KeyboardListener implements KeyListener{

	private final Gui gui;
	
	Set<Integer> pushedKeys = new HashSet<Integer>();

	public KeyboardListener(Gui gui) {
		this.gui = gui;
	}
	
	@Override
	public void keyPressed(KeyEvent pressedKeyEvent) {
		pushedKeys.add(pressedKeyEvent.getKeyCode());
		System.out.println(
				format("=>key pressed, char='%s', code='%d')",
						pressedKeyEvent.getKeyChar(),
						pressedKeyEvent.getKeyCode()));
		System.out.println(
				format("=>active keys: %s", pushedKeys.toString()));
		if(pushedKeys.contains(17) && pushedKeys.contains(83)) {
			try {
				gui.saveCurrentText();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent releasedKeyEvent) {
		pushedKeys.remove(releasedKeyEvent.getKeyCode());
	}

	@Override
	public void keyTyped(KeyEvent typedKeyEvent) {
		// TODO Auto-generated method stub
		
	}
}
