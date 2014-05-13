package vu.editor;

import org.junit.Assert;
import org.junit.Test;

public class FormattersTest {

	@Test public void formatsXml() {
		Assert.assertEquals("<a xmlns:a=\"http\">\n<b>c</b>\n</a>\n", Formatters.formatXml("<a xmlns:a=\"http\"><b>c</b></a>"));
		Assert.assertEquals("<?XML?>\n<a/>\n", Formatters.formatXml("<?XML?><a/>"));
	}
}
