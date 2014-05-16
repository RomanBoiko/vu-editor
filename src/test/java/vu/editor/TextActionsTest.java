package vu.editor;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TextActionsTest {

	@Mock Driver driver;
	
	void initialText(String text) {
		when(driver.text()).thenReturn(text);
	}
	void initialTextWithCursorAt(String text, int cursorPosition) {
		initialText(text);
		when(driver.cursor()).thenReturn(cursorPosition);
	}
	
	@Test public void formatsXml() {
		Assert.assertEquals("<a xmlns:a=\"http\">\n<b>c</b>\n</a>\n", TextActions.formatXml("<a xmlns:a=\"http\"><b>c</b></a>"));
		Assert.assertEquals("<?XML?>\n<a/>\n", TextActions.formatXml("<?XML?><a/>"));
	}

	@Test public void deleteFirstLine() {
		initialTextWithCursorAt("ab\ncd\nef", 1);
		TextActions.deleteLine(driver);
		verify(driver).replaceRange("", 0, 3);
	}
	@Test public void deleteLineFromTheMiddle() {
		initialTextWithCursorAt("ab\ncd\nef", 3);
		TextActions.deleteLine(driver);
		verify(driver).replaceRange("", 3, 6);
	}
	@Test public void deleteLastLineWithoutEndOfFile() {
		initialTextWithCursorAt("ab\ncd\nef", 7);
		TextActions.deleteLine(driver);
		verify(driver).replaceRange("", 6, 8);
	}
	@Test public void deleteLastLineBeforeWithEndOfFile() {
		initialTextWithCursorAt("ab\ncd\nef\n", 7);
		TextActions.deleteLine(driver);
		verify(driver).replaceRange("", 6, 9);
	}
	@Test public void deleteNothingIfNoText() {
		initialTextWithCursorAt("", 0);
		TextActions.deleteLine(driver);
		verify(driver).replaceRange("", 0, 0);
	}
}
