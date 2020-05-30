package kafka.chatting.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import kafka.chatting.MessageFactory;
import kafka.chatting.model.Message;
import kafka.chatting.model.User;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class ServerHandler extends SimpleChannelInboundHandler<String> {
    private static final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        User user = new User();
        incoming.attr(Server.USER).set(user);
        System.out.println(user + " has joined.");

        // 추가된 사용자에게 유저 정보 전달
        writeMessage(incoming, MessageFactory.clientSetUserServerMessage(user));
        // 사용자가 추가되었을 때 기존 사용자에게 알림
//        broadcast(Message.joinMessage(user).toJsonString());
        channelGroup.add(incoming);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        final Message message = Message.jsonToMessage(msg);
        System.out.println("Server received > " + message);
        processReadMessage(ctx.channel(), message);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 사용자가 접속했을 때 서버에 표시
        Channel incoming = ctx.channel();
        User user = incoming.attr(Server.USER).get();
        System.out.println(user + " is online.");
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

        // 사용자가 나갔을 때 기존 사용자에게 알림
//        broadcast(Message.leaveMessage(user).toJsonString());
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

        chatRoomNoes.forEach(chatRoomNo -> broadcast(MessageFactory.userLeaveServerMessage(user, chatRoomNo)));
    }

    private void processReadMessage(Channel channel, Message message) {
        Set<Integer> list = channel.attr(Server.CHAT_ROOM_NO).get();

        switch (message.getCommandType()) {
            case JOIN:
                if (list == null) {
                    list = new HashSet<> ();
                    channel.attr(Server.CHAT_ROOM_NO).set(list);
                }
                list.add(message.getChatRoomNo());
                broadcast(MessageFactory.userJoinServerMessage(message.getUser(), message.getChatRoomNo()));
                break;
            case LEAVE:
                broadcast(MessageFactory.userLeaveServerMessage(message.getUser(), message.getChatRoomNo()));
                list.remove(message.getChatRoomNo());
                break;
            case NORMAL:
                broadcast(MessageFactory.normalClientMessage(message.getUser(), message.getChatRoomNo(), message.getMessage()));
                break;
            default:
                System.out.println("Command Not Found");
        }
    }

    private void broadcast(Message message) {
        System.out.println("Server broadcast > " + message);
        channelGroup.stream()
                .filter(Predicate.not(
                        channel -> message.getCommandType() == Message.CommandType.JOIN
                                && channel.attr(Server.USER).get().equals(message.getUser())))
                .filter(channel -> channel.attr(Server.CHAT_ROOM_NO).get() != null
                        && channel.attr(Server.CHAT_ROOM_NO).get().contains(message.getChatRoomNo()))
                .forEach(channel -> writeMessage(channel, message));
    }

    private void writeMessage(Channel channel, Message message) {
        channel.writeAndFlush(message.toJsonString());
    }
}