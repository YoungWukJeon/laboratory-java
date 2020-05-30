package kafka.chatting;

import kafka.chatting.network.Client;
import kafka.chatting.ui.chatroom_list.ChatRoomListFrame;

import java.util.concurrent.CompletableFuture;

// TODO: 2020-04-28 TODO: 2020-04-28 외부에서 클라이언트의 포트를 설정할 수 있게 변경해야 함. build.gradle에서 이 클래스만 실행할 수 있는 jar 생성
public class ChattingClientApplication {
    public static void main(String[] args) {
        CompletableFuture.runAsync(Client.getInstance()::run);
        ChatRoomListFrame chatRoomListFrame = new ChatRoomListFrame();
        System.out.println("ChattingClient is running.");
        chatRoomListFrame.setVisible(true);
    }
}