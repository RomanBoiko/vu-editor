package vu.editor;

abstract class Find {
	private static final int INITIAL_FOUND_POSITION = -1;
	private final String findMessage;
	private final Driver driver;
	private String previousFindText = "";
	private int previousFindPosition = INITIAL_FOUND_POSITION;
	public Find(Driver driver, String findMessage) {
		this.driver = driver;
		this.findMessage = findMessage;
	}
	protected final String text() {
		return driver.text();
	}
	void resetLastGoundPosition() {
		previousFindPosition = INITIAL_FOUND_POSITION;
	}

	void find() {
		String textToFind = driver.statusBar().getText();
		if (textToFind.startsWith(findMessage)) {
			textToFind = textToFind.substring(findMessage.length());
		}
		find(textToFind);
	}
	
	private void find(String textToFind) {
		if (textToFind.length() == 0) {
			return;
		}
		if (!previousFindText.equals(textToFind)) {
			previousFindText = textToFind;
			previousFindPosition = INITIAL_FOUND_POSITION;
		}
		Texts.removeAllFindHighlights(driver);
		int newPosition = findAfter(textToFind, previousFindPosition + 1);
		if (newPosition >= 0 ) {
			previousFindPosition = newPosition;
			highlight(textToFind, newPosition);
		} else if (previousFindPosition >= 0) {//search wrap
			int firstOccurence = findAfter(textToFind, 0);
			previousFindPosition = firstOccurence;
			highlight(textToFind, firstOccurence);
		}
	}

	private void highlight(String textToFind, int firstOccurence) {
		Texts.highlightFoundText(driver, firstOccurence, numberOfCharsToHighlight(textToFind));
	}
	protected int numberOfCharsToHighlight(String textToFind) {
		return textToFind.length();
	}
	
	protected abstract int findAfter(String textToFind, int startPosition);

	int previousFindTextWithoutPrefixLength() {
		return previousFindText.length();
	}
	String previousFindText() {
		return previousFindText;
	}

	String initialFindMessage() {
		return findMessage + previousFindText();
	}

	int previousFindPosition() {
		return previousFindPosition;
	}
}

class FindCaseSensitive extends Find {
	FindCaseSensitive(Driver driver) {
		super(driver, "FIND, CASE-SENSITIVE =>");
	}
	@Override protected int findAfter(String textToFind, int startPosition) {
		return text().indexOf(textToFind, startPosition);
	}
}
class FindCaseInsensitive extends Find {
	FindCaseInsensitive(Driver driver) {
		super(driver, "FIND, CASE-INSENSITIVE =>");
	}
	@Override protected int findAfter(String textToFind, int startPosition) {
		return text().toLowerCase().indexOf(textToFind.toLowerCase(), startPosition);
	}
}
class FindByRegexp extends Find {
	FindByRegexp(Driver driver) {
		super(driver, "FIND, BY REGULAR EXPRESSION =>");
	}
	@Override protected int findAfter(String textToFind, int startPosition) {
		String[] partsSplitByRegexp = text().substring(startPosition).split(textToFind);
		return (partsSplitByRegexp.length > 1
				? (startPosition + partsSplitByRegexp[0].length())
				: -1);
	}
	@Override protected int numberOfCharsToHighlight(String textToFind) {
		return 1;
	}
	@Override int previousFindTextWithoutPrefixLength() {
		return 1;
	}
}