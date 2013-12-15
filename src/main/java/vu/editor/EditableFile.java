package vu.editor;

import static com.google.common.base.Joiner.on;
import static com.google.common.io.Files.write;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

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
			return on('\n').join(Files.readLines(file, Charsets.UTF_8));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void saveText(String text) throws IOException {
		write(text.getBytes(), file);
	}

	public String getFileName() {
		return file.getName();
	}
}