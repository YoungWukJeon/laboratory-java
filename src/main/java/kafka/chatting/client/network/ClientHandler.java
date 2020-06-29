package kafka.chatting.client.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import kafka.chatting.client.ClientInstance;
import kafka.chatting.model.Message;
import kafka.chatting.server.network.Server;
import kafka.chatting.utility.MessageFactory;

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
        switch (message.getCommandType()) {
            case SET_USER:
                processSetUserResponse(message);
                return;
            case GET_CHAT_ROOM_LIST:
                processGetChatRoomListResponse(message);
                return;
            case CREATE_CHAT_ROOM:
                processCreateChatRoomResponse(message);
                return;
            case JOIN:
                processJoinResponse(message);
                return;
            case LEAVE:
                processLeaveResponse(message);
                return;
            case NORMAL:
                processNormalResponse(message);
                return;
            default:
                System.out.println("Command Not Found");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.err.println("Connection was disconnected abnormally because of server problem.");
        System.err.println(cause.getMessage());
        ctx.close();
    }

    private void processSetUserResponse(Message message) {
        ClientInstance.getInstance().setUser(message.getUser());
        ClientInstance.getInstance().send(MessageFactory.chatRoomListClientMessage(ClientInstance.getInstance().getUser()));
    }

    private void processGetChatRoomListResponse(Message message) {
        publishMessage(message);
    }

    private void processCreateChatRoomResponse(Message message) {
        publishMessage(message);
    }

    private void processJoinResponse(Message message) {
        if (ClientInstance.getInstance().getUser().equals(message.getUser())) {
            publishMessage(message);
            return;
        }
        processNormalResponse(message);
    }

    private void processLeaveResponse(Message message) {
        if (ClientInstance.getInstance().getUser().equals(message.getUser())) {
            publishMessage(message);
            System.out.println("Exit this room(chatRoomNo=" + message.getChatRoomNo() + ") because of user '!quit' command.");
            ClientInstance.getInstance().removeChatRoomNo(message.getChatRoomNo());
            return;
        }
        processNormalResponse(message);
    }

    private void processNormalResponse(Message message) {
        ClientInstance.getInstance().addMessage(message);
        publishMessage(message);
    }

    private void publishMessage(Message message) {
        ClientInstance.getInstance().publishMessage(message);
    }
}