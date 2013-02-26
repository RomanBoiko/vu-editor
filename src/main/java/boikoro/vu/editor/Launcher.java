package boikoro.vu.editor;

import java.io.IOException;

import javax.swing.SwingUtilities;

public class Launcher {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					if(args.length == 1) {
						Gui.startNewGui(new EditableFile(args[0]));
					} else {
						Gui.startNewGui();
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}
}