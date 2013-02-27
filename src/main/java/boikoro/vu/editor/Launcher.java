package boikoro.vu.editor;

import java.awt.event.KeyListener;
import java.io.IOException;

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
					
					if(args.length == 1) {
						driver.loadResource(new EditableFile(args[0]));
					}

					gui.show();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}
}