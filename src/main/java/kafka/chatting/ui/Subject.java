package kafka.chatting.ui;

public interface Subject<T> {
    void notifyObserver(T t);
}
