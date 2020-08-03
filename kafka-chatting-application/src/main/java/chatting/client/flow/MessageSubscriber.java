package chatting.client.flow;

import chatting.model.Message;

import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.Flow.Subscriber;
import java.util.function.Consumer;

public class MessageSubscriber implements Subscriber<Message> {
    private Subscription subscription;
    private final Consumer<Message> consumer;

    public MessageSubscriber(Consumer<Message> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1L);
    }

    @Override
    public void onNext(Message message) {
        consumer.accept(message);
        subscription.request(1L);
    }

    @Override
    public void onError(Throwable throwable) {
        System.err.println(throwable.getMessage());
        subscription.cancel();
    }

    @Override
    public void onComplete() {
        System.out.println("Done!");
        subscription.cancel();
    }
}
