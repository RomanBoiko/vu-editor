package vu.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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


@SuppressWarnings("serial")
public class Gui {
	private static final Dimension ZERO_DIMENSION = new Dimension(0, 0);
	private static final String VU_ICON_IMAGE = "vu-icon.png";
	private static final Border EMPTY_BORDER = javax.swing.BorderFactory.createEmptyBorder();
	private final BottomStatusBar statusBar = new BottomStatusBar();
	private final InputArea editorArea = new InputArea();
	private final MainFrame mainFrame = new MainFrame();
	
	public Gui() throws IOException {
		JScrollPane scrollPane = new EditorScrollPane(editorArea, new LineNumbersArea());
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
	public void setTitle(String resourceUnderEdit){
		mainFrame.setTitle("vu" + (null == resourceUnderEdit? "":"-"+resourceUnderEdit));
	}
	public void setStatusBarText(String text) {
		statusBar.setText(text);
	}
	public void setKeyListener(KeyListener keyListener) {
		editorArea.addKeyListener(keyListener);
	}

	private static class MainFrame extends JFrame {
		public MainFrame() {
			try {
				setIconImage(ImageIO.read(this.getClass().getClassLoader().getResourceAsStream(VU_ICON_IMAGE)));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			setBounds(0, 0, screenSize.width, screenSize.height);
			setTitle(null);
			setUndecorated(true);
			setDefaultCloseOperation(EXIT_ON_CLOSE);
		}
	}

	private static class EditorScrollPane extends JScrollPane{
		EditorScrollPane(JTextArea editorArea, JTextArea lineNumbers) {
			super(editorArea);
			setRowHeaderView(lineNumbers);
			getVerticalScrollBar().setPreferredSize(ZERO_DIMENSION);
			getHorizontalScrollBar().setPreferredSize(ZERO_DIMENSION);
			setBorder(EMPTY_BORDER);
		}
	}

	private static final Font INPUT_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 14);

	private static class InputArea extends JTextArea {
		InputArea() {
			setSelectionColor(Color.GRAY);
			setFont(INPUT_FONT);
			setEditable(true);
			setupColors();
			setTabSize(4);
		}

		private void setupColors() {
			setBackground(Color.BLACK);
			setForeground(Color.WHITE);
			setCaretColor(Color.WHITE);
		}
	}

	private static class BottomStatusBar extends JTextField {
		BottomStatusBar() {
			setupReadOnlyTextElement(this);
			setText("status bar");
			setBorder(EMPTY_BORDER);
		}
	}

	private static class LineNumbersArea extends JTextArea {
		private static final int MAX_BUFFER_SIZE = 9999;
		LineNumbersArea() {
			super(MAX_BUFFER_SIZE, 4);
			setupReadOnlyTextElement(this);
			setFont(INPUT_FONT);
			setText(linesNumbers());
		}

		private static String linesNumbers() {
			StringBuffer text = new StringBuffer();
			for (int i = 1; i <= MAX_BUFFER_SIZE; i++) {
				text.append(i).append('\n');
			}
			return text.toString();
		}
	}

	private static void setupReadOnlyTextElement(JTextComponent textArea) {
		textArea.setBackground(Color.BLACK);
		textArea.setForeground(Color.GRAY);
		textArea.setEditable(false);
		textArea.setFocusable(false);
	}

	public void show() {
		mainFrame.setVisible(true);
	}
}