package kafka.chatting.model;

import com.google.gson.Gson;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@ToString
@Setter
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    private MessageType messageType;
    private CommandType commandType;
    private User user;
    private LocalDateTime time;
    private String message;

    public String toJsonString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static Message jsonToMessage(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Message.class);
    }

    public static Message joinMessage(User user) {
        return Message.builder()
                .messageType(MessageType.SERVER)
                .commandType(CommandType.JOIN)
                .user(user)
                .time(LocalDateTime.now())
                .build();
    }

    public static Message setUserMessage(User user) {
        return Message.builder()
                .messageType(MessageType.SERVER)
                .commandType(CommandType.SET_USER)
                .user(user)
                .build();
    }

    public static Message normalMessage(User user, String message) {
        return Message.builder()
                .messageType(MessageType.CLIENT)
                .commandType(CommandType.NORMAL)
                .user(user)
                .message(message)
                .time(LocalDateTime.now())
                .build();
    }

    public static Message leaveMessage(User user) {
        return Message.builder()
                .messageType(MessageType.SERVER)
                .commandType(CommandType.LEAVE)
                .user(user)
                .time(LocalDateTime.now())
                .build();
    }
}