package vu.editor;

import javax.swing.SwingUtilities;

public class Launcher {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					Driver driver = new Driver();
					
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