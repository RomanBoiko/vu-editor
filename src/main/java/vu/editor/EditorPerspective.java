package vu.editor;

import static java.awt.event.KeyEvent.VK_ALT;
import static java.awt.event.KeyEvent.VK_C;
import static java.awt.event.KeyEvent.VK_CONTROL;
import static java.awt.event.KeyEvent.VK_D;
import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_F;
import static java.awt.event.KeyEvent.VK_J;
import static java.awt.event.KeyEvent.VK_P;
import static java.awt.event.KeyEvent.VK_S;
import static java.awt.event.KeyEvent.VK_SHIFT;
import static java.awt.event.KeyEvent.VK_TAB;
import static java.awt.event.KeyEvent.VK_UP;
import static java.awt.event.KeyEvent.VK_W;
import static java.awt.event.KeyEvent.VK_Z;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class EditorPerspective extends Perspective {

	private final Driver driver;
	private final KeyboardListener keyListener;
	private final CaretListener caretListener;

	private AtomicBoolean toRecordStateChange = new AtomicBoolean(true);
	public EditorPerspective(Driver driver) {
		this.driver = driver;
		this.keyListener = new KeyboardListener(driver) {
			@Override protected void actionOnKeyPressed() {
				if (shortcutDetected(VK_CONTROL, VK_S)) {
					driver.saveCurrentBuffer();
				} else if (shortcutDetected(VK_CONTROL, VK_SHIFT, VK_S)) {
					driver.saveAllOpenBuffers();
				} else if (shortcutDetected(VK_CONTROL, VK_SHIFT, VK_F)) {
					Texts.formatXml(driver);
				} else if (shortcutDetected(VK_CONTROL, VK_D)) {
					Texts.deleteLine(driver);
				} else if (shortcutDetected(VK_ALT, VK_DOWN)) {
					Texts.moveLinesDown(driver);
				} else if (shortcutDetected(VK_ALT, VK_UP)) {
					Texts.moveLinesUp(driver);
				} else if (shortcutDetected(VK_CONTROL, VK_ALT, VK_DOWN)) {
					Texts.duplicateLines(driver);
				} else if (shortcutDetected(VK_ALT, VK_W)) {
					Texts.showOrHideWhitespacesAndHighlights(driver);
				} else if (shortcutDetected(VK_CONTROL, VK_J)) {
					Texts.joinLines(driver);
				} else if (shortcutDetected(VK_ALT, VK_C)) {
					Texts.toUpperCase(driver);
				} else if (shortcutDetected(VK_ALT, VK_SHIFT, VK_C)) {
					Texts.toLowerCase(driver);
				} else if (shortcutDetected(VK_CONTROL, VK_TAB)) {
					driver.loadBuffersView();
				} else if (shortcutDetected(VK_CONTROL, VK_W)) {
					driver.closeCurrentBuffer();
				} else if (shortcutDetected(VK_CONTROL, VK_P)) {
					driver.copyCurrentFilePathToClipboard();
				} else if (shortcutDetected(VK_CONTROL, VK_F)) {
					findMode();
				} else if (shortcutDetected(VK_CONTROL, VK_Z)) {
					undo();
				} else if (shortcutDetected(VK_CONTROL, VK_SHIFT, VK_Z)) {
					redo();
				} else if (shortcutDetected(VK_TAB)) {
					if (Texts.selectionContainsMultipleRows(driver)) {
						Texts.indent(driver);
						stopLastKeyPressedEventPropagation(); //prevents editor from adding new tab at cursor place
					}
				} else if (shortcutDetected(VK_SHIFT, VK_TAB)) {
					Texts.unindent(driver);
					stopLastKeyPressedEventPropagation(); //prevents editor from adding new tab at cursor place
				}
			}

		};
		this.caretListener = new CaretListener() {
			@Override public void caretUpdate(CaretEvent event) {
				highlightMatchingBrackets();
				recordNewBufferState();
			}
		};
	}

	private void undo() {
		toRecordStateChange.set(false);
		driver.undo();
	}
	private void redo() {
		toRecordStateChange.set(false);
		driver.redo();
	}
	private void recordNewBufferState() {
		if (toRecordStateChange.getAndSet(true)) {
			driver.recordNewBufferState();
		}
	}
	private void highlightMatchingBrackets() {
		Texts.highlightMatchingBrackets(driver);
	}

	void loadResource(Buffer resource) {
		driver.makeInputAreaEditable(true);
		driver.setText(resource.getText());
		driver.setTitle(resource.getFileName());
		driver.setStatusBarText(resource.getPath());
		driver.setCursorPosition(0);
		driver.inputAreaHighlighter().removeAllHighlights();
		driver.setInputAreaKeyListener(keyListener);
		driver.setInputAreaCaretListener(caretListener);
	}

	@Override void actionOnExitFromPerspective() {
		driver.setCurrentBufferText();
	}

	private final KeyListener findKeyListener = new KeyListener() {
		@Override public void keyTyped(KeyEvent e) { }
		@Override public void keyReleased(KeyEvent e) { }
		@Override public void keyPressed(KeyEvent event) {
			if (event.getKeyCode() == KeyEvent.VK_ENTER) {
				find();
			} else if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
				backToEditMode();
			}
		}
	};

	private class Finder {
		private String previousFindText = "";
		private int previousFindPosition = -1;
		public void find(String textToFind) {
			if (textToFind.length() == 0) {
				return;
			}
			if (!previousFindText.equals(textToFind)) {
				previousFindText = textToFind;
				previousFindPosition = -1;
			}
			Texts.removeAllFindHighlights(driver);
			int newPosition = findAfter(textToFind, previousFindPosition + 1);
			if (newPosition >= 0 ) {
				previousFindPosition = newPosition;
				highlight(textToFind, newPosition);
			} else if (previousFindPosition >= 0) {//search wrap
				int firstOccurence = findAfter(textToFind, 0);
				previousFindPosition = firstOccurence;
				highlight(textToFind, firstOccurence);
			}
		}

		private void highlight(String textToFind, int firstOccurence) {
			if (textToFind.startsWith("r=")) {
				Texts.highlightFoundText(driver, firstOccurence, 1);
			} else {
				Texts.highlightFoundText(driver, firstOccurence, findQueryWithoutPrefix(textToFind).length());
			}
		}
		
		private int findAfter(String textToFind, int startPosition) {
			if (textToFind.startsWith("s=")) {
				return driver.text().indexOf(findQueryWithoutPrefix(textToFind), startPosition);
			} else if(textToFind.startsWith("r=")) {
				String[] partsSplitByRegexp = driver.text().substring(startPosition).split(findQueryWithoutPrefix(textToFind));
				return (partsSplitByRegexp.length > 1
						? (startPosition + partsSplitByRegexp[0].length())
						: -1);
			} else {
				return driver.text().toLowerCase().indexOf(textToFind.toLowerCase(), startPosition);
			}
		}

		private String findQueryWithoutPrefix(String textToFind) {
			int prefixLength = 2;
			return (textToFind.startsWith("s=") || textToFind.startsWith("r=")) ? textToFind.substring(prefixLength) : textToFind; 
		}
		int previousFindTextWithoutPrefixLength() {
			if (previousFindText().startsWith("r=")) {
				return 1;
			} else {
				return findQueryWithoutPrefix(previousFindText).length();
			}
		}
		String previousFindText() {
			return previousFindText;
		}
	}
	private static final String FIND_MESSAGE = "FIND ('s=' case-sensitive, 'r=' regexp) =>";
	private Finder finder = new Finder();

	private void find() {
		String textToFind = driver.statusBar().getText();
		if (textToFind.startsWith(FIND_MESSAGE)) {
			textToFind = textToFind.substring(FIND_MESSAGE.length());
		}
		finder.find(textToFind);
	}
	private void backToEditMode() {
		driver.statusBar().removeKeyListener(findKeyListener);
		driver.statusBar().setEditable(false);
		driver.statusBar().setFocusable(false);

		driver.makeInputAreaEditable(true);
		driver.inputArea().setFocusable(true);
		driver.inputArea().requestFocus();

		driver.setStatusBarText("");//path to current file not identified after find
		if (finder.previousFindPosition >= 0) {
			driver.setSelectionStart(finder.previousFindPosition);
			driver.setSelectionEnd(finder.previousFindPosition + finder.previousFindTextWithoutPrefixLength());
		} else {
			driver.setCursorPosition(previousCursorPosition);
		}
		driver.inputAreaHighlighter().removeAllHighlights();
		driver.setInputAreaKeyListener(keyListener);
		driver.setInputAreaCaretListener(caretListener);
	}
	private int previousCursorPosition = 0;
	private void findMode() {
		previousCursorPosition = driver.inputArea().getCaretPosition();

		driver.makeInputAreaEditable(false);
		driver.inputAreaHighlighter().removeAllHighlights();
		driver.statusBar().getHighlighter().removeAllHighlights();
		driver.statusBar().setEditable(true);

		driver.statusBar().setText(FIND_MESSAGE + finder.previousFindText());
		finder = new Finder();

		driver.statusBar().setFocusable(true);
		driver.statusBar().requestFocus();
		driver.inputArea().setFocusable(false);
		driver.statusBar().addKeyListener(findKeyListener);
	}
}
