package kafka.chatting;

import kafka.chatting.model.Message;
import kafka.chatting.model.User;

import java.time.LocalDateTime;

public class MessageFactory {
    private MessageFactory() {}

    public static Message normalClientMessage(User user, int chatRoomNo, String message) {
        return Message.builder()
                .messageType(Message.MessageType.CLIENT)
                .commandType(Message.CommandType.NORMAL)
                .user(user)
                .chatRoomNo(chatRoomNo)
                .message(message)
                .time(LocalDateTime.now())
                .build();
    }

    public static Message userJoinClientMessage(User user, int chatRoomNo) {
        return Message.builder()
                .messageType(Message.MessageType.CLIENT)
                .commandType(Message.CommandType.JOIN)
                .user(user)
                .chatRoomNo(chatRoomNo)
                .build();
    }

    public static Message userLeaveClientMessage(User user, int chatRoomNo) {
        return Message.builder()
                .messageType(Message.MessageType.CLIENT)
                .commandType(Message.CommandType.LEAVE)
                .user(user)
                .chatRoomNo(chatRoomNo)
                .build();
    }

    public static Message userJoinServerMessage(User user, int chatRoomNo) {
        return Message.builder()
                .messageType(Message.MessageType.SERVER)
                .commandType(Message.CommandType.JOIN)
                .user(user)
                .chatRoomNo(chatRoomNo)
                .time(LocalDateTime.now())
                .build();
    }

    public static Message clientSetUserServerMessage(User user) {
        return Message.builder()
                .messageType(Message.MessageType.SERVER)
                .commandType(Message.CommandType.SET_USER)
                .user(user)
                .time(LocalDateTime.now())
                .build();
    }

    public static Message userLeaveServerMessage(User user, int chatRoomNo) {
        return Message.builder()
                .messageType(Message.MessageType.SERVER)
                .commandType(Message.CommandType.LEAVE)
                .user(user)
                .chatRoomNo(chatRoomNo)
                .time(LocalDateTime.now())
                .build();
    }
}
