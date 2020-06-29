package kafka.chatting.server.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import kafka.chatting.server.ServerInstance;
import kafka.chatting.server.middleware.KafkaAdminConnector;
import kafka.chatting.server.middleware.KafkaAdminUtil;
import kafka.chatting.utility.MessageFactory;
import kafka.chatting.model.Message;
import kafka.chatting.model.User;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ServerHandler extends SimpleChannelInboundHandler<String> {
    private final ChannelGroup channelGroup;

    public ServerHandler(ChannelGroup channelGroup) {
        this.channelGroup = channelGroup;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        User user = new User();
        incoming.attr(Server.USER).set(user);
        System.out.println(user + " has joined.");

        // 추가된 사용자에게 유저 정보 전달
        ServerInstance.getInstance().send(incoming, MessageFactory.clientSetUserServerMessage(user));
        channelGroup.add(incoming);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 사용자가 접속했을 때 서버에 표시
        Channel incoming = ctx.channel();
        User user = incoming.attr(Server.USER).get();
        System.out.println(user + " is online.");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        final Message message = Message.jsonToMessage(msg);
        System.out.println("Server received > " + message);
        processReadMessage(ctx.channel(), message);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 사용자가 접속을 끊었을 때 서버에 표시
        Channel incoming = ctx.channel();
        User user = incoming.attr(Server.USER).get();
        System.out.println(user + " is offline.");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        User user = incoming.attr(Server.USER).get();
        System.out.println(user + " has left.");

        channelGroup.remove(incoming);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        final User user = ctx.channel().attr(Server.USER).get();
        final Set<Integer> chatRoomNoes = ctx.channel().attr(Server.CHAT_ROOM_NO).get();

        ctx.close();
        System.err.println("User(" + user + ") was disconnected abnormally in chatRoomNo(" + chatRoomNoes + ")");
        System.err.println("current channel count: " + (channelGroup.size()));
        cause.printStackTrace();

        chatRoomNoes.forEach(chatRoomNo -> processLeaveRequest(MessageFactory.userLeaveServerMessage(user, chatRoomNo)));
    }

    public void processReadMessage(Channel channel, Message message) {
        switch (message.getCommandType()) {
            case GET_CHAT_ROOM_LIST:
                processGetChatRoomListRequest(channel);
                return;
            case CREATE_CHAT_ROOM:
                processCreateChatRoom(channel, message.getUser());
                return;
            case JOIN:
                processJoinRequest(message);
                return;
            case LEAVE:
                processLeaveRequest(message);
                return;
            case NORMAL:
                processNormalRequest(message);
                return;
            default:
                System.out.println("Command Not Found");
        }
    }

    private void processGetChatRoomListRequest(Channel channel) {
        ServerInstance.getInstance().send(channel,
                MessageFactory.clientGetChatRoomListServerMessage(
                        ServerInstance.getInstance()
                                .getChatRooms()
                                .stream()
                                .map(Object::toString)
                                .collect(Collectors.joining(" "))));
    }

    private void processCreateChatRoom(Channel channel, User user) {
        String port = Integer.toString(ServerInstance.getInstance().getServerPort());
        // Kafka에서 Topic을 생성할 수 있는지 알기 위해 topic 목록을 가져옴.
        // 현재 서버에 없는 topic 이 있을 경우 Consumer도 생성
        ServerInstance.getInstance().createConsumers(port);

        int chatRoomNo = ServerInstance.getInstance()
                .createChatRoomNo()
                .orElse(-1);

        // 서바가 더 이상 채팅 방을 생성할 수 없을 경우(topic이 100개 이상이 된 경우)
        if (chatRoomNo == -1) {
            return;
        }

//        try {
            String topicName = String.format(ServerInstance.TOPIC_NAME_FORMAT, chatRoomNo);
            KafkaAdminUtil.createTopic(KafkaAdminConnector.getInstance().getAdminClient(), topicName);
            ServerInstance.getInstance().createChatRoomConsumer(chatRoomNo, port);
            System.out.println("ChattingServer used kafka consumers: " + new TreeSet<> (ServerInstance.getInstance().getChatRooms()));
//            Thread.sleep(2000L);    // Consumer Connection 시간 대기
            ServerInstance.getInstance().send(channel, MessageFactory.clientCreateChatRoomServerMessage(chatRoomNo));
            ServerInstance.getInstance().addUserInChatRoomNo(user, chatRoomNo);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    private void processJoinRequest(Message message) {
        publish(MessageFactory.userJoinServerMessage(message.getUser(), message.getChatRoomNo()));
    }

    private void processLeaveRequest(Message message) {
        publish(MessageFactory.userLeaveServerMessage(message.getUser(), message.getChatRoomNo()));
    }

    private void processNormalRequest(Message message) {
        publish(MessageFactory.normalClientMessage(message.getUser(), message.getChatRoomNo(), message.getMessage()));
    }

    private void publish(Message message) {
        String topicName = String.format(ServerInstance.TOPIC_NAME_FORMAT, message.getChatRoomNo());
        ServerInstance.getInstance().publish(topicName, message.toJsonString());
    }
}