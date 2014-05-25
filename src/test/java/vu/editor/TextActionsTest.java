package vu.editor;

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
public class TextActionsTest {

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
		Assert.assertEquals("<a xmlns:a=\"http\">\n<b>c</b>\n</a>\n", TextActions.formatXml("<a xmlns:a=\"http\"><b>c</b></a>"));
		Assert.assertEquals("<?XML?>\n<a/>\n", TextActions.formatXml("<?XML?><a/>"));
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
		TextActions.deleteLine(driver);
		assertResultedTextIs("2\n3\n4\n5");
	}
	@Test public void deletesLineFromTheMiddle() {
		initialTextWithCursorAt(testText, 2);
		TextActions.deleteLine(driver);
		assertResultedTextIs("1\n3\n4\n5");
	}
	@Test public void deletesLastLineWithoutEndOfFile() {
		initialTextWithCursorAt(testText, 8);
		TextActions.deleteLine(driver);
		assertResultedTextIs("1\n2\n3\n4\n");
	}
	@Test public void deletesLastLineBeforeWithEndOfFile() {
		initialTextWithCursorAt(testText + "\n", 9);
		TextActions.deleteLine(driver);
		assertResultedTextIs("1\n2\n3\n4\n");
	}
	@Test public void deletesNothingIfNoText() {
		initialTextWithCursorAt("", 0);
		TextActions.deleteLine(driver);
		assertResultedTextIs("");
	}

	//testText:
	//positions:  0 1  2 3  4 5  6 7  8 9
	//characters:  1 \n 2 \n 3 \n 4 \n 5
	@Test public void doesNotMoveFirstLineUp() {
		initialTextWithSelection(testText, 1, 1);
		TextActions.moveLinesUp(driver);
		assertResultedTextIs(testText);
	}
	@Test public void movesSecondLineUp() {
		initialTextWithSelection(testText, 2, 2);
		TextActions.moveLinesUp(driver);
		assertResultedTextIs("2\n1\n3\n4\n5");
	}
	@Test public void movesThirdLineUp() {
		initialTextWithSelection(testText, 5, 5);
		TextActions.moveLinesUp(driver);
		assertResultedTextIs("1\n3\n2\n4\n5");
	}
	@Test public void movesLastLineUp() {
		initialTextWithSelection(testText, 9, 9);
		TextActions.moveLinesUp(driver);
		assertResultedTextIs("1\n2\n3\n5\n4\n");
	}
	@Test public void movesLastLineWithLineEndUp() {
		initialTextWithSelection(testText + "\n", 9, 9);
		TextActions.moveLinesUp(driver);
		assertResultedTextIs("1\n2\n3\n5\n4\n");
	}
	@Test public void movesMultipleLinesUp() {
		initialTextWithSelection(testText, 4, 6);
		TextActions.moveLinesUp(driver);
		assertResultedTextIs("1\n3\n4\n2\n5");
	}
	@Test public void putsCursorIntoStartOfMovedUpLine() {
		initialTextWithSelection(testText, 5, 5);
		TextActions.moveLinesUp(driver);
		assertCursorPsotionIs(2);
	}

	//testText:
	//positions:  0 1  2 3  4 5  6 7  8 9
	//characters:  1 \n 2 \n 3 \n 4 \n 5
	@Test public void doesNotMoveLastLineWithEndOfLineDown() {
		initialTextWithSelection(testText + "\n", 9, 9);
		TextActions.moveLinesDown(driver);
		assertResultedTextIs(testText + "\n");
	}
	@Test public void doesMoveLastLineWithFewEndsOfLineDown() {
		initialTextWithSelection(testText + "5\n\n", 9, 9);
		TextActions.moveLinesDown(driver);
		assertResultedTextIs("1\n2\n3\n4\n\n55\n");
	}
	@Test public void doesNotMoveLastLineWithoutEndOfLineDown() {
		initialTextWithSelection(testText, 8, 8);
		TextActions.moveLinesDown(driver);
		assertResultedTextIs(testText);
	}
	@Test public void doesNotMoveLastEndOfLineDown() {
		initialTextWithSelection(testText + "\n\n", 11, 11);
		TextActions.moveLinesDown(driver);
		assertResultedTextIs(testText + "\n\n");
	}
	@Test public void movesFirstLineDown() {
		initialTextWithSelection(testText, 0, 0);
		TextActions.moveLinesDown(driver);
		assertResultedTextIs("2\n1\n3\n4\n5");
	}
	@Test public void movesSecondLineDown() {
		initialTextWithSelection(testText, 2, 2);
		TextActions.moveLinesDown(driver);
		assertResultedTextIs("1\n3\n2\n4\n5");
	}
	@Test public void movesoneBeforeLastLineDown() {
		initialTextWithSelection(testText, 6, 6);
		TextActions.moveLinesDown(driver);
		assertResultedTextIs("1\n2\n3\n5\n4");
	}
	@Test public void movesMultipleLinesDown() {
		initialTextWithSelection(testText, 2, 4);
		TextActions.moveLinesDown(driver);
		assertResultedTextIs("1\n4\n2\n3\n5");
	} 
	@Test public void putsCursorIntoStartOfMovedDownLine() {
		initialTextWithSelection(testText, 5, 5);
		TextActions.moveLinesDown(driver);
		assertCursorPsotionIs(6);
	}

	@Test public void removesAllHighlightsIfThereAreAny() throws BadLocationException {
		initialText("aaaaa");
		testArea.getHighlighter().addHighlight(0, 1, null);
		TextActions.showOrHideWhitespacesAndHighlights(driver);
		assertThat(testArea.getHighlighter().getHighlights().length, is(0));
	}
	@Test public void highlightsPrecedingAndTrailingWhitespaces() throws BadLocationException {
		//                      10          20
		//           01        901    56           5678
		initialText("  av asd\n   as\n  \nas \t s\n    ");
		TextActions.showOrHideWhitespacesAndHighlights(driver);
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
	@Test public void highlightsTrailingWhitespaces() throws BadLocationException {
		//             234     8910
		initialText("as  \t\nas  \t");
		TextActions.showOrHideWhitespacesAndHighlights(driver);
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
		TextActions.showOrHideWhitespacesAndHighlights(driver);
		assertThat(testArea.getSelectionStart(), is(1));
		assertThat(testArea.getSelectionEnd(), is(1));
	}

	private void assertHighlightIsAtPosition(int highlightNumber, int startOffset) {
		assertThat(testArea.getHighlighter().getHighlights()[highlightNumber].getStartOffset(), is(startOffset));
		assertThat(testArea.getHighlighter().getHighlights()[highlightNumber].getEndOffset(), is(startOffset+1));
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
		TextActions.joinLines(driver);
		assertResultedTextIs(expectedText);
		assertThat(testArea.getCaretPosition(), is(expectedCursorPosition));
	}
	
	@Test public void convertsSelectedTextIntoUpperCase() {
		initialTextWithSelection("abto3Dode", 2, 7);
		TextActions.toUpperCase(driver);
		assertResultedTextIs("abTO3DOde");
	}
	@Test public void convertsSelectedTextIntoLowerCase() {
		initialTextWithSelection("ABTo3DODE", 2, 7);
		TextActions.toLowerCase(driver);
		assertResultedTextIs("ABto3doDE");
	}
}
