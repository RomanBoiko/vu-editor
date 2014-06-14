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
	
	private final Find caseInsensitiveFinder;
	private final Find caseSensitiveFinder;
	private final Find regExpFinder;
	private Find currentFind;

	private AtomicBoolean toRecordStateChange = new AtomicBoolean(true);
	public EditorPerspective(Driver driver) {
		this.driver = driver;
		this.caseInsensitiveFinder = new FindCaseInsensitive(driver);
		this.caseSensitiveFinder = new FindCaseSensitive(driver);
		this.regExpFinder = new FindByRegexp(driver);
		this.currentFind = caseInsensitiveFinder;

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
					findMode(caseInsensitiveFinder);
				} else if (shortcutDetected(VK_CONTROL, VK_ALT, VK_F)) {
					findMode(caseSensitiveFinder);
				} else if (shortcutDetected(VK_ALT, VK_F)) {
					findMode(regExpFinder);
				} else if (shortcutDetected(VK_ALT, VK_S)) {
					driver.loadSearchView();
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
				currentFind.find();
			} else if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
				backToEditMode();
			}
		}
	};

	private void backToEditMode() {
		driver.statusBar().removeKeyListener(findKeyListener);
		driver.statusBar().setEditable(false);
		driver.statusBar().setFocusable(false);

		driver.makeInputAreaEditable(true);
		driver.inputArea().setFocusable(true);
		driver.inputArea().requestFocus();

		driver.setStatusBarText("");//path to current file not identified after find
		if (currentFind.previousFindPosition() >= 0) {
			driver.setSelectionStart(currentFind.previousFindPosition());
			driver.setSelectionEnd(currentFind.previousFindPosition() + currentFind.previousFindTextWithoutPrefixLength());
		} else {
			driver.setCursorPosition(previousCursorPosition);
		}
		driver.inputAreaHighlighter().removeAllHighlights();
		driver.setInputAreaKeyListener(keyListener);
		driver.setInputAreaCaretListener(caretListener);
	}
	private int previousCursorPosition = 0;
	private void findMode(Find newFind) {
		currentFind = newFind;
		currentFind.resetLastFoundPosition();
		previousCursorPosition = driver.inputArea().getCaretPosition();

		driver.makeInputAreaEditable(false);
		driver.inputAreaHighlighter().removeAllHighlights();
		driver.statusBar().getHighlighter().removeAllHighlights();
		driver.statusBar().setEditable(true);

		driver.statusBar().setText(currentFind.initialFindMessage());

		driver.statusBar().setFocusable(true);
		driver.statusBar().requestFocus();
		driver.inputArea().setFocusable(false);
		driver.statusBar().addKeyListener(findKeyListener);
	}
}
