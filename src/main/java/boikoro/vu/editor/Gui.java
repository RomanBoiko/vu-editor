package boikoro.vu.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
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
	
	private EditableFile resourceUnderEdit;

	private Gui() throws IOException {
		initMainFrame();
		initStatusBar();
		initEditorArea(createTextForEditor());
		initLineNumbersArea(linesNumbersText());
		initEditorAndLineNumbersPanel(editorArea, lineNumbers);
		mainFrame.getContentPane().add(scrollPane);
		mainFrame.getContentPane().add(statusBar, BorderLayout.PAGE_END);
		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowOpened( WindowEvent e){
				editorArea.requestFocus();
			}
		});
	}

	public void saveCurrentText() throws IOException {
		resourceUnderEdit.saveText(editorArea.getText());
	}
	
	private void setMainFrameTitle(String resourceUnderEdit){
		mainFrame.setTitle("vu" + (null == resourceUnderEdit? "":"-"+resourceUnderEdit));
	}

	private void setStatusBarText(String text) {
		statusBar.setText(text);
	}

	private void initMainFrame() throws IOException {
		mainFrame = new JFrame();
		mainFrame.setIconImage(ImageIO.read(this.getClass().getClassLoader().getResourceAsStream(VU_ICON_IMAGE)));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		mainFrame.setBounds(0, 0, screenSize.width, screenSize.height);
		setMainFrameTitle(null);
		mainFrame.setUndecorated(true);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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


	private void initEditorArea(String text) {
		editorArea = new JTextArea();
		editorArea.setSelectionColor(Color.GRAY);
		editorArea.setEditable(true);
		setTextAreaColors(editorArea);
		editorArea.setText(text);
		editorArea.setTabSize(4);
		editorArea.addKeyListener(new KeyboardListener(this));
	}

	private void initLineNumbersArea(String text) {
		lineNumbers = new JTextArea(100, 3);
		lineNumbers.setText(text);
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

	private void loadResource(EditableFile resource) throws IOException {
		editorArea.setText(resource.getText());
		setMainFrameTitle(resource.getFileName());
		setStatusBarText(resource.getPath());
		resourceUnderEdit = resource;
	}

	private void show() {
		mainFrame.setVisible(true);
	}

	public static void startNewGui() throws IOException {
		new Gui().show();
	}

	
	public static void startNewGui(EditableFile resource) throws IOException {
		Gui gui = new Gui();
		gui.loadResource(resource);
		gui.show();
	}

}