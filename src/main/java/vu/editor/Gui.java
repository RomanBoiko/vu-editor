package vu.editor;

import static java.lang.String.format;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
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

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Gui gui = new Gui();
				Driver driver = new Driver(gui);
				KeyListener keyListener = new KeyboardListener(driver);
				gui.setKeyListener(keyListener);
				gui.show();
			}
		});
	}

	public Gui() {
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

	private void createMainFrame() {
		JFrame mainFrame = new JFrame();
		try {
			mainFrame.setIconImage(ImageIO.read(this.getClass().getClassLoader().getResourceAsStream(VU_ICON_IMAGE)));
		} catch (IOException e) {
			throw new RuntimeException();
		}
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

class KeyboardListener implements KeyListener{

	private final Driver driver;
	
	private Set<Integer> pushedKeys = new HashSet<Integer>();

	public KeyboardListener(Driver driver) {
		this.driver = driver;
	}

	@Override
	public void keyPressed(KeyEvent pressedKeyEvent) {
		pushedKeys.add(pressedKeyEvent.getKeyCode());
		System.out.println(
				format("=>key pressed, char='%s', code='%d')",
						pressedKeyEvent.getKeyChar(),
						pressedKeyEvent.getKeyCode()));
		System.out.println(
				format("=>active keys: %s", pushedKeys.toString()));
		if(pushedKeys.contains(17) && pushedKeys.contains(83)) {
			driver.save();
		}
	}

	@Override
	public void keyReleased(KeyEvent releasedKeyEvent) {
		pushedKeys.remove(releasedKeyEvent.getKeyCode());
	}

	@Override
	public void keyTyped(KeyEvent typedKeyEvent) {
		// TODO Auto-generated method stub
		
	}
}

class Driver {
	private final Gui gui;

	public Driver(Gui gui) {
		this.gui = gui;
		this.gui.setCurrentText(createTextForEditor());
		this.gui.setLineNumbers(linesNumbersText());
	}

	public void save() {
		try {
			resourceUnderEdit.saveText(gui.getCurrentText());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private String linesNumbersText() {
		String text = "";
		for (int i = 1; i < 101; i++) {
			text += "" + i + "\n";
		}
		return text;
	}
	
	private String createTextForEditor() {
		String text = "";
		for (int i = 0; i < 100; i++) {
			text += "asd" + i + "\n";
		}
		return text;
	}
	
	private EditableFile resourceUnderEdit;
	
	public void loadResource(EditableFile resource) {
		gui.setCurrentText(resource.getText());
		gui.setMainFrameTitle(resource.getFileName());
		gui.setStatusBarText(resource.getPath());
		resourceUnderEdit = resource;
	}
}
