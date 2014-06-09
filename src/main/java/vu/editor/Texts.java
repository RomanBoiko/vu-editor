package vu.editor;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

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

public class Texts {
	private Texts() { }
	static final String EMPTY_STRING = "";
	static final char SPACE = ' ';
	static final String SPACE_STR = Character.toString(SPACE);
	static final char TAB = '\t';
	static final String TAB_STR = Character.toString(TAB);
	static final char LINE_SEPARATOR = '\n';
	static final String LINE_SEPARATOR_STR = Character.toString(LINE_SEPARATOR);

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
	
	static void replaceContentOfCurrentRow(Driver driver, String textToInsert) {
		String text = driver.text();
		int position = driver.selectionStart();
		driver.replaceRange(textToInsert, startOfLineWithoutLineEnd(text, position), endOfLineWithoutLineEnd(text, position));
	}
	static void replaceContentOfCurrentAndNextRows(Driver driver, int numberOfRowsToRemoveAfterwards, String textToInsert) {
		String text = driver.text();
		int position = driver.selectionStart();
		int endOfRange = endOfLineWithoutLineEnd(text, position);
		for (int i = 0; i < numberOfRowsToRemoveAfterwards; i++) {
			endOfRange = endOfLineWithoutLineEnd(text, endOfRange + 1);
		}
		driver.replaceRange(textToInsert, startOfLineWithoutLineEnd(text, position), endOfRange);
	}

	static void moveLinesUp(Driver driver) {
		String text = driver.text();
		int startOfLineWithoutLineEnd = startOfLineWithoutLineEnd(text, driver.selectionStart());
		int endOfLineWithoutLineEnd = endOfLineWithoutLineEnd(text, driver.selectionEnd());
		if (startOfLineWithoutLineEnd == 0) { return; }
		String textToMove = text.substring(startOfLineWithoutLineEnd, endOfLineWithoutLineEnd) + LINE_SEPARATOR;
		int positionToInsertTextInto = startOfLineWithoutLineEnd(text, startOfLineWithoutLineEnd - 1);
		removeEndOfLineIfExists(driver, text, endOfLineWithoutLineEnd);
		driver.replaceRange(EMPTY_STRING, startOfLineWithoutLineEnd, endOfLineWithoutLineEnd);
		moveLinesAndSetSelection(driver, textToMove, positionToInsertTextInto);
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
		moveLinesAndSetSelection(driver, textToMove, positionToInsertTextInto);
	}
	private static void moveLinesAndSetSelection(Driver driver, String textToMove, int positionToInsertTextInto) {
		driver.insert(textToMove, positionToInsertTextInto);
		driver.setSelectionStart(positionToInsertTextInto);
		driver.setSelectionEnd(positionToInsertTextInto + textToMove.length() - 1);
	}
	static void duplicateLines(Driver driver) {
		String text = driver.text();
		int startOfLineWithoutLineEnd = startOfLineWithoutLineEnd(text, driver.selectionStart());
		int endOfLineWithoutLineEnd = endOfLineWithoutLineEnd(text, driver.selectionEnd());
		String textToMove = LINE_SEPARATOR + text.substring(startOfLineWithoutLineEnd, endOfLineWithoutLineEnd);
		driver.insert(textToMove, endOfLineWithoutLineEnd);
		driver.setSelectionStart(endOfLineWithoutLineEnd + 1);
		driver.setSelectionEnd(endOfLineWithoutLineEnd + textToMove.length());
	}


	public final static HighlightPainter SPACE_PAINTER = new DefaultHighlighter.DefaultHighlightPainter(Color.YELLOW);
	public final static HighlightPainter TAB_PAINTER = new DefaultHighlighter.DefaultHighlightPainter(Color.BLUE);
	static void showOrHideWhitespacesAndHighlights(Driver driver) {
		driver.setCursorPosition(driver.selectionStart());//to fix bug #1
		Highlighter highlighter = driver.inputAreaHighlighter();
		for (Highlight highlight : highlighter.getHighlights()) {
			if (highlight.getPainter().equals(SPACE_PAINTER) || highlight.getPainter().equals(TAB_PAINTER)) {
				removeSimilarHighlights(highlighter, SPACE_PAINTER);
				removeSimilarHighlights(highlighter, TAB_PAINTER);
				return;
			}
		}

		String text = driver.text();
		boolean precedingWhitespaces = true;
		boolean trailingSpaces = false;
		int startOfTrailingSpaces = 0;
		for (int pos = 0; pos < text.length(); pos++) {
			char currentChar = text.charAt(pos);
			if (precedingWhitespaces) {
				if (currentChar == SPACE) {
					highlightChar(pos, highlighter, SPACE_PAINTER);
				} else if (currentChar == TAB) {
					highlightChar(pos, highlighter, TAB_PAINTER);
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
	}

	private static void highlightWhiteSpaceRange(int startOfTrailingSpaces, int endOfTrailingSpaces, String text, Highlighter highlighter) {
		for (int i = startOfTrailingSpaces; i < endOfTrailingSpaces; i++) {
			if (text.charAt(i) == SPACE) {
				highlightChar(i, highlighter, SPACE_PAINTER);
			} else {
				highlightChar(i, highlighter, TAB_PAINTER);
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

	private final static HighlightPainter SELECTION_PAINTER = new DefaultHighlighter.DefaultHighlightPainter(Color.DARK_GRAY);
	static void highlightCurrentLine(Driver driver) {
		String text = driver.text();
		int startOfLineWithoutLineEnd = startOfLineWithoutLineEnd(text, driver.selectionStart());
		int endOfLineWithoutLineEnd = endOfLineWithoutLineEnd(text, driver.selectionStart());
		
		Highlighter highlighter = driver.inputAreaHighlighter();
		removeSimilarHighlights(highlighter, SELECTION_PAINTER);
		highlightText(startOfLineWithoutLineEnd, endOfLineWithoutLineEnd, highlighter, SELECTION_PAINTER);
	}

	public static final HighlightPainter MATCHING_BRACKET_PAINTER = new DefaultHighlighter.DefaultHighlightPainter(Color.RED);
	private static final Map<Character, Character> START_BRACKET_TO_END_BRACKET = new HashMap<Character, Character>() {private static final long serialVersionUID = 1L;{
		put('(', ')');
		put('[', ']');
		put('{', '}');
	}};
	private static final Map<Character, Character> END_BRACKET_TO_START_BRACKET = new HashMap<Character, Character>() {private static final long serialVersionUID = 1L;{
		for (Map.Entry<Character, Character> startToEndBracket : START_BRACKET_TO_END_BRACKET.entrySet()) {
			put(startToEndBracket.getValue(), startToEndBracket.getKey());
		}
	}};
	private static final Set<Character> BRACKETS = new HashSet<Character>() {private static final long serialVersionUID = 1L;{
		addAll(START_BRACKET_TO_END_BRACKET.keySet());
		addAll(START_BRACKET_TO_END_BRACKET.values());
	}};
	static void highlightMatchingBrackets(Driver driver) {
		Highlighter highlighter = driver.inputAreaHighlighter();
		removeSimilarHighlights(highlighter, MATCHING_BRACKET_PAINTER);

		String text = driver.text();
		int currentPosition = driver.selectionStart();

		if (currentPosition != text.length() && BRACKETS.contains(text.charAt(currentPosition))) {
			highlightMatchingBrackets(currentPosition, text, highlighter);
		} else if (currentPosition != 0 && BRACKETS.contains(text.charAt(currentPosition - 1))) {
			highlightMatchingBrackets(currentPosition - 1, text, highlighter);
		}
	}
	private static void highlightMatchingBrackets(int bracketPosition, String text, Highlighter highlighter) {
		Stack<Character> expectedBracketsOnTheWay = new Stack<Character>();
		char bracket = text.charAt(bracketPosition);
		if (START_BRACKET_TO_END_BRACKET.containsKey(bracket)) {
			expectedBracketsOnTheWay.push(START_BRACKET_TO_END_BRACKET.get(bracket));
			for (int i = bracketPosition + 1; i < text.length(); i++) {
				char currentChar = text.charAt(i);
				if (BRACKETS.contains(currentChar)) {
					if (expectedBracketsOnTheWay.peek() == currentChar) {
						expectedBracketsOnTheWay.pop();
					} else if (START_BRACKET_TO_END_BRACKET.containsKey(currentChar)) {
						expectedBracketsOnTheWay.push(START_BRACKET_TO_END_BRACKET.get(currentChar));
					} else {
						return;
					}
				}
				if (expectedBracketsOnTheWay.isEmpty()) {
					int matchingBracketPosition = i;
					highlightChar(bracketPosition, highlighter, MATCHING_BRACKET_PAINTER);
					highlightChar(matchingBracketPosition, highlighter, MATCHING_BRACKET_PAINTER);
					return;
				} 
			}
		} else {
			expectedBracketsOnTheWay.push(END_BRACKET_TO_START_BRACKET.get(bracket));
			for (int i = bracketPosition - 1; i >= 0; i--) {
				char currentChar = text.charAt(i);
				if (BRACKETS.contains(currentChar)) {
					if (expectedBracketsOnTheWay.peek() == currentChar) {
						expectedBracketsOnTheWay.pop();
					} else if (END_BRACKET_TO_START_BRACKET.containsKey(currentChar)) {
						expectedBracketsOnTheWay.push(END_BRACKET_TO_START_BRACKET.get(currentChar));
					} else {
						return;
					}
				}
				if (expectedBracketsOnTheWay.isEmpty()) {
					int matchingBracketPosition = i;
					highlightChar(bracketPosition, highlighter, MATCHING_BRACKET_PAINTER);
					highlightChar(matchingBracketPosition, highlighter, MATCHING_BRACKET_PAINTER);
					return;
				}
			}
		}
	}

	private static void removeSimilarHighlights(Highlighter highlighter, HighlightPainter painter) {
		for (Highlight highlight : highlighter.getHighlights()) {
			if (highlight.getPainter().equals(painter)) {
				highlighter.removeHighlight(highlight);
			}
		}
	}
	private static void highlightText(int startOfRange, int endOfRange, Highlighter highlighter, HighlightPainter painter) {
		try {
			highlighter.addHighlight(startOfRange, endOfRange, painter);
		} catch (BadLocationException e) {
			throw new RuntimeException(e);
		}
	}
	private static void highlightChar(int charPosition, Highlighter highlighter, HighlightPainter painter) {
		highlightText(charPosition, charPosition + 1, highlighter, painter);
	}

	static int currentRow(Driver driver) {
		return (driver.text().substring(0, driver.selectionStart()) + "extratext").split(LINE_SEPARATOR_STR).length;
	}

	static int secondPositionInCurrentRow(Driver driver) {
		String text = driver.text();
		return text.substring(0, driver.selectionStart()).lastIndexOf(LINE_SEPARATOR) + 2;
	}

	private static interface LineTransformer {
		String transform(String source);
	}
	private static final LineTransformer INDENT = new LineTransformer() {
		@Override public String transform(String source) {
			return TAB_STR + source;
		}
	};
	private static final LineTransformer UNINDENT = new LineTransformer() {
		@Override public String transform(String source) {
			if (source.startsWith(TAB_STR)) {
				return source.substring(1);
			} else {
				int charactersToRemove = 0;
				for (int i = 0; i < 4 && i < source.length(); i++) {
					if (source.charAt(i) != SPACE) {
						break;
					}
					charactersToRemove++;
				}
				return source.substring(charactersToRemove);
			}
		}
	};
	private static void processSelectedLines(Driver driver, LineTransformer transformer) {
		String text = driver.text();
		int firstRowStart = startOfLineWithoutLineEnd(text, driver.selectionStart());
		int lastRowEnd = endOfLineWithoutLineEnd(text, driver.selectionEnd());
		String[] selectedLines = text.substring(firstRowStart, lastRowEnd).split(LINE_SEPARATOR_STR);
		StringBuffer indentedBlock = new StringBuffer();
		for (int i = 0; i < selectedLines.length-1; i++) {
			indentedBlock.append(transformer.transform(selectedLines[i])).append(LINE_SEPARATOR);
		}
		indentedBlock.append(transformer.transform(selectedLines[selectedLines.length-1]));
		String indentedBlockString = indentedBlock.toString();
		driver.replaceRange(indentedBlockString, firstRowStart, lastRowEnd);
		driver.setSelectionStart(firstRowStart);
		driver.setSelectionEnd(firstRowStart + indentedBlockString.length());
	} 

	static void indent(Driver driver) {
		processSelectedLines(driver, INDENT);
	}
	static void unindent(Driver driver) {
		processSelectedLines(driver, UNINDENT);
	}
	
	static boolean selectionContainsMultipleRows(Driver driver) {
		return driver.text().substring(driver.selectionStart(), driver.selectionEnd()).contains(LINE_SEPARATOR_STR);
	}

}
