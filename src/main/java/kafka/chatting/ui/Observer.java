package kafka.chatting.ui;

public interface Observer<T> {
    void update(T t);
}
