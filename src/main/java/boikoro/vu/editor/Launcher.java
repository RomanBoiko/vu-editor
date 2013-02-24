package boikoro.vu.editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.SwingUtilities;

public class Launcher {
	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					if(args.length == 1) {
						Gui.startNewGui(new EditableFile(args[0]));
					} else {
						Gui.startNewGui();
					}
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	public static class EditableFile {
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

		public String getFileName() {
			return file.getName();
		}
	}
}