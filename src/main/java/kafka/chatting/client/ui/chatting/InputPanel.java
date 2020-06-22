package kafka.chatting.client.ui.chatting;

import kafka.chatting.client.ClientInstance;
import kafka.chatting.utility.MessageFactory;
import kafka.chatting.model.Message;
import kafka.chatting.model.User;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class InputPanel extends JPanel {
    private static final String LEAVE_CHAT_ROOM = "!quit";
    private final Integer chatRoomNo;
    private final JTextField textField = new JTextField("");

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
                    final String text = textField.getText().trim();
                    textField.setText("");
                    sendText(text);
                }
            }
        });
        this.add(textField);
    }

    private void sendText(String text) {
        if (text.length() > 0) {
            final User user = ClientInstance.getInstance().getUser();
            final Message message;

            if (LEAVE_CHAT_ROOM.equals(text)) {
                message = MessageFactory.userLeaveClientMessage(user, chatRoomNo);
            } else {
                message = MessageFactory.normalClientMessage(user, chatRoomNo, text);
            }
            ClientInstance.getInstance().send(message);
        }
    }
}
