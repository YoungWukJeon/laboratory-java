package kafka.chatting.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import kafka.chatting.model.CommandType;
import kafka.chatting.model.Message;
import kafka.chatting.model.MessageType;
import kafka.chatting.ui.EventTarget;

public class ClientHandler extends SimpleChannelInboundHandler<String> {
    private EventTarget<Message> eventTarget;

    public void setEventTarget(EventTarget<Message> eventTarget) {
        if (this.eventTarget == null) {
            this.eventTarget = eventTarget;
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        Client.getInstance().setUser(ctx.channel().attr(Server.USER).get());
        System.out.println("Connection Established.");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String text) throws Exception {
        System.out.println("Received > " + text);
        Message message = Message.jsonToMessage(text);

        if (message.getMessageType() == MessageType.SERVER
                && message.getCommandType() == CommandType.SET_USER) {
            Client.getInstance().setUser(message.getUser());
            return;
        } else if (message.getMessageType() == MessageType.SERVER
                && message.getCommandType() == CommandType.LEAVE
                && Client.getInstance().getUser().equals(message.getUser())) {
            System.out.println("ChattingClient terminated because of user '!quit' command");
            ctx.close();
            System.exit(0);
        }

        this.eventTarget.update(message);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}