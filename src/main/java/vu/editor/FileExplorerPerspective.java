package vu.editor;

import static java.awt.event.KeyEvent.VK_CONTROL;
import static java.awt.event.KeyEvent.VK_ENTER;
import static java.awt.event.KeyEvent.VK_ESCAPE;
import static java.awt.event.KeyEvent.VK_H;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_P;
import static java.awt.event.KeyEvent.VK_R;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_W;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

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
				exploredItems.lastCaretPosition = driver.selectionStart();
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
				} else if (shortcutDetected(VK_CONTROL, VK_H)) {
					searchMode();
				} else if (shortcutDetected(VK_ENTER)) {
					loadEditorWithFile();
					stopLastKeyPressedEventPropagation(); //prevents editor from adding new line after resource is loaded
				}
			}
		};
		this.caretListener = new HighlightingCurrentLineCaretListener(driver);
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
		driver.setCursorPosition(exploredItems.lastCaretPosition);
		driver.highlightCurrentLine();
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

	private void searchMode() {
		driver.statusBar().getHighlighter().removeAllHighlights();
		driver.statusBar().setEditable(true);
		driver.statusBar().setText(driver.lastSearchText());
		
		driver.statusBar().setFocusable(true);
		driver.statusBar().requestFocus();
		driver.inputArea().setFocusable(false);
		driver.statusBar().addKeyListener(searchKeyListener);
	}

	private final KeyListener searchKeyListener = new KeyListener() {
		@Override public void keyTyped(KeyEvent e) { }
		@Override public void keyReleased(KeyEvent e) { }
		@Override public void keyPressed(KeyEvent event) {
			if (event.getKeyCode() == KeyEvent.VK_ENTER) {
				search();
			} else if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
				backToFileExplorer();
			}
		}
	};

	private void search() {
		String textToSearch = driver.statusBar().getText();
		if (!"".equals(textToSearch)) {
			driver.search(textToSearch, currentFile());
			resetStatusBar();
			driver.loadSearchView();
		}
	}

	private void backToFileExplorer() {
		resetStatusBar();
		driver.inputAreaHighlighter().removeAllHighlights();
		loadExplorerView();
	}

	private void resetStatusBar() {
		driver.statusBar().removeKeyListener(searchKeyListener);
		driver.statusBar().setEditable(false);
		driver.statusBar().setFocusable(false);

		driver.inputArea().setFocusable(true);
		driver.inputArea().requestFocus();
	}	
}
