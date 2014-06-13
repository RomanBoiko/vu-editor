package vu.editor;

import static java.awt.event.KeyEvent.VK_CONTROL;
import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_ESCAPE;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_P;
import static java.awt.event.KeyEvent.VK_R;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_W;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class FileExplorerPerspective extends Perspective {

	private final KeyboardListener keyListener;
	private final CaretListener caretListener;
	private final Driver driver;
	private final ExploredItems rootExploredItems = new ExploredItems(File.listRoots());
	private ExploredItems workDirExploredItems = new ExploredItems(new File(System.getProperty("user.dir")));
	private ExploredItems exploredItems;

	FileExplorerPerspective(Driver driver) {
		this.driver = driver;
		this.exploredItems = workDirExploredItems;
		this.keyListener = new KeyboardListener(driver) {
			@Override protected void actionOnKeyPressed() {
				exploredItems.lastSelectedRow = driver.selectionStart();
				if (shortcutDetected(VK_ESCAPE)) {
					driver.loadEditorView();
				} else if (shortcutDetected(VK_RIGHT)) {
					openItem(driver);
				} else if (shortcutDetected(VK_LEFT)) {
					closeItem(driver);
				} else if (shortcutDetected(VK_R)) {
					resetExplorerTo(rootExploredItems);
				} else if (shortcutDetected(VK_W)) {
					resetExplorerTo(workDirExploredItems);
				} else if (shortcutDetected(VK_CONTROL, VK_W)) {
					setCurrentDirAsWorkdirAndSwitchToIt();
				} else if (shortcutDetected(VK_CONTROL, VK_P)) {
					copyCurrentFilePathToClipboard();
				} else if (shortcutDetected(VK_ENTER)) {
					loadEditorWithFile();
					stopLastKeyPressedEventPropagation(); //prevents editor from adding new line after resource is loaded
				}
			}
		};
		this.caretListener = new CaretListener() {
			@Override public void caretUpdate(CaretEvent event) {
				highlightCurrentItem();
			}
		};
	}

	private void setCurrentDirAsWorkdirAndSwitchToIt() {
		File currentDir = currentFile();
		if (currentDir.isDirectory()) {
			workDirExploredItems = new ExploredItems(currentDir);
			resetExplorerTo(workDirExploredItems);
		}
	}
	private void copyCurrentFilePathToClipboard() {
		driver.copyCurrentFilePathToClipboard(currentFile().getAbsolutePath());
	}

	private void resetExplorerTo(ExploredItems exploredItems) {
		this.exploredItems = exploredItems;
		loadExplorerView();
	}

	void loadExplorerView() {
		driver.makeInputAreaEditable(false);
		driver.setInputAreaKeyListener(keyListener);
		driver.setInputAreaCaretListener(caretListener);
		driver.setText(exploredItems.asString());
		driver.setCursorPosition(Texts.secondPositionInCurrentRow(driver));
		driver.setTitle("FileExplorer");
		driver.setStatusBarText("FileExplorer: 'R' - root, 'W' - working dir (" + workDirExploredItems.pathToRoot() + "), 'Ctrl+P' - selected file path to clipboard, 'Ctrl+W' - set selected dir as workdir");
		driver.setCursorPosition(exploredItems.lastSelectedRow);
		highlightCurrentItem();
	}

	private void highlightCurrentItem() {
		Texts.highlightCurrentLine(driver);
	}

	private void loadEditorWithFile() {
		File currentFile = currentFile();
		if (currentFile.isFile()) {
			driver.loadEditorView(new Buffer(currentFile));
		}
	}

	private File currentFile() {
		return exploredItems.item(Texts.currentRow(driver)).file;
	}

	private void openItem(Driver driver) {
		int positionBeforeOpen = driver.selectionStart();
		int currentRow = Texts.currentRow(driver);
		if (exploredItems.item(currentRow).canBeOpened()) {
			Texts.replaceContentOfCurrentRow(driver, exploredItems.openItem(currentRow));
		}
		driver.setCursorPosition(positionBeforeOpen);
	}
	private void closeItem(Driver driver) {
		int currentRow = Texts.currentRow(driver);
		ExploredItem item = exploredItems.item(currentRow);
		if (item.canBeClosed()) {
			ItemCloseResult closeResult = exploredItems.closeItem(currentRow);
			Texts.replaceContentOfCurrentAndNextRows(driver, closeResult.closedChildrenCount, closeResult.newDirContentString);
		}
		driver.setCursorPosition(Texts.secondPositionInCurrentRow(driver));
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
			return buffer.append(nonEmptyFilePath()).toString();
		}
		private String nonEmptyFilePath() {
			return file.getName().equals("") ? file.getAbsolutePath() : file.getName();
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
		int lastSelectedRow = 0;
		ExploredItems(File...roots) {
			for (File root : roots) {
				items.add(new ExploredItem(root, 0));
			}
		}

		String pathToRoot() {
			return item(1).file.getAbsolutePath();
		}

		String asString() {
			StringBuffer buffer = new StringBuffer();
			for (ExploredItem item : items) {
				buffer.append(item.asString()).append(Texts.LINE_SEPARATOR);
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
				buffer.append(Texts.LINE_SEPARATOR).append(item.asString());
				items.add(positionToInsertChid++, item);
			}
			return buffer.toString();
		}
	}
}
