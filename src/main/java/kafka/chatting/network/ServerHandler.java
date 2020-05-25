package kafka.chatting.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import kafka.chatting.model.CommandType;
import kafka.chatting.model.Message;
import kafka.chatting.model.User;

import java.util.function.Predicate;

public class ServerHandler extends SimpleChannelInboundHandler<String> {
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        User user = new User();
        incoming.attr(Server.USER).set(user);
        System.out.println(user + " has joined.");

        // 추가된 사용자에게 유저 정보 전달
        incoming.writeAndFlush(Message.setUserMessage(user).toJsonString());
        // 사용자가 추가되었을 때 기존 사용자에게 알림
//        broadcast(Message.joinMessage(user).toJsonString());
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
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Message message = Message.jsonToMessage(msg);
        System.out.println("Server received > " + message);

        switch (message.getCommandType()) {
            case JOIN:
                ctx.channel().attr(Server.CHAT_ROOM_NO).set(message.getChatRoomNo());
                broadcast(Message.joinMessage(message.getUser(), message.getChatRoomNo()));
                break;
            case LEAVE:
                ctx.channel().writeAndFlush(Message.leaveMessage(message.getUser(), message.getChatRoomNo()).toJsonString());
                break;
            case NORMAL:
                broadcast(Message.normalMessage(message.getUser(), message.getChatRoomNo(), message.getMessage()));
                break;
            default:
                System.out.println("Command Not Found");
        }
    }

    private void broadcast(Message message) {
        System.out.println("Server broadcast > " + message);
        channelGroup.stream()
                .filter(Predicate.not(
                        channel -> message.getCommandType() == CommandType.JOIN
                                && channel.attr(Server.USER).get().equals(message.getUser())))
                .filter(channel -> channel.attr(Server.CHAT_ROOM_NO).get().equals(message.getChatRoomNo()))
                .forEach(channel -> channel.writeAndFlush(message.toJsonString()));
    }
}