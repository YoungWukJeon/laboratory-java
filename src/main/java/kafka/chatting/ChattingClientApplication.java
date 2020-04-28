package kafka.chatting;

import kafka.chatting.network.Client;
import kafka.chatting.ui.ChattingFrame;

// TODO: 2020-04-28 TODO: 2020-04-28 외부에서 클라이언트의 포트를 설정할 수 있게 변경해야 함. build.gradle에서 이 클래스만 실행할 수 있는 jar 생성
public class ChattingClientApplication {
    public static void main(String[] args) {
        new Thread(Client.getInstance()).start();
        ChattingFrame cf = new ChattingFrame();
        System.out.println("ChattingClient Running.");
    }
}