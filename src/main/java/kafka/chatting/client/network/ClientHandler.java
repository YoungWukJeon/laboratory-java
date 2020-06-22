package kafka.chatting.client.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import kafka.chatting.client.ClientInstance;
import kafka.chatting.model.Message;
import kafka.chatting.server.network.Server;

public class ClientHandler extends SimpleChannelInboundHandler<String> {
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ClientInstance.getInstance().setUser(ctx.channel().attr(Server.USER).get());
        System.out.println("Connection Established.");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String text) throws Exception {
        final Message message = Message.jsonToMessage(text);
        System.out.println("Received > " + message);
        processReadMessage(ctx, message);
    }

    private void processReadMessage(ChannelHandlerContext ctx, Message message) {
        if (message.getMessageType() == Message.MessageType.SERVER
                && message.getCommandType() == Message.CommandType.SET_USER) {
            ClientInstance.getInstance().setUser(message.getUser());
            return;
        } else if (message.getMessageType() == Message.MessageType.SERVER
                && message.getCommandType() == Message.CommandType.LEAVE
                && ClientInstance.getInstance().getUser().equals(message.getUser())) {
            ClientInstance.getInstance().publishMessage(message);
            System.out.println("Exit this room(chatRoomNo=" + message.getChatRoomNo() + ") because of user '!quit' command.");
            ClientInstance.getInstance().removeChatRoomNo(message.getChatRoomNo());
            return;
        }
        ClientInstance.getInstance().addMessage(message);
        ClientInstance.getInstance().publishMessage(message);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.err.println("Connection was disconnected abnormally because of server problem.");
        System.err.println(cause.getMessage());
        ctx.close();
    }
}