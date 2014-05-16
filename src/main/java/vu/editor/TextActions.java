package vu.editor;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.Writer;

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
	private static final char LINE_SEPARATOR = '\n';

	static void formatXml(Driver driver) {
		driver.text(formatXml(driver.text()));
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
		int position = driver.cursor();
		String currentText = driver.text();
		int start = currentText.substring(0, position).lastIndexOf('\n') + 1;
		
		int distanceFromCursorToLineEndChar = currentText.substring(position).indexOf(LINE_SEPARATOR);
		int end = distanceFromCursorToLineEndChar < 0 ? currentText.length() : distanceFromCursorToLineEndChar + position + 1;
		driver.replaceRange(EMPTY_STRING, start, end);
	}

}
