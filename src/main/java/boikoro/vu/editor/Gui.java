package boikoro.vu.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

public class Gui extends JFrame {
	private static final long serialVersionUID = 1L;

	public Gui() {
		initMainFrame();
		final JTextArea editorArea = editorArea(createTextForEditor());
		JTextArea lineNumbers = lineNumbersArea(linesNumbersText());
		JScrollPane scrollPane = editorAndLineNumbersPanel(editorArea, lineNumbers);
		JTextField statusBar = statusBar();
		getContentPane().add(scrollPane);
		getContentPane().add(statusBar, BorderLayout.PAGE_END);
		addWindowListener(new WindowAdapter() {
			public void windowOpened( WindowEvent e){
				editorArea.requestFocus();
			}
		});
	}

	private void initMainFrame() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(0, 0, screenSize.width, screenSize.height);
		setTitle("vu");
		setUndecorated(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private JScrollPane editorAndLineNumbersPanel(JTextArea editorArea, JTextArea lineNumbers) {
		JScrollPane scrollPane = new JScrollPane(editorArea);
		scrollPane.setRowHeaderView(lineNumbers);
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		return scrollPane;
	}

	private JTextField statusBar() {
		JTextField statusBar = new JTextField();
		statusBar.setText("status bar");
		setTextAreaColors(statusBar);
		return statusBar;
	}

	private JTextArea editorArea(String text) {
		JTextArea textArea = new JTextArea();
		textArea.setSelectionColor(Color.GRAY);
		textArea.setEditable(true);
		setTextAreaColors(textArea);
		textArea.setText(text);
		return textArea;
	}

	private void setTextAreaColors(JTextComponent textArea) {
		textArea.setBackground(Color.BLACK);
		textArea.setForeground(Color.WHITE);
		textArea.setCaretColor(Color.WHITE);
	}


	private JTextArea lineNumbersArea(String text) {
		JTextArea linesNumbers = new JTextArea(100, 3);
		linesNumbers.setText(text);
		linesNumbers.setEditable(false);
		linesNumbers.setFocusable(false);
		linesNumbers.setBackground(Color.GRAY);
		linesNumbers.setForeground(Color.BLACK);
		return linesNumbers;
	}

	private String createTextForEditor() {
		String text = "";
		for (int i = 0; i < 100; i++) {
			text += "asd" + i + "\n";
		}
		return text;
	}

	private String linesNumbersText() {
		String text = "";
		for (int i = 1; i < 101; i++) {
			text += "" + i + "\n";
		}
		return text;
	}
}