package kafka.chatting.ui;

public interface EventTarget<D> {
    void update(D d);
}
