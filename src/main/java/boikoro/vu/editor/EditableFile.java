package boikoro.vu.editor;

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

	public String getText() throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		FileInputStream fileInputStream = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
		String read = reader.readLine();

		while (read != null) {
			stringBuilder.append(read).append("\n");
			read = reader.readLine();

		}
		reader.close();
		fileInputStream.close();
		return stringBuilder.toString();
	}

	public void saveText(String text) throws IOException {
		FileWriter writer = new FileWriter(file);
		writer.write(text);
		writer.close();
	}

	public String getFileName() {
		return file.getName();
	}
}