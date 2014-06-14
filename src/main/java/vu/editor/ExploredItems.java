package vu.editor;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

class ExploredItems {
	private final List<ExploredItem> items = new LinkedList<ExploredItem>();
	int lastCaretPosition = 0;
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
class ExploredItem {
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
class ItemCloseResult {
	final String newDirContentString;
	final int closedChildrenCount;
	public ItemCloseResult(String newDirContentString, int closedChildrenCount) {
		this.newDirContentString = newDirContentString;
		this.closedChildrenCount = closedChildrenCount;
	}
}