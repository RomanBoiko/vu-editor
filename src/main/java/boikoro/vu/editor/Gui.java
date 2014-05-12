package boikoro.vu.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;


public class Gui {
	private static final Dimension ZERO_DIMENSION = new Dimension(0, 0);
	private static final String VU_ICON_IMAGE = "vu-icon.png";
	private static final Border EMPTY_BORDER = javax.swing.BorderFactory.createEmptyBorder();
	private JFrame mainFrame;
	private JTextField statusBar;
	private JTextArea editorArea;
	private JTextArea lineNumbers;
	private JScrollPane scrollPane;
	
	public Gui() throws IOException {
		createMainFrame();
		initStatusBar();
		initEditorArea();
		initLineNumbersArea();
		initEditorAndLineNumbersPanel(editorArea, lineNumbers);
		mainFrame.getContentPane().add(scrollPane);
		mainFrame.getContentPane().add(statusBar, BorderLayout.PAGE_END);
		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowOpened( WindowEvent e){
				editorArea.requestFocus();
			}
		});
	}

	public String getCurrentText() {
		return editorArea.getText();
	}
	
	public void setCurrentText(String text) {
		editorArea.setText(text);
	}
	
	public void setLineNumbers(String lineNumbers) {
		this.lineNumbers.setText(lineNumbers);
	}
	
	public void setMainFrameTitle(String resourceUnderEdit){
		mainFrame.setTitle("vu" + (null == resourceUnderEdit? "":"-"+resourceUnderEdit));
	}

	public void setStatusBarText(String text) {
		statusBar.setText(text);
	}

	private void createMainFrame() throws IOException {
		JFrame mainFrame = new JFrame();
		mainFrame.setIconImage(ImageIO.read(this.getClass().getClassLoader().getResourceAsStream(VU_ICON_IMAGE)));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		mainFrame.setBounds(0, 0, screenSize.width, screenSize.height);
		setMainFrameTitle(null);
		mainFrame.setUndecorated(true);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return mainFrame;
	}

	private void initStatusBar() {
		statusBar = new JTextField();
		setStatusBarText("status bar");
		setTextAreaColors(statusBar);
		statusBar.setBorder(EMPTY_BORDER);
	}

	private void initEditorAndLineNumbersPanel(JTextArea editorArea, JTextArea lineNumbers) {
		scrollPane = new JScrollPane(editorArea);
		scrollPane.setRowHeaderView(lineNumbers);
		scrollPane.getVerticalScrollBar().setPreferredSize(ZERO_DIMENSION);
		scrollPane.getHorizontalScrollBar().setPreferredSize(ZERO_DIMENSION);
		scrollPane.setBorder(EMPTY_BORDER);
	}


	private void initEditorArea() {
		editorArea = new JTextArea();
		editorArea.setSelectionColor(Color.GRAY);
		editorArea.setEditable(true);
		setTextAreaColors(editorArea);
		editorArea.setTabSize(4);
	}
	
	public void setKeyListener(KeyListener keyListener) {
		editorArea.addKeyListener(keyListener);
	}

	private void initLineNumbersArea() {
		lineNumbers = new JTextArea(100, 3);
		lineNumbers.setEditable(false);
		lineNumbers.setFocusable(false);
		lineNumbers.setBackground(Color.BLACK);
		lineNumbers.setForeground(Color.GRAY);
	}

	private void setTextAreaColors(JTextComponent textArea) {
		textArea.setBackground(Color.BLACK);
		textArea.setForeground(Color.WHITE);
		textArea.setCaretColor(Color.WHITE);
	}

	public void show() {
		mainFrame.setVisible(true);
	}
}