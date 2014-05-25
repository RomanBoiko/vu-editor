package vu.editor;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class FileExplorerPerspective extends Perspective {

	private final KeyboardListener keyListener;
	private final CaretListener caretListener;
	private final Driver driver;
	private final ExploredItems exploredItems;

	FileExplorerPerspective(Driver driver) {
		this.driver = driver;
		this.exploredItems = new ExploredItems();
		this.keyListener = new KeyboardListener(driver) {
			@Override protected void actionOnKeyPressed() {
				if (shortcutDetected(KeyEvent.VK_ESCAPE)) {
					driver.loadEditorView();
				} else if (shortcutDetected(KeyEvent.VK_RIGHT)) {
					openItem(driver);
				} else if (shortcutDetected(KeyEvent.VK_LEFT)) {
					closeItem(driver);
				}
			}
		};
		this.caretListener = new CaretListener() {
			@Override public void caretUpdate(CaretEvent caretEvent) {
				highlightCurrentItem();
			}
		};
		
	}

	void loadExplorerView() {
		driver.makeInputAreaEditable(false);
		driver.setInputAreaKeyListener(keyListener);
		driver.setInputAreaCaretListener(caretListener);
		driver.setText(exploredItems.asString());
		driver.setTitle("FileExplorer");
		driver.setStatusBarText("FileExplorer");
		highlightCurrentItem();
	}

	private void highlightCurrentItem() {
		TextActions.highlightCurrentLine(driver);
	}

	private static class ExploredItem {
		final File file;
		final int depth;
		private boolean isOpen = false;
		public int childrenCount = 0;
		ExploredItem(File file, int depth) {
			this.file = file;
			this.depth = depth;
		}
		boolean isOpen() {
			return file.isFile() || isOpen;
		}
		void open() {
			isOpen = true;
			childrenCount = file.listFiles().length;
		}
		void close() {
			isOpen = false;
			childrenCount = 0;
		}
		String asString() {
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < depth; i++) {
				buffer.append("|   ");
			}
			if (isOpen()) {
				buffer.append("- ");
			} else {
				buffer.append("+ ");
			}
			return buffer.append(file.getName()).toString();
		}
	}
	
	private void openItem(Driver driver) {
		int currentRow = TextActions.currentRow(driver);
		if (!exploredItems.item(currentRow).isOpen()) {
			TextActions.replaceContentOfCurrentRow(driver, exploredItems.openItem(currentRow));
		}
	}
	private void closeItem(Driver driver) {
		int currentRow = TextActions.currentRow(driver);
		ExploredItem item = exploredItems.item(currentRow);
		if (item.isOpen() && item.file.isDirectory()) {
			int childrenCount = item.childrenCount;
			System.out.println("-->" + childrenCount);
			TextActions.replaceContentOfCurrentAndNextRows(driver, childrenCount , exploredItems.closeItem(currentRow));
		}
	}
	private static class ExploredItems {
		private final List<ExploredItem> items = new LinkedList<ExploredItem>();
		ExploredItems() {
			String workingDir = System.getProperty("user.dir");
			items.add(new ExploredItem(new File(workingDir), 0));
		}
		String asString() {
			StringBuffer buffer = new StringBuffer();
			for (ExploredItem item : items) {
				buffer.append(item.asString()).append(TextActions.LINE_SEPARATOR);
			}
			return buffer.toString().trim();
		}
		ExploredItem item(int rowNumber) {
			return items.get(rowNumber - 1);
		}
		public String closeItem(int rowNumber) {
			ExploredItem itemToClose = item(rowNumber);
			for (int i = 0; i < itemToClose.childrenCount; i++) {
				items.remove(rowNumber);
			}
			itemToClose.close();
			return itemToClose.asString();
		}
		public String openItem(int rowNumber) {
			ExploredItem itemToOpen = item(rowNumber);
			int positionToInsertChid = rowNumber;
			itemToOpen.open();
			StringBuffer buffer = new StringBuffer().append(itemToOpen.asString());
			File[] children = itemToOpen.file.listFiles();
			List<File> allChildren = new LinkedList<File>();
			List<File> files = new LinkedList<File>();
			for (File child : children) {
				if (child.isDirectory()) {
					allChildren.add(child);
				} else {
					files.add(child);
				}
			}
			allChildren.addAll(files);
			for (File child : allChildren) {
				ExploredItem item = new ExploredItem(child, itemToOpen.depth + 1);
				buffer.append(TextActions.LINE_SEPARATOR).append(item.asString());
				items.add(positionToInsertChid++, item);
			}
			return buffer.toString();
		}
	}
}
