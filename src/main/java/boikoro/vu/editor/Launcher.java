package boikoro.vu.editor;

import javax.swing.SwingUtilities;

public class Launcher {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Gui ex = new Gui();
				ex.setVisible(true);
			}
		});
	}
}
