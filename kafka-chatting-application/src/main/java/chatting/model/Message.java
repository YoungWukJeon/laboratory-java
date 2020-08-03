package chatting.model;

import com.google.gson.Gson;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
@EqualsAndHashCode
public class Message {
    private MessageType messageType;
    private CommandType commandType;
    private User user;
    private Integer chatRoomNo;
    private LocalDateTime time;
    private String message;

    @Builder
    public Message(MessageType messageType, CommandType commandType, User user, Integer chatRoomNo, LocalDateTime time, String message) {
        this.messageType = messageType;
        this.commandType = commandType;
        this.user = user;
        this.chatRoomNo = chatRoomNo;
        this.time = time;
        this.message = message;
    }

    public String toJsonString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static Message jsonToMessage(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Message.class);
    }

    public enum CommandType {
        JOIN, NORMAL, SET_USER, LEAVE, GET_CHAT_ROOM_LIST, CREATE_CHAT_ROOM,
    }

    public enum MessageType {
        SERVER, CLIENT
    }
}