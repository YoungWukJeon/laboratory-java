package kafka.chatting.ui.chatroom_list;

import kafka.chatting.model.Message;
import kafka.chatting.network.Client;

import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import javax.swing.*;
import javax.swing.event.*;

public class ChatRoomListFrame extends JFrame implements Subscriber<Message> {
    private final ChatRoomListPanel chatRoomListPanel = new ChatRoomListPanel(new BorderLayout());
    private final JButton createRoomButton = new JButton("Create Chat Room");
    private Subscription subscription;

    public ChatRoomListFrame() {
        init();
        addComponent();
        Client.getInstance().subscribe(this);
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
    }

    private void addComponent() {
        this.add(chatRoomListPanel);
        this.add(createRoomButton, BorderLayout.SOUTH);
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1L);
    }

    @Override
    public void onNext(Message message) {
        System.out.println("onNext(ChatRoomListFrame) -> " + message);
        subscription.request(1L);
    }

    @Override
    public void onError(Throwable throwable) {
        System.err.println(throwable.getMessage());
        subscription.cancel();
    }

    @Override
    public void onComplete() {
        System.out.println("Done!(ChatRoomListFrame)");
        subscription.cancel();
    }
}
