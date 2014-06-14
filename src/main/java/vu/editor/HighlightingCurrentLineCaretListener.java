package vu.editor;

import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class HighlightingCurrentLineCaretListener implements CaretListener {

	private final Driver driver;

	HighlightingCurrentLineCaretListener(Driver driver) {
		this.driver = driver;
	}

	@Override
	public void caretUpdate(CaretEvent e) {
		driver.highlightCurrentLine();
	}

}
