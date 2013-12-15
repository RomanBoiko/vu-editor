package vu.editor;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class Log {
	static Log instance = new Log();
	static void info(String message) {
		instance.writeToLog(message, "INFO");
	}
	static void debug(String message) {
		instance.writeToLog(message, "DEBUG");
	}

	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final File logFile = new File(System.getProperty("user.home"), ".vue/log");

	private Log() {
		logFile.getParentFile().mkdirs();
	}

	private void writeToLog(String message, String level) {
		try {
			Files.append(String.format("%s [%s] %s\n", dateFormat.format(new Date()), level , message), logFile, Charsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
