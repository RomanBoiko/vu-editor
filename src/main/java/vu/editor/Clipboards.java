package vu.editor;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

class Clipboards {

	private Clipboards() {}
	
	static void put(String copiedText) {
		StringSelection selectionToCopy = new StringSelection(copiedText);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(selectionToCopy, selectionToCopy);
	}
}
