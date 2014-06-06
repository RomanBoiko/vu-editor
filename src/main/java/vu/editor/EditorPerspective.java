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

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class EditorPerspective extends Perspective {

	private final Driver driver;
	private final KeyboardListener keyListener;
	private final CaretListener caretListener;

	public EditorPerspective(Driver driver) {
		this.driver = driver;
		this.keyListener = new KeyboardListener(driver) {
			@Override protected void actionOnKeyPressed() {
				if (shortcutDetected(VK_CONTROL, VK_S)) {
					driver.saveCurrentBuffer();
				} else if (shortcutDetected(VK_CONTROL, VK_SHIFT, VK_F)) {
					TextActions.formatXml(driver);
				} else if (shortcutDetected(VK_CONTROL, VK_D)) {
					TextActions.deleteLine(driver);
				} else if (shortcutDetected(VK_ALT, VK_DOWN)) {
					TextActions.moveLinesDown(driver);
				} else if (shortcutDetected(VK_ALT, VK_UP)) {
					TextActions.moveLinesUp(driver);
				} else if (shortcutDetected(VK_ALT, VK_W)) {
					TextActions.showOrHideWhitespacesAndHighlights(driver);
				} else if (shortcutDetected(VK_CONTROL, VK_J)) {
					TextActions.joinLines(driver);
				} else if (shortcutDetected(VK_ALT, VK_C)) {
					TextActions.toUpperCase(driver);
				} else if (shortcutDetected(VK_ALT, VK_SHIFT, VK_C)) {
					TextActions.toLowerCase(driver);
				} else if (shortcutDetected(VK_CONTROL, VK_TAB)) {
					driver.loadBuffersView();
				} else if (shortcutDetected(VK_CONTROL, VK_W)) {
					driver.closeCurrentBuffer();
				}
			}
		};
		this.caretListener = new CaretListener() {
			@Override public void caretUpdate(CaretEvent event) {
				highlightMatchingBrackets();
				highlightCurrentLine();
			}

		};
	}

	private void highlightMatchingBrackets() {
		TextActions.highlightMatchingBrackets(driver);
	}

	private void highlightCurrentLine() {
		TextActions.highlightCurrentLine(driver);
	}

	void loadResource(Buffer resource) {
		driver.makeInputAreaEditable(true);
		driver.setInputAreaKeyListener(keyListener);
		driver.setInputAreaCaretListener(caretListener);
		driver.setText(resource.getText());
		driver.setTitle(resource.getFileName());
		driver.setStatusBarText(resource.getPath());
		driver.setCursorPosition(0);
	}

	@Override void actionOnExitFromPerspective() {
		driver.setCurrentBufferText();
	}
}
