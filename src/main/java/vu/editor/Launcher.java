package vu.editor;

import java.awt.event.KeyListener;

import javax.swing.SwingUtilities;

public class Launcher {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui gui = new Gui();
					Driver driver = new Driver(gui);
					KeyListener keyListener = new KeyboardListener(driver);
					gui.setKeyListener(keyListener);
					gui.show();
					
					if(args.length == 1) {
						driver.loadResource(new EditableFile(args[0]));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}