package vu.editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class EditableFile {
	private final File file;

	public EditableFile(String pathToFile) {
		this.file = new File(pathToFile);
	}

	public String getPath() {
		return file.getAbsolutePath();
	}

	public String getText() {
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

class NewEmptyFile extends EditableFile {
	private static final String DEFAULT_PATH = "newEmptyFile.txt";

	private boolean alreadySaved = false;

	public NewEmptyFile(String pathToFile) {
		super(pathToFile);
	}
	public NewEmptyFile() {
		super(DEFAULT_PATH);
	}

	public String getText() {
		return fileNotExistsYet() ? "" : super.getText();
	}
	private boolean fileNotExistsYet() {
		return DEFAULT_PATH.equals(getFileName()) && !alreadySaved;
	}

	public void saveText(String text) {
		alreadySaved = true;
		super.saveText(text);
	}
}