package kafka.chatting.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

public class ChattingPanel extends JPanel implements Observer<String> {
    private JTextPane textPane = new JTextPane();

    private Style style = textPane.addStyle("Color Style", null);

    public ChattingPanel(LayoutManager layout) {
        super(layout);
        addComponent();
        addText("USER#123 님이 채팅에 참여하셨습니다.");
    }

    private void addComponent() {
        textPane.setEditable(false);
        textPane.setAutoscrolls(true);
        textPane.setBackground(new Color(178, 199, 217));

        this.add(textPane);
    }

    private void addText(String text) {
        try {
            StyleConstants.setForeground(style, Color.BLACK);
            StyleConstants.setBold(style, true);
//            StyleConstants.setFontSize(style, 8);
            textPane.getDocument().insertString(textPane.getDocument().getLength(), text + "\n", style);
        } catch (BadLocationException exception) {
            exception.printStackTrace();
        }
    }

    public void update(String data) {
        addText(data);
    }
}
