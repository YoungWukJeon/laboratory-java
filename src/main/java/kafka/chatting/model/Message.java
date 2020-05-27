package kafka.chatting.model;

import com.google.gson.Gson;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@ToString
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class Message {
    private MessageType messageType;
    private CommandType commandType;
    private User user;
    private Integer chatRoomNo;
    private LocalDateTime time;
    private String message;

    private Message() {}

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