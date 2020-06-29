package kafka.chatting.server;

import io.netty.channel.Channel;
import kafka.chatting.model.Message;
import kafka.chatting.server.middleware.Consumer;
import kafka.chatting.server.middleware.KafkaAdminConnector;
import kafka.chatting.server.middleware.KafkaAdminUtil;
import kafka.chatting.server.middleware.Producer;
import kafka.chatting.server.network.Server;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;

public enum ServerInstance {
    INSTANCE;

    private Server server;
    public static final String TOPIC_PREFIX = "chatting_room_";
    public static final String TOPIC_NAME_FORMAT = TOPIC_PREFIX + "%02d";
    public static final int MAX_CHAT_ROOM_NUM = 100;
    private static final Map<Integer, Consumer> chatRooms = new HashMap<>();
    private static Producer producer;
    private static final Executor KAFKA_CONSUMER_EXECUTOR = Executors.newFixedThreadPool(MAX_CHAT_ROOM_NUM, r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t;
    });

    public static ServerInstance getInstance() {
        if (producer == null) {
            producer = new Producer();
        }
        return INSTANCE;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public int getServerPort() {
        return server.getPort();
    }

    public void createChatRoomConsumer(int no, String port) {
        if (!isJoinedChatRoom(no)) {
            Consumer consumer = Consumer.from(
                    String.format(TOPIC_NAME_FORMAT, no),
                    Objects.requireNonNullElse(port, Integer.toString(Server.DEFAULT_PORT)));
            CompletableFuture.runAsync(consumer, KAFKA_CONSUMER_EXECUTOR);
            chatRooms.put(no, consumer);
        }
    }

    public boolean isJoinedChatRoom(int no) {
        return chatRooms.containsKey(no);
    }

    public Set<Integer> getChatRooms() {
        return chatRooms.keySet();
    }

    public int createChatRoomNo() {
        int no = (int) (Math.random() * 100);
        if (getChatRooms().contains(no)) {
            return createChatRoomNo();
        }
        return no;
    }

    public void processReadMessage(Message message) {
        switch (message.getCommandType()) {
            case JOIN:
                server.addUserInChatRoomNo(message.getUser(), message.getChatRoomNo());
                broadcast(message);
                return;
            case LEAVE:
                broadcast(message);
                server.removeUserInChatRoomNo(message.getUser(), message.getChatRoomNo());
                return;
            case NORMAL:
                broadcast(message);
                return;
            default:
                System.out.println("Command Not Found");
        }
    }

    public void send(Channel channel, Message message) {
        server.send(channel, message);
    }

    public void broadcast(Message message) {
        server.broadcast(message);
    }

    public void publish(String topicName, String message) {
        producer.publish(topicName, message);
    }

    public void createConsumers(String port) {
        KafkaAdminUtil.getTopics(KafkaAdminConnector.getInstance().getAdminClient())
                .stream()
                .filter(s -> s.startsWith(ServerInstance.TOPIC_PREFIX))
                .map(s -> s.split("_")[2])
                .map(Integer::parseInt)
                .forEach(i -> createChatRoomConsumer(i, port)); // 토픽에 대응하는 Consumer 실행
    }
}
