package vu.editor;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

import net.java.openjdk.cacio.ctc.junit.CacioTestRunner;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CacioTestRunner.class)
public class TextsTest {

	private final String testText = "1\n2\n3\n4\n5";
	private final JTextArea testArea = new JTextArea();
	private final Driver driver = new Driver() {
		@Override protected JTextArea inputArea() {
			return testArea;
		}
	};

	@After
	public void after() {
		initialTextWithSelection("", 0, 0);
		initialTextWithCursorAt("", 0);
		testArea.getHighlighter().removeAllHighlights();
	}
	
	private void initialText(String text) {
		testArea.setText(text);
	}
	private void initialTextWithCursorAt(String text, int cursorPosition) {
		initialText(text);
		testArea.setCaretPosition(cursorPosition);
	}
	private void initialTextWithSelection(String text, int startPosition, int endPosition) {
		initialText(text);
		testArea.setSelectionStart(startPosition);
		testArea.setSelectionEnd(endPosition);
	}
	
	@Test public void formatsXml() {
		Assert.assertEquals("<a xmlns:a=\"http\">\n<b>c</b>\n</a>\n", Texts.formatXml("<a xmlns:a=\"http\"><b>c</b></a>"));
		Assert.assertEquals("<?XML?>\n<a/>\n", Texts.formatXml("<?XML?><a/>"));
	}

	//testText:
	//positions:  0 1  2 3  4 5  6 7  8 9
	//characters:  1 \n 2 \n 3 \n 4 \n 5
	private void assertResultedTextIs(String expected) {
		assertThat(testArea.getText(), is(expected));
	}
	private void assertCursorPsotionIs(int position) {
		assertThat(testArea.getSelectionStart(), is(position));
	}

	@Test public void deletesFirstLine() {
		initialTextWithCursorAt(testText, 1);
		Texts.deleteLine(driver);
		assertResultedTextIs("2\n3\n4\n5");
	}
	@Test public void deletesLineFromTheMiddle() {
		initialTextWithCursorAt(testText, 2);
		Texts.deleteLine(driver);
		assertResultedTextIs("1\n3\n4\n5");
	}
	@Test public void deletesLastLineWithoutEndOfFile() {
		initialTextWithCursorAt(testText, 8);
		Texts.deleteLine(driver);
		assertResultedTextIs("1\n2\n3\n4\n");
	}
	@Test public void deletesLastLineBeforeWithEndOfFile() {
		initialTextWithCursorAt(testText + "\n", 9);
		Texts.deleteLine(driver);
		assertResultedTextIs("1\n2\n3\n4\n");
	}
	@Test public void deletesNothingIfNoText() {
		initialTextWithCursorAt("", 0);
		Texts.deleteLine(driver);
		assertResultedTextIs("");
	}

	//testText:
	//positions:  0 1  2 3  4 5  6 7  8 9
	//characters:  1 \n 2 \n 3 \n 4 \n 5
	@Test public void doesNotMoveFirstLineUp() {
		initialTextWithSelection(testText, 1, 1);
		Texts.moveLinesUp(driver);
		assertResultedTextIs(testText);
	}
	@Test public void movesSecondLineUp() {
		initialTextWithSelection(testText, 2, 2);
		Texts.moveLinesUp(driver);
		assertResultedTextIs("2\n1\n3\n4\n5");
	}
	@Test public void movesThirdLineUp() {
		initialTextWithSelection(testText, 5, 5);
		Texts.moveLinesUp(driver);
		assertResultedTextIs("1\n3\n2\n4\n5");
	}
	@Test public void movesLastLineUp() {
		initialTextWithSelection(testText, 9, 9);
		Texts.moveLinesUp(driver);
		assertResultedTextIs("1\n2\n3\n5\n4\n");
	}
	@Test public void movesLastLineWithLineEndUp() {
		initialTextWithSelection(testText + "\n", 9, 9);
		Texts.moveLinesUp(driver);
		assertResultedTextIs("1\n2\n3\n5\n4\n");
	}
	@Test public void movesMultipleLinesUp() {
		initialTextWithSelection(testText, 4, 6);
		Texts.moveLinesUp(driver);
		assertResultedTextIs("1\n3\n4\n2\n5");
	}
	@Test public void putsCursorIntoStartOfMovedUpLine() {
		initialTextWithSelection(testText, 5, 5);
		Texts.moveLinesUp(driver);
		assertCursorPsotionIs(2);
	}

	//testText:
	//positions:  0 1  2 3  4 5  6 7  8 9
	//characters:  1 \n 2 \n 3 \n 4 \n 5
	@Test public void doesNotMoveLastLineWithEndOfLineDown() {
		initialTextWithSelection(testText + "\n", 9, 9);
		Texts.moveLinesDown(driver);
		assertResultedTextIs(testText + "\n");
	}
	@Test public void doesMoveLastLineWithFewEndsOfLineDown() {
		initialTextWithSelection(testText + "5\n\n", 9, 9);
		Texts.moveLinesDown(driver);
		assertResultedTextIs("1\n2\n3\n4\n\n55\n");
	}
	@Test public void doesNotMoveLastLineWithoutEndOfLineDown() {
		initialTextWithSelection(testText, 8, 8);
		Texts.moveLinesDown(driver);
		assertResultedTextIs(testText);
	}
	@Test public void doesNotMoveLastEndOfLineDown() {
		initialTextWithSelection(testText + "\n\n", 11, 11);
		Texts.moveLinesDown(driver);
		assertResultedTextIs(testText + "\n\n");
	}
	@Test public void movesFirstLineDown() {
		initialTextWithSelection(testText, 0, 0);
		Texts.moveLinesDown(driver);
		assertResultedTextIs("2\n1\n3\n4\n5");
	}
	@Test public void movesSecondLineDown() {
		initialTextWithSelection(testText, 2, 2);
		Texts.moveLinesDown(driver);
		assertResultedTextIs("1\n3\n2\n4\n5");
	}
	@Test public void movesoneBeforeLastLineDown() {
		initialTextWithSelection(testText, 6, 6);
		Texts.moveLinesDown(driver);
		assertResultedTextIs("1\n2\n3\n5\n4");
	}
	@Test public void movesMultipleLinesDown() {
		initialTextWithSelection(testText, 2, 4);
		Texts.moveLinesDown(driver);
		assertResultedTextIs("1\n4\n2\n3\n5");
	}
	@Test public void putsCursorIntoStartOfMovedDownLine() {
		initialTextWithSelection(testText, 5, 5);
		Texts.moveLinesDown(driver);
		assertCursorPsotionIs(6);
	}

	@Test public void removesWhitespaceHighlightsIfThereAreAny() throws BadLocationException {
		initialText("aaaaa");
		testArea.getHighlighter().addHighlight(0, 1, Texts.SPACE_PAINTER);
		Texts.showOrHideWhitespacesAndHighlights(driver);
		assertThat(testArea.getHighlighter().getHighlights().length, is(0));
	}
	@Test public void highlightsPrecedingAndTrailingWhitespaces() {
		//                      10          20
		//           01        901    56           5678
		initialText("  av asd\n   as\n  \nas \t s\n    ");
		Texts.showOrHideWhitespacesAndHighlights(driver);
		assertThat(testArea.getHighlighter().getHighlights().length, is(11));
		assertHighlightIsAtPosition(0, 0);
		assertHighlightIsAtPosition(1, 1);
		assertHighlightIsAtPosition(2, 9);
		assertHighlightIsAtPosition(3, 10);
		assertHighlightIsAtPosition(4, 11);
		assertHighlightIsAtPosition(5, 15);
		assertHighlightIsAtPosition(6, 16);
		assertHighlightIsAtPosition(7, 25);
		assertHighlightIsAtPosition(8, 26);
		assertHighlightIsAtPosition(9, 27);
		assertHighlightIsAtPosition(10, 28);
	}
	@Test public void highlightsTrailingWhitespaces() {
		//             234     8910
		initialText("as  \t\nas  \t");
		Texts.showOrHideWhitespacesAndHighlights(driver);
		assertThat(testArea.getHighlighter().getHighlights().length, is(6));
		assertHighlightIsAtPosition(0, 2);
		assertHighlightIsAtPosition(1, 3);
		assertHighlightIsAtPosition(2, 4);
		assertHighlightIsAtPosition(3, 8);
		assertHighlightIsAtPosition(4, 9);
		assertHighlightIsAtPosition(5, 10);
	}
	// removes selection highlight - workaround issue #1(see known bugs)
	@Test public void removesSelectionHighlightBySettingCursorToSelectionStart() {
		initialText("aaaa");
		testArea.setSelectionStart(1);
		testArea.setSelectionEnd(3);
		Texts.showOrHideWhitespacesAndHighlights(driver);
		assertThat(testArea.getSelectionStart(), is(1));
		assertThat(testArea.getSelectionEnd(), is(1));
	}

	private void assertHighlightIsAtPosition(int highlightNumber, int startOffset) {
		assertHighlightIsAtPosition(highlightNumber, startOffset, "highlight position is wrong");
	}
	private void assertHighlightIsAtPosition(int highlightNumber, int startOffset, String assertionMessage) {
		assertThat(assertionMessage, testArea.getHighlighter().getHighlights()[highlightNumber].getStartOffset(), is(startOffset));
		assertThat(assertionMessage, testArea.getHighlighter().getHighlights()[highlightNumber].getEndOffset(), is(startOffset+1));
	}

	@Test public void joinsCurrentLineAndNextOneOnSpaceAndSetsCursorInJoinPlace() {
		joinLinesTest("aaa\nbbb", 1, "aaa bbb", 3);
	}
	@Test public void joinsCurrentEmptyLineAndNextOneOnSpaceAndSetsCursorInJoinPlace() {
		joinLinesTest("\n\na", 1, "\n a", 1);
	}
	@Test public void doesNotAnythingIfCurrentLineIsTheLast() {
		joinLinesTest("aaa\nbbb", 5, "aaa\nbbb", 5);
	}
	private void joinLinesTest(
			String initialText, int initialCursorPosition,
			String expectedText, int expectedCursorPosition) {
		initialTextWithCursorAt(initialText, initialCursorPosition);
		Texts.joinLines(driver);
		assertResultedTextIs(expectedText);
		assertThat(testArea.getCaretPosition(), is(expectedCursorPosition));
	}
	
	@Test public void convertsSelectedTextIntoUpperCase() {
		initialTextWithSelection("abto3Dode", 2, 7);
		Texts.toUpperCase(driver);
		assertResultedTextIs("abTO3DOde");
	}
	@Test public void convertsSelectedTextIntoLowerCase() {
		initialTextWithSelection("ABTo3DODE", 2, 7);
		Texts.toLowerCase(driver);
		assertResultedTextIs("ABto3doDE");
	}

	@Test public void identifiesRowNumber() {
		initialTextWithCursorAt("0\n2\n4", 4);
		assertThat(Texts.currentRow(driver), is(3));
		initialTextWithCursorAt("0\n2\n45", 5);
		assertThat(Texts.currentRow(driver), is(3));
	}
	

	@Test public void removesPreviousBracketHighlights() throws BadLocationException {
		initialTextWithCursorAt("s", 0);
		testArea.getHighlighter().addHighlight(0, 1, Texts.MATCHING_BRACKET_PAINTER);
		Texts.highlightMatchingBrackets(driver);
		assertThat(testArea.getHighlighter().getHighlights().length, is(0));
	}

	@Test public void highlightsMatchingBrackets() {
		Object[][] testCases = new Object[][] {
				{"",       0, false},
				{"(s",     0, false},
				{"()",     0, true,  0, 1},
				{"())",    0, true,  0, 1},
				{"(s)",    0, true,  0, 2},
				{"s(s)",   0, false},
				{"(s)",    1, true,  0, 2},
				{"((s)",   1, true,  1, 3},
				{"[s]",    0, true,  0, 2},
				{"{s}",    0, true,  0, 2},
				{"[s)",    0, false},
				{"(s)",    2, true,  2, 0},
				{")s(",    2, false},
				{"([)",    0, false},
				{"(][)",   0, false},
				{"(][)",   3, false},
				{"([]{})", 0, true, 0, 5},
				{"([{}])", 0, true, 0, 5},
				{"([{}])", 5, true, 5, 0},
		};
		for (Object[] testCase : testCases) {
			String text = (String) testCase[0];
			int cursorPosition = parseInt(testCase[1].toString());
			boolean shouldHighlight = parseBoolean(testCase[2].toString());
			initialTextWithCursorAt(text, cursorPosition);

			Texts.highlightMatchingBrackets(driver);

			String testCaseDescription = text + " with cursor at " + cursorPosition;
			if (shouldHighlight) {
				assertThat(testCaseDescription + " should produce highlights", testArea.getHighlighter().getHighlights().length, is(2));
				assertHighlightIsAtPosition(0, parseInt(testCase[3].toString()), testCaseDescription + " highlight start position is wrong");
				assertHighlightIsAtPosition(1, parseInt(testCase[4].toString()), testCaseDescription + " highlight end position is wrong");
			} else {
				assertThat(testCaseDescription + " should not produce highlights", testArea.getHighlighter().getHighlights().length, is(0));
			}
		}
	}
	@Test public void indicatesWhetherSelectionContainsMultipleRows() {
		Object[][] testCases = new Object[][] {
				{"",        0, 0, false},
				{"a",       0, 1, false},
				{"\n",      0, 1, true},
				{"s\na",    1, 2, true},
				{"s\na\nb", 0, 4, true},
				{"s\n\n",   0, 3, true},
		};
		for (Object[] testCase : testCases) {
			String text = (String) testCase[0];
			int selectionStart = parseInt(testCase[1].toString());
			int selectionEnd = parseInt(testCase[2].toString());
			boolean shouldContainMultipleRows = parseBoolean(testCase[3].toString());
			initialTextWithSelection(text, selectionStart, selectionEnd);
			
			Texts.highlightMatchingBrackets(driver);
			assertThat(text + " with selection at " + selectionStart + " and " + selectionEnd + " failed assertion", Texts.selectionContainsMultipleRows(driver), is(shouldContainMultipleRows));
		}
	}
}
