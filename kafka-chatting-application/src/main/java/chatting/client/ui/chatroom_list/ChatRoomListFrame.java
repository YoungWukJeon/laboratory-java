package chatting.client.ui.chatroom_list;

import chatting.client.ClientInstance;
import chatting.utility.MessageFactory;

import java.awt.*;
import javax.swing.*;

public class ChatRoomListFrame extends JFrame {
    private final ChatRoomListPanel chatRoomListPanel = new ChatRoomListPanel(new BorderLayout());
    private final JPanel buttonPanel = new JPanel(new BorderLayout());
    private final JButton createChatRoomButton = new JButton("Create Chat Room");
    private final JButton reloadChatRoomListButton = new JButton("Reload Chat Room List");

    public ChatRoomListFrame() {
        init();
        addComponent();
    }

    private void init() {
        this.setTitle("Active Chat Room List");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createChatRoomButton.setFocusable(false);
        createChatRoomButton.addActionListener((event) ->
                ClientInstance.getInstance().send(
                        MessageFactory.createChatRoomClientMessage(ClientInstance.getInstance().getUser())));

        reloadChatRoomListButton.setFocusable(false);
        reloadChatRoomListButton.addActionListener((event) ->
            ClientInstance.getInstance().send(
                    MessageFactory.chatRoomListClientMessage(ClientInstance.getInstance().getUser())));

        buttonPanel.add(createChatRoomButton, BorderLayout.WEST);
        buttonPanel.add(reloadChatRoomListButton, BorderLayout.EAST);

        this.setSize(500, 500);
        this.setResizable(false);
        this.setLocationRelativeTo(getParent());
    }

    private void addComponent() {
        this.add(chatRoomListPanel);
        this.add(buttonPanel, BorderLayout.SOUTH);
    }
}
