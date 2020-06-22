package kafka.chatting.model;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class ChatRoomInfo {
    private final int no;
    private final String recent;
    private final boolean isPresent;

    private ChatRoomInfo(int no, String recent, boolean isPresent) {
        this.no = no;
        this.recent = recent;
        this.isPresent = isPresent;
    }

    public static ChatRoomInfo from(int no, String recent, boolean isPresent) {
        return new ChatRoomInfo(no, recent, isPresent);
    }

    @Override
    public boolean equals(Object object) {
        ChatRoomInfo chatRoomInfo = (ChatRoomInfo) object;
        return this.no == chatRoomInfo.no;
    }

    @Override
    public int hashCode() {
        return no;
    }
}
