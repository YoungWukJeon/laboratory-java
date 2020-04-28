package kafka.chatting;

import kafka.chatting.network.Server;

// TODO: 2020-04-28 TODO: 2020-04-28 외부에서 서버의 실행 포트를 설정할 수 있게 변경해야 함.
//  build.gradle에서 이 클래스만 실행할 수 있는 jar 생성
//  이후에 kafka의 포트도 외부에서 변경할 수 있게 해야함
public class ChattingServerApplication {
    public static void main(String[] args) {
        System.out.println("ChattingServer Running.");
        Server server = new Server();
        server.run();
    }
}