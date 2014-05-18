package vu.editor;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import javax.swing.JTextArea;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

public class TextActionsTest {

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
	
	void initialText(String text) {
		testArea.setText(text);
	}
	void initialTextWithCursorAt(String text, int cursorPosition) {
		initialText(text);
		testArea.setCaretPosition(cursorPosition);
	}
	void initialTextWithSelection(String text, int startPosition, int endPosition) {
		initialText(text);
		testArea.setSelectionStart(startPosition);
		testArea.setSelectionEnd(endPosition);
	}
	
	@Test public void formatsXml() {
		Assert.assertEquals("<a xmlns:a=\"http\">\n<b>c</b>\n</a>\n", TextActions.formatXml("<a xmlns:a=\"http\"><b>c</b></a>"));
		Assert.assertEquals("<?XML?>\n<a/>\n", TextActions.formatXml("<?XML?><a/>"));
	}

	
	//0 1  2 3  4 5  6 7  8 9
	// 1 \n 2 \n 3 \n 4 \n 5
	private final String testText = "1\n2\n3\n4\n5";
	private void assertResultedTextIs(String expected) {
		assertThat(testArea.getText(), is(expected));
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

//	@Test public void movesLineUp() {
//		initialTextWithSelection(testText, 2, 2);
//		TextActions.moveLinesUp(driver);
//		assertResultedTextIs("2\n1\n3\n4\n5");
//	}
}
