package kafka.chatting.ui.chatting;

import kafka.chatting.model.CommandType;
import kafka.chatting.network.Client;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class InputPanel extends JPanel {
    private final JTextField textField = new JTextField("");
    private final Integer chatRoomNo;

    public InputPanel(BorderLayout layout, Integer chatRoomNo) {
        super(layout);
        this.chatRoomNo = chatRoomNo;
        addComponent();
    }

    private void addComponent() {
        textField.setFont(new Font("", Font.PLAIN, 16));
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (textField.getText().trim().length() > 0) {
                        Client.getInstance().send(CommandType.NORMAL, chatRoomNo, textField.getText().trim());
                        textField.setText("");
                    }
                }
            }
        });

        this.add(textField);
    }
}
