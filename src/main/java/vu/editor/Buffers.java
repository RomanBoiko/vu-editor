package vu.editor;

import java.util.Stack;

class Buffers {
	private final Stack<Buffer> buffers = new Stack<Buffer>();

	void addCurrentBuffer(Buffer buffer) {
		for (int i = 0; i < buffers.size(); i++) {
			if (buffers.get(i).getPath().equals(buffer.getPath())) {
				Buffer existingBuffer = buffers.remove(i);
				buffers.push(existingBuffer);
				return;
			}
		}
		buffers.push(buffer);
	}
	Buffer currentBuffer() {
		return buffers.peek();
	}
	void setCurrentBufferText(String text) {
		currentBuffer().setText(text);
	}
	void saveCurrentBuffer(String text) {
		currentBuffer().saveText(text);
	}
	void saveAllOpenBuffers() {
		for (Buffer buffer : buffers) {
			if (buffer.hasUnsavedChanges()) {
				buffer.saveText(buffer.getText());
			}
		}
	}
	void closeCurrentBuffer() {
		if (buffers.size() > 1) {
			buffers.pop();
		}
	}

	Buffer selectBufferAsCurrent(int currentRow) {
		Buffer newCurrentBuffer = buffers.remove(buffers.size() - currentRow);
		buffers.push(newCurrentBuffer);
		return newCurrentBuffer;
	}
	String asString() {
		StringBuffer result = new StringBuffer();
		for (int i = buffers.size()-1; i >=0; i--) {
			result.append(bufferToString(buffers.get(i))).append(Texts.LINE_SEPARATOR);
		}
		return result.toString().trim();
	}
	private String bufferToString(Buffer buffer) {
		return buffer.hasUnsavedChanges()
				? "| *unsaved* | " + buffer.getPath()
				: "|           | " + buffer.getPath();
	}
}