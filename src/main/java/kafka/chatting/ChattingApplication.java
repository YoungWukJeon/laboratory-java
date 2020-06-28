package kafka.chatting;

import kafka.chatting.client.ChattingClientApplication;
import kafka.chatting.server.ChattingServerApplication;

import java.util.Objects;

public class ChattingApplication {
    public static void main(String[] args) {
        try {
            String type = null;
            String host = null;
            String port = null;

            for (int i = 0; i < args.length; i += 2) {
                switch (args[i]) {
                    case "-t":
                        type = args[i + 1];
                        break;
                    case "-h":
                        host = args[i + 1];
                        break;
                    case "-p":
                        port = args[i + 1];
                        break;
                }
            }

            switch (Objects.requireNonNull(type)) {
                case "server":
                    new ChattingServerApplication(port); // java -jar chatting-application.jar -t server -p 8888
                    break;
                case "client":
                    new ChattingClientApplication(host, port); // java -jar chatting-application.jar -t client -h localhost -p 8888
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
