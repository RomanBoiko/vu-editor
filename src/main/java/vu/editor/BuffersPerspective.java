package vu.editor;

import java.awt.event.KeyEvent;
import java.util.Stack;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class BuffersPerspective extends Perspective {
	private final Driver driver;
	private final Buffers buffers = new Buffers();
	private final KeyboardListener keyListener;
	private final CaretListener caretListener;

	public BuffersPerspective(Driver driver) {
		this.driver = driver;
		this.keyListener = new KeyboardListener(driver) {
			@Override protected void actionOnKeyPressed() {
				if (shortcutDetected(KeyEvent.VK_ESCAPE)) {
					driver.loadEditorView();
				} else if (shortcutDetected(KeyEvent.VK_RIGHT)) {
					driver.setCursorPosition(TextActions.secondPositionInCurrentRow(driver));
				} else if (shortcutDetected(KeyEvent.VK_LEFT)) {
					driver.setCursorPosition(TextActions.secondPositionInCurrentRow(driver));
				} else if (shortcutDetected(KeyEvent.VK_ENTER)) {
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

	private void loadEditorWithFile() {
		EditableFile buffer = buffers.selectBufferAsCurrent(TextActions.currentRow(driver));
		driver.loadEditorView(buffer);
	}

	private void highlightCurrentItem() {
		TextActions.highlightCurrentLine(driver);
		
	}
	EditableFile currentBuffer() {
		return buffers.currentBuffer();
	}	
	void addCurrentBuffer(EditableFile buffer) {
		buffers.addCurrentBuffer(buffer);
	}
	public void closeCurrentBuffer() {
		buffers.closeCurrentBuffer();
	}
	void loadBuffersView() {
		driver.makeInputAreaEditable(false);
		driver.setInputAreaKeyListener(keyListener);
		driver.setInputAreaCaretListener(caretListener);
		driver.setText(buffers.asString());
		driver.setTitle("Buffers");
		driver.setStatusBarText("Buffers");
		driver.setCursorPosition(0);
		highlightCurrentItem();
	}
	
	private class Buffers {
		private final Stack<EditableFile> buffers = new Stack<EditableFile>();

		void addCurrentBuffer(EditableFile buffer) {
			for (int i = 0; i < buffers.size(); i++) {
				if (buffers.get(i).getPath().equals(buffer.getPath())) {
					EditableFile existingBuffer = buffers.remove(i);
					buffers.push(existingBuffer);
					return;
				}
			}
			buffers.push(buffer);
		}
		EditableFile currentBuffer() {
			return buffers.peek();
		}
		void closeCurrentBuffer() {
			if (buffers.size() > 1) {
				buffers.pop();
			}
		}

		EditableFile selectBufferAsCurrent(int currentRow) {
			EditableFile newCurrentBuffer = buffers.remove(buffers.size()-currentRow);
			buffers.push(newCurrentBuffer);
			return newCurrentBuffer;
		}
		String asString() {
			StringBuffer result = new StringBuffer();
			for (int i = buffers.size()-1; i >=0; i--) {
				result.append(bufferToString(buffers.get(i))).append(TextActions.LINE_SEPARATOR);
			}
			return result.toString().trim();
		}
		private String bufferToString(EditableFile buffer) {
			return buffer.hasUnsavedChanges()
					? "| *unsaved* | " + buffer.getPath()
					: "|           | " + buffer.getPath();
		}
	}
}
