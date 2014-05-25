package vu.editor;

import java.awt.event.KeyEvent;
import java.io.File;

public class FileExplorerPerspective extends Perspective {

	private final KeyboardListener keyListener;
	private final Driver driver;

	FileExplorerPerspective(Driver driver) {
		this.driver = driver;
		this.keyListener = new KeyboardListener(driver) {
			@Override
			protected void actionOnKeyPressed() {
				if (shortcutDetected(KeyEvent.VK_ESCAPE)) {
					driver.loadEditorView();
				}
			}
		};
	}

	void loadExplorerView() {
		driver.makeInputAreaEditable(false);
		driver.setInputAreaKeyListener(keyListener);
		driver.setText(directoryTree());
		driver.setTitle("FileExplorer");
		driver.setStatusBarText("FileExplorer");
	}

	private String directoryTree() {
		StringBuffer buffer = new StringBuffer();
		String workingDir = System.getProperty("user.dir");
		directoryTree(new File(workingDir), 0, buffer);
		return buffer.toString();
	}
	private void directoryTree(File dir, int depthLevel, StringBuffer buffer) {
		for (int i = 0; i < depthLevel; i++) {
			buffer.append('\t');
		}
		buffer.append(dir.getName()).append('\n');
		if (dir.isDirectory()) {
			for (File child : dir.listFiles()) {
				directoryTree(child, depthLevel + 1, buffer);
			}
		}
	}
}
