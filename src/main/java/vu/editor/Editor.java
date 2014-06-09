package vu.editor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Scanner;

import javax.swing.SwingUtilities;

public class Editor {
	private Editor() {}//no instances

	public static void main(final String[] args) {
		initGlobalExceptionHandler();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Driver driver = new Driver();
				driver.showGui();
				
				if(args.length == 1) {
					driver.loadEditorView(new Buffer(new File(args[0])));
				} else {
					driver.loadEditorView(new Buffer(Editor.inputStream()));
				}
			}
		});
	}

	private static String inputStream() {
		try {
			StringBuffer inputTextBuffer = new StringBuffer();
			if (System.in.available() > 0) {
				Scanner scanner = new Scanner(System.in);
				while (scanner.hasNextLine()) {
					inputTextBuffer.append(scanner.nextLine()).append('\n');
				}
				scanner.close();
			}
			return inputTextBuffer.toString().trim();
		} catch (IOException e) {
			throw new RuntimeException();
		}
	}

	private static void initGlobalExceptionHandler() {
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override public void uncaughtException(Thread thread, Throwable error) {
				Gui.alert(error.getMessage());
				log(error);
			}
		});
	} 

	private static final Writer logWriter;
	static {
		File logFile = new File(System.getProperty("user.home") + "/.vueditor/log");
		logFile.getParentFile().mkdirs();
		try {
			logFile.createNewFile();
			boolean appendToExistingLog = true;
			logWriter = new BufferedWriter(new FileWriter(logFile, appendToExistingLog));
			log("==========Editor started: " + new Date().toString());
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override public void run() {
					try {
						log("==========Editor stopped: " + new Date().toString());
						logWriter.close();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}}));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static int flushCounter = 0;
	static void log(String message) {
		++flushCounter;
		try {
			logWriter.write(message + "\n");
			if (flushCounter == 100) {
				logWriter.flush();
				flushCounter = 0;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	static void log(Throwable error) {
		StringWriter stringWriter = new StringWriter();
		error.printStackTrace(new PrintWriter(stringWriter));
		log(stringWriter.toString());
	}
}