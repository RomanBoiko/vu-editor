package vu.editor;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.Writer;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.Highlight;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class TextActions {
	private TextActions() { }
	private static final String EMPTY_STRING = "";
	private static final char SPACE = ' ';
	private static final String SPACE_STR = Character.toString(SPACE);
	private static final char TAB = '\t';
	private static final char LINE_SEPARATOR = '\n';
	private static final String LINE_SEPARATOR_STR = Character.toString(LINE_SEPARATOR);

	static void formatXml(Driver driver) {
		driver.setText(formatXml(driver.text()));
	}

	static String formatXml(String text) {
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilderFactory.setValidating(false);
			String xmlDeclaration = text.startsWith("<?") ? text.substring(0, text.indexOf("?>") + 2) + "\n" : "";
			String xmlWithoutXmlDeclaration = xmlDeclaration.length() == 0 ? text : text.substring(xmlDeclaration.length() - 1);
			ByteArrayInputStream textInputStream = new ByteArrayInputStream(xmlWithoutXmlDeclaration.getBytes());
			Document doc = docBuilderFactory.newDocumentBuilder().parse(textInputStream);
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			Writer out = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(out));
			return (xmlDeclaration + out.toString()).replaceAll("   ", "  ").replaceAll("\r\n", "\n");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	static void deleteLine(Driver driver) {
		String text = driver.text();
		int startOfLineWithoutLineEnd = startOfLineWithoutLineEnd(text, driver.selectionStart());
		int endOfLineWithoutLineEnd = endOfLineWithoutLineEnd(text, driver.selectionEnd());
		removeEndOfLineIfExists(driver, text, endOfLineWithoutLineEnd);
		driver.replaceRange(EMPTY_STRING, startOfLineWithoutLineEnd, endOfLineWithoutLineEnd);
	}

	private static int startOfLineWithoutLineEnd(String text, int positionInLine) {
		return text.substring(0, positionInLine).lastIndexOf('\n') + 1;
	}
	
	private static int endOfLineWithoutLineEnd(String text, int positionInLine) {
		int distanceFromCursorToLineEndChar = text.substring(positionInLine).indexOf(LINE_SEPARATOR);
		return distanceFromCursorToLineEndChar < 0 ? text.length() : distanceFromCursorToLineEndChar + positionInLine;
	}
	private static void removeEndOfLineIfExists(Driver driver, String text, int endOfLineWithoutLineEnd) {
		if (text.length() > endOfLineWithoutLineEnd) {
			driver.replaceRange(EMPTY_STRING, endOfLineWithoutLineEnd, endOfLineWithoutLineEnd + 1);
		}
	}

	static void moveLinesUp(Driver driver) {
		String text = driver.text();
		int startOfLineWithoutLineEnd = startOfLineWithoutLineEnd(text, driver.selectionStart());
		int endOfLineWithoutLineEnd = endOfLineWithoutLineEnd(text, driver.selectionEnd());
		if (startOfLineWithoutLineEnd == 0) { return; }
		String textToMove = text.substring(startOfLineWithoutLineEnd, endOfLineWithoutLineEnd) + LINE_SEPARATOR;
		int positionToInsertTextInto = startOfLineWithoutLineEnd(text, startOfLineWithoutLineEnd -1);
		removeEndOfLineIfExists(driver, text, endOfLineWithoutLineEnd);
		driver.replaceRange(EMPTY_STRING, startOfLineWithoutLineEnd, endOfLineWithoutLineEnd);
		driver.insert(textToMove, positionToInsertTextInto);
		driver.setCursorPosition(positionToInsertTextInto);
	}

	static void moveLinesDown(Driver driver) {
		String text = driver.text();
		int startOfLineWithoutLineEnd = startOfLineWithoutLineEnd(text, driver.selectionStart());
		int endOfLineWithoutLineEnd = endOfLineWithoutLineEnd(text, driver.selectionEnd());
		if (endOfLineWithoutLineEnd == text.length() || 
			(endOfLineWithoutLineEnd + 1 == text.length() && text.endsWith(LINE_SEPARATOR_STR))) { return; }
		String textToMove = text.substring(startOfLineWithoutLineEnd, endOfLineWithoutLineEnd) + LINE_SEPARATOR;
		driver.replaceRange(EMPTY_STRING, startOfLineWithoutLineEnd, endOfLineWithoutLineEnd);
		removeEndOfLineIfExists(driver, driver.text(), startOfLineWithoutLineEnd);
		int positionToInsertTextInto = endOfLineWithoutLineEnd(driver.text(), startOfLineWithoutLineEnd) + 1;
		driver.insert(textToMove, positionToInsertTextInto);
		driver.setCursorPosition(positionToInsertTextInto);
	}

	private final static HighlightPainter SPACE_PAINTER = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
	private final static HighlightPainter TAB_PAINTER = new DefaultHighlighter.DefaultHighlightPainter(Color.BLUE);
	static void showOrHideWhitespacesAndHighlights(Driver driver) {
		driver.setCursorPosition(driver.selectionStart());
		Highlighter highlighter = driver.inputAreaHighlighter();
		Highlight[] highlights = highlighter.getHighlights();
		if (highlights.length > 0) {
			highlighter.removeAllHighlights();
			return;
		}

		String text = driver.text();
		boolean precedingWhitespaces = true;
		boolean trailingSpaces = false;
		int startOfTrailingSpaces = 0;
		try {
			for (int pos = 0; pos < text.length(); pos++) {
				char currentChar = text.charAt(pos);
				if (precedingWhitespaces) {
					if (currentChar == SPACE) {
						highlighter.addHighlight(pos, pos + 1, SPACE_PAINTER);
					} else if (currentChar == TAB) {
						highlighter.addHighlight(pos, pos + 1, TAB_PAINTER);
					} else if (currentChar == LINE_SEPARATOR) {
						trailingSpaces = false;
					} else {
						precedingWhitespaces = false;
					}
				} else if (currentChar == SPACE || currentChar == TAB) {
					if (!trailingSpaces) {
						trailingSpaces = true;
						startOfTrailingSpaces = pos;
					}
				} else if (currentChar == LINE_SEPARATOR) {
					if (trailingSpaces == true) {
						highlightWhiteSpaceRange(startOfTrailingSpaces, pos, text, highlighter);
						trailingSpaces = false;
					}
					precedingWhitespaces = true;
				} else {
					trailingSpaces = false;
				}
			}
			if (trailingSpaces) {
				highlightWhiteSpaceRange(startOfTrailingSpaces, text.length(), text, highlighter);
			}
		} catch (BadLocationException e) {
			throw new RuntimeException(e);
		}
	}

	private static void highlightWhiteSpaceRange(int startOfTrailingSpaces, int endOfTrailingSpaces, String text, Highlighter highlighter) throws BadLocationException {
		for (int i = startOfTrailingSpaces; i < endOfTrailingSpaces; i++) {
			if (text.charAt(i) == SPACE) {
				highlighter.addHighlight(i, i + 1, SPACE_PAINTER);
			} else {
				highlighter.addHighlight(i, i + 1, TAB_PAINTER);
			}
		} 
	}

	static void joinLines(Driver driver) {
		String text = driver.text();
		int cursorPosition = driver.selectionStart();
		int lineEndPosition = text.indexOf(LINE_SEPARATOR, cursorPosition);
		if (lineEndPosition < 0) { return; }
		driver.replaceRange(SPACE_STR, lineEndPosition, lineEndPosition + 1);
		driver.setCursorPosition(lineEndPosition);
	}

	static void toUpperCase(Driver driver) {
		String text = driver.text();
		int selectionStart = driver.selectionStart();
		int selectionEnd = driver.selectionEnd();
		if (selectionStart == selectionEnd) { return; }
		String replacement = text.substring(selectionStart, selectionEnd).toUpperCase();
		driver.replaceRange(replacement, selectionStart, selectionEnd);
	}

	static void toLowerCase(Driver driver) {
		String text = driver.text();
		int selectionStart = driver.selectionStart();
		int selectionEnd = driver.selectionEnd();
		if (selectionStart == selectionEnd) { return; }
		String replacement = text.substring(selectionStart, selectionEnd).toLowerCase();
		driver.replaceRange(replacement, selectionStart, selectionEnd);
	}
}
