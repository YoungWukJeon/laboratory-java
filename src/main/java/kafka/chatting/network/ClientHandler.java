package kafka.chatting.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import kafka.chatting.model.Message;
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
            System.out.println("ChattingClient terminated because of user '!quit' command");
            ctx.close();
            System.exit(0);
            // TODO: 2020-05-28 !quit를 입력했을 때, 클라이언트 종료가 아니고 채팅방 자체에서 나가게 설정을 해야됨. 그리고 다이얼로그를 dismiss 하게...
        }
        this.eventTarget.update(message);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.err.println("Connection was disconnected abnormally because of server problem.");
        System.err.println(cause.getMessage());
//        cause.printStackTrace();
        ctx.close();
    }
}