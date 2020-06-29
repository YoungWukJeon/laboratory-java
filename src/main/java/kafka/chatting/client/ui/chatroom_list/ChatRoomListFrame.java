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
            ClientInstance.getInstance().send(MessageFactory.createChatRoomClientMessage(ClientInstance.getInstance().getUser()));
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
