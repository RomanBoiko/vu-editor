package vu.editor;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import javax.swing.JTextArea;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class TextActionsTest {

	private final String testText = "1\n2\n3\n4\n5";
	private final JTextArea testArea = new JTextArea();
	private final Driver driver = new Driver() {
		protected JTextArea inputArea() {
			return testArea;
		}
	};
	
	@After
	public void after() {
		initialTextWithSelection("", 0, 0);
		initialTextWithCursorAt("", 0);
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
	@Test public void movesLastLineWithEndOfLineDown() {
		initialTextWithSelection(testText + "\n", 9, 9);
		TextActions.moveLinesDown(driver);
		assertResultedTextIs(testText + "\n");
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
}
