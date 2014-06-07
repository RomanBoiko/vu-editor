package vu.editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class Buffer {
	private static final String DEFAULT_PATH = "newEmptyFile.txt";
	private static final String DEFAULT_TEXT = "defaultText";
	private static final int MAX_UNDO_HISTORY_SIZE = 30;
	private final File file;
	private String currentText = DEFAULT_TEXT;
	private boolean hasUnsavedChanges = false;

	Buffer(String pathToFile) {
		this.file = new File(pathToFile);
		states.addFirst(new BufferState(getText(), 0));
	}
	Buffer() {
		this(DEFAULT_PATH);
	}

	String getPath() {
		return file.getAbsolutePath();
	}

	void setText(String text) {
		if (!text.equals(getText())) {
			hasUnsavedChanges = true;
		}
		this.currentText = text;
		
	}
	String getText() {
		if (!DEFAULT_TEXT.equals(currentText)) {//to fix bug #3
			return currentText;
		}
		if (!file.exists()) { return ""; }
		try {
			StringBuilder stringBuilder = new StringBuilder();
			FileInputStream fileInputStream;
				fileInputStream = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
			String read = reader.readLine();
	
			while (read != null) {
				stringBuilder.append(read).append("\n");
				read = reader.readLine();
	
			}
			reader.close();
			fileInputStream.close();
			return stringBuilder.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	void saveText(String text) {
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(text);
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		this.currentText = text;
		hasUnsavedChanges = false;
	}

	public String getFileName() {
		return file.getName();
	}

	boolean hasUnsavedChanges() {
		return hasUnsavedChanges;
	}
	
	static class BufferState {
		final String text;
		final int caretPosition;
		public BufferState(String text, int caretPosition) {
			this.text = text;
			this.caretPosition = caretPosition;
		}
	}

	private final LinkedList<BufferState> states = new LinkedList<BufferState>();
	private int currentState = 0;
	BufferState rollbackToPreviousState() {
		if (currentState < states.size()-1) {
			currentState++;
		}
		return states.get(currentState);
	}
	BufferState forwardToUndoneChange() {
		if (currentState > 0) {
			currentState--;
		}
		return states.get(currentState);
	}
	boolean isStateUpToDate(String text) {
		return text.equals(states.get(currentState).text);
	}
	void recordNewState(String text, int caretPosition) {
		for (int i = 0; i < currentState; i++) {
			states.removeFirst();
		}
		currentState = 0;
		states.addFirst(new BufferState(text, caretPosition));
		while (states.size() > MAX_UNDO_HISTORY_SIZE) {
			states.removeLast();
		}
	}
}
