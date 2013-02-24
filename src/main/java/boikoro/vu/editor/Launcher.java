package boikoro.vu.editor;

import java.io.IOException;

import javax.swing.SwingUtilities;

public class Launcher {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui.startNewGui();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}
}
