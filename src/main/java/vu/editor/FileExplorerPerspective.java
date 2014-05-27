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
			@Override public void caretUpdate(CaretEvent event) {
				highlightCurrentItem();
			}
		};
		
	}

	void loadExplorerView() {
		driver.makeInputAreaEditable(false);
		driver.setInputAreaKeyListener(keyListener);
		driver.setInputAreaCaretListener(caretListener);
		driver.setText(exploredItems.asString());
		driver.setCursorPosition(TextActions.secondPositionInCurrentRow(driver));
		driver.setTitle("FileExplorer");
		driver.setStatusBarText("FileExplorer: " + workingDir().getAbsolutePath());
		highlightCurrentItem();
	}

	private void highlightCurrentItem() {
		TextActions.highlightCurrentLine(driver);
	}

	
	private void openItem(Driver driver) {
		int positionBeforeOpen = driver.selectionStart();
		int currentRow = TextActions.currentRow(driver);
		if (exploredItems.item(currentRow).canBeOpened()) {
			TextActions.replaceContentOfCurrentRow(driver, exploredItems.openItem(currentRow));
		}
		driver.setCursorPosition(positionBeforeOpen);
	}
	private void closeItem(Driver driver) {
		int currentRow = TextActions.currentRow(driver);
		ExploredItem item = exploredItems.item(currentRow);
		if (item.canBeClosed()) {
			ItemCloseResult closeResult = exploredItems.closeItem(currentRow);
			TextActions.replaceContentOfCurrentAndNextRows(driver, closeResult.closedChildrenCount, closeResult.newDirContentString);
		}
		driver.setCursorPosition(TextActions.secondPositionInCurrentRow(driver));
	}

	private File workingDir() {
		String workingDirPath = System.getProperty("user.dir");
		return new File(workingDirPath);
	}


	private class ExploredItem {
		final File file;
		final int depth;
		private boolean isOpen = false;
		ExploredItem(File file, int depth) {
			this.file = file;
			this.depth = depth;
		}
		void open() {
			isOpen = true;
		}
		void close() {
			isOpen = false;
		}
		String asString() {
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < depth; i++) {
				buffer.append("|   ");
			}
			if (file.isFile()) {
				buffer.append("= ");
			} else if (isOpen) {
				buffer.append("- ");
			} else {
				buffer.append("+ ");
			}
			return buffer.append(file.getName()).toString();
		}
		boolean canBeClosed() {
			return file.isDirectory() && isOpen;
		}
		boolean canBeOpened() {
			return file.isDirectory() && !isOpen;
		}
	}
	private class ItemCloseResult {
		final String newDirContentString;
		final int closedChildrenCount;
		public ItemCloseResult(String newDirContentString, int closedChildrenCount) {
			this.newDirContentString = newDirContentString;
			this.closedChildrenCount = closedChildrenCount;
		}
	}
	private class ExploredItems {
		private final List<ExploredItem> items = new LinkedList<ExploredItem>();
		ExploredItems() {
			items.add(new ExploredItem(workingDir(), 0));
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

		ItemCloseResult closeItem(int rowNumber) {
			ExploredItem itemToClose = item(rowNumber);
			int childrenCount = 0;
			while(items.size() > rowNumber) {
				ExploredItem itemAfterOneToClose = items.get(rowNumber);
				if (itemAfterOneToClose.depth > itemToClose.depth) {
					childrenCount++;
					items.remove(rowNumber);
				} else {
					break;
				}
			}
			itemToClose.close();
			return new ItemCloseResult(itemToClose.asString(), childrenCount);
		}
		String openItem(int rowNumber) {
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
