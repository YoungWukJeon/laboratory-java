package chatting.server;

import chatting.server.network.Server;

public class ChattingServerApplication {
    public ChattingServerApplication(String port) {
        Server server = new Server(port);
        ServerInstance.getInstance().createConsumers(port);
        ServerInstance.getInstance().setServer(server);

        System.out.println("ChattingServer is running.");
        server.run();
        System.out.println("ChattingServer has been shut down.");
    }
}
