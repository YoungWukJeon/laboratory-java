package kafka.chatting.client.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import kafka.chatting.client.ClientInstance;
import kafka.chatting.model.Message;
import kafka.chatting.server.network.Server;
import kafka.chatting.utility.MessageFactory;

// TODO: 2020-06-23 processReadMessage 간소화
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
//        if (message.getMessageType() == Message.MessageType.SERVER
//                && message.getCommandType() == Message.CommandType.SET_USER) {
//            ClientInstance.getInstance().setUser(message.getUser());
//            ClientInstance.getInstance().send(MessageFactory.chatRoomListClientMessage());
//            return;
//        } else if (message.getCommandType() == Message.CommandType.CREATE_CHAT_ROOM) {
//            ClientInstance.getInstance().publishMessage(message);
//            return;
        } else if (message.getMessageType() == Message.MessageType.SERVER
                && message.getCommandType() == Message.CommandType.LEAVE
                && ClientInstance.getInstance().getUser().equals(message.getUser())) {
            ClientInstance.getInstance().publishMessage(message);
            System.out.println("Exit this room(chatRoomNo=" + message.getChatRoomNo() + ") because of user '!quit' command.");
            ClientInstance.getInstance().removeChatRoomNo(message.getChatRoomNo());
            ClientInstance.getInstance().publishMessage(message);
            return;
//        } else if (message.getCommandType() == Message.CommandType.JOIN
//                && ClientInstance.getInstance().getUser().equals(message.getUser())) {
//            ClientInstance.getInstance().publishMessage(message);
//            return;
        } else if (message.getCommandType() != Message.CommandType.GET_CHAT_ROOM_LIST) {
            ClientInstance.getInstance().addMessage(message);
            ClientInstance.getInstance().publishMessage(message);
            return;
        }
//        ClientInstance.getInstance().publishMessage(message);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.err.println("Connection was disconnected abnormally because of server problem.");
        System.err.println(cause.getMessage());
        ctx.close();
    }

    private void processSetUserResponse(Message message) {
        ClientInstance.getInstance().setUser(message.getUser());
        ClientInstance.getInstance().send(MessageFactory.chatRoomListClientMessage());
    }

    private void processGetChatRoomListResponse(Message message) {
//        ClientInstance.getInstance().publishMessage(message);
        publishMessage(message);
    }

    private void processCreateChatRoomResponse(Message message) {
//        ClientInstance.getInstance().publishMessage(message);
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
//        ClientInstance.getInstance().publishMessage(message);
            publishMessage(message);
            System.out.println("Exit this room(chatRoomNo=" + message.getChatRoomNo() + ") because of user '!quit' command.");
            ClientInstance.getInstance().removeChatRoomNo(message.getChatRoomNo());
//        publishMessage(message);
            return;
        }
        processNormalResponse(message);
    }

    private void processNormalResponse(Message message) {
        publishMessage(message);
    }

    private void publishMessage(Message message) {
        ClientInstance.getInstance().publishMessage(message);
    }
}