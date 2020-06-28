package kafka.chatting.client;

import kafka.chatting.client.network.Client;
import kafka.chatting.client.ui.chatroom_list.ChatRoomListFrame;

import java.util.concurrent.CompletableFuture;

public class ChattingClientApplication {
    public ChattingClientApplication(String host, String port) {
        Client client = new Client(host, port);
        ClientInstance.getInstance().setClient(client);
        CompletableFuture.runAsync(client::run);
        ChatRoomListFrame chatRoomListFrame = new ChatRoomListFrame();
        System.out.println("ChattingClient is running.");
        chatRoomListFrame.setVisible(true);
    }
}