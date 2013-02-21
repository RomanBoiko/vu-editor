package boikoro.vu.editor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;


public class Widget extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextArea textArea = new JTextArea(5, 20);
    private JScrollPane scrollPane = new JScrollPane(textArea);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Widget ex = new Widget();
                ex.setVisible(true);
            }
        });
    }

    public Widget() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(0, 0, screenSize.width, screenSize.height);

        setTitle("vu");
        setLocationRelativeTo(null);
        setUndecorated(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);


        textArea.setSelectionColor(Color.GRAY);
        textArea.setEditable(true);
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.WHITE);
        textArea.setCaretColor(Color.WHITE);
        add(scrollPane);

        textArea.setText(createTextForArea());
        scrollPane.setRowHeaderView(textAreaWithRowsNumber());
        scrollPane.getVerticalScrollBar().setPreferredSize (new Dimension(0,0));
//        addWindowListener(new WindowAdapter() {
//            public void windowOpened( WindowEvent e){
//                textArea.requestFocus();
//            }
//        });
    }


    private JTextArea textAreaWithRowsNumber() {
        JTextArea lines = new JTextArea(100, 2);
        String text = "";
        for (int i = 1; i < 101; i++) {
            text += ""+i+"\n";
        }
        lines.setText(text);
        lines.setEditable(false);
        lines.setFocusable(false);
        return lines;
    }

    private String createTextForArea() {
        String text = "";
        for (int i = 0; i < 100; i++) {
            text += "asd"+i+"\n";
        }
        return text;
    }
}
