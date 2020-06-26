package kafka.chatting.client.ui.chatroom_list;

import kafka.chatting.client.ClientInstance;
import kafka.chatting.utility.MessageFactory;

import java.awt.*;
import javax.swing.*;

public class ChatRoomListFrame extends JFrame {
    private final ChatRoomListPanel chatRoomListPanel = new ChatRoomListPanel(new BorderLayout());
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
            ClientInstance.getInstance().send(MessageFactory.createChatRoomClientMessage());
        });

        this.setSize(500, 500);
        this.setResizable(false);
        this.setLocationRelativeTo(getParent());
    }

    private void addComponent() {
        this.add(chatRoomListPanel);
        this.add(createRoomButton, BorderLayout.SOUTH);
    }
}
