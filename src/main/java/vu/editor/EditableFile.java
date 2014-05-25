package vu.editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class EditableFile {
	private static final String DEFAULT_PATH = "newEmptyFile.txt";
	private static final String DEFAULT_TEXT = "defaultText";
	private final File file;
	private String currentText = DEFAULT_TEXT;

	public EditableFile(String pathToFile) {
		this.file = new File(pathToFile);
	}
	public EditableFile() {
		this.file = new File(DEFAULT_PATH);
	}

	public String getPath() {
		return file.getAbsolutePath();
	}

	public void setText(String text) {
		this.currentText = text;
		
	}
	public String getText() {
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

	public void saveText(String text) {
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(text);
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String getFileName() {
		return file.getName();
	}
}
