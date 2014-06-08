package vu.editor;

import java.awt.event.KeyEvent;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class BuffersPerspective extends Perspective {
	private final Driver driver;
	
	private final KeyboardListener keyListener;
	private final CaretListener caretListener;

	public BuffersPerspective(Driver driver) {
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
					driver.loadBufferIntoEditor();
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

	private void highlightCurrentItem() {
		Texts.highlightCurrentLine(driver);
	}
	void loadBuffersView() {
		driver.makeInputAreaEditable(false);
		driver.setInputAreaKeyListener(keyListener);
		driver.setInputAreaCaretListener(caretListener);
		driver.setText(driver.buffersAsString());
		driver.setTitle("Buffers");
		driver.setStatusBarText("Buffers");
		driver.setCursorPosition(0);
		highlightCurrentItem();
	}
}
