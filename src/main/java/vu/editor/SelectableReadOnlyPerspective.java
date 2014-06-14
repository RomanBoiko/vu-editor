package vu.editor;

import java.awt.event.KeyEvent;

import javax.swing.event.CaretListener;

abstract class SelectableReadOnlyPerspective extends Perspective {
	protected final Driver driver;
	private final KeyboardListener keyListener;
	private final CaretListener caretListener;

	SelectableReadOnlyPerspective(Driver driver) {
		this.driver = driver;
		this.keyListener = new KeyboardListener(driver) {
			@Override protected void actionOnKeyPressed() {
				if (shortcutDetected(KeyEvent.VK_ESCAPE)) {
					driver.loadEditorView();
				} else if (shortcutDetected(KeyEvent.VK_RIGHT)) {
					driver.setCursorPosition(Texts.secondPositionInCurrentRow(driver));
				} else if (shortcutDetected(KeyEvent.VK_LEFT)) {
					driver.setCursorPosition(Texts.secondPositionInCurrentRow(driver));
				} else if (shortcutDetected(KeyEvent.VK_ENTER)) {
					actionOnSelected();
					stopLastKeyPressedEventPropagation(); //prevents editor from adding new line after resource is loaded
				}
			}
		};
		this.caretListener = new HighlightingCurrentLineCaretListener(driver);
	}

	final void show() {
		driver.makeInputAreaEditable(false);
		driver.setInputAreaKeyListener(keyListener);
		driver.setInputAreaCaretListener(caretListener);
		driver.setText(mainText());
		driver.setTitle(title());
		driver.setStatusBarText(statusBarText());
		driver.setCursorPosition(cursorPositionOnLoad());
		driver.highlightCurrentLine();
	}

	protected abstract void actionOnSelected();

	protected abstract String mainText();

	protected abstract String title();

	protected abstract String statusBarText();

	protected int cursorPositionOnLoad() {
		return 0;
	}
}
