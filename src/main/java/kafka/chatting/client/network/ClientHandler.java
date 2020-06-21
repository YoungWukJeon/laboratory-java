package kafka.chatting.client.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import kafka.chatting.model.Message;
import kafka.chatting.server.network.Server;

public class ClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        Client.getInstance().setUser(ctx.channel().attr(Server.USER).get());
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
            Client.getInstance().setUser(message.getUser());
            return;
        } else if (message.getMessageType() == Message.MessageType.SERVER
                && message.getCommandType() == Message.CommandType.LEAVE
                && Client.getInstance().getUser().equals(message.getUser())) {
            Client.getInstance().addMessage(message);
            System.out.println("Exit this room(chatRoomNo=" + message.getChatRoomNo() + ") because of user '!quit' command.");
            Client.getInstance().removeChatRoomNo(message.getChatRoomNo());
            return;
        }
        Client.getInstance().addMessage(message);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.err.println("Connection was disconnected abnormally because of server problem.");
        System.err.println(cause.getMessage());
//        cause.printStackTrace();
        ctx.close();
    }
}