package vu.editor;

import java.io.InputStream;
import java.util.Scanner;

class Streams {
	private Streams() {}

	static String streamToString(InputStream stream) {
		Scanner scanner = new Scanner(stream).useDelimiter("\\A");
		String result = scanner.hasNext() ? scanner.next() : "";
		scanner.close();
		return result;
	}
}
