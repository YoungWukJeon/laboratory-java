package kafka.chatting.ui.chatroom_list;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class ChatRoomListFrame extends JFrame {
    private final JPanel chatRoomListPanel = new ChatRoomListPanel(new BorderLayout());
    private final JButton createRoomButton = new JButton("Create Chat Room");

    public ChatRoomListFrame() {
        init();
        addComponent();
    }

    private void init() {
        this.setTitle("Active Chat Room List");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createRoomButton.setFocusable(false);
        createRoomButton.addActionListener((event) -> {
            System.out.println("채팅 방 생성");
        });

        this.setSize(500, 500);
        this.setResizable(false);
        this.setLocationRelativeTo(getParent());
        this.setVisible(true);
    }

    private void addComponent() {
        this.add(chatRoomListPanel);
        this.add(createRoomButton, BorderLayout.SOUTH);
    }
}
