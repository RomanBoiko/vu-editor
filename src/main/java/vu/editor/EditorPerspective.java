package vu.editor;

import static java.awt.event.KeyEvent.VK_ALT;
import static java.awt.event.KeyEvent.VK_C;
import static java.awt.event.KeyEvent.VK_CONTROL;
import static java.awt.event.KeyEvent.VK_D;
import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_F;
import static java.awt.event.KeyEvent.VK_J;
import static java.awt.event.KeyEvent.VK_S;
import static java.awt.event.KeyEvent.VK_SHIFT;
import static java.awt.event.KeyEvent.VK_TAB;
import static java.awt.event.KeyEvent.VK_UP;
import static java.awt.event.KeyEvent.VK_W;
import static java.awt.event.KeyEvent.VK_Z;

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
				} else if (shortcutDetected(VK_CONTROL, VK_SHIFT, VK_F)) {
					Texts.formatXml(driver);
				} else if (shortcutDetected(VK_CONTROL, VK_D)) {
					Texts.deleteLine(driver);
				} else if (shortcutDetected(VK_ALT, VK_DOWN)) {
					Texts.moveLinesDown(driver);
				} else if (shortcutDetected(VK_ALT, VK_UP)) {
					Texts.moveLinesUp(driver);
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

}
