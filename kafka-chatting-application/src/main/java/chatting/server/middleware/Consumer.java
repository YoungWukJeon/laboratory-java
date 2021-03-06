package chatting.server.middleware;

import chatting.model.Message;
import chatting.server.ServerInstance;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class Consumer implements Runnable {
    private final String topicName; // chatting_message
    private final String groupName; // Kafka Consumer Group
    private static final long TIMEOUT = 1000L;  // 1 second
    private static final Properties PROPS = new Properties();
    private KafkaConsumer<String, String> kafkaConsumer;

    private Consumer(String topicName, String groupName) {
        this.topicName = topicName;
        this.groupName = groupName;
        init();
    }

    private void init() {
        PROPS.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        PROPS.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        PROPS.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        PROPS.put(ConsumerConfig.GROUP_ID_CONFIG, "chatting-app-" + groupName);
    }

    @Override
    public void run() {
        try (KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<> (PROPS)) {
            kafkaConsumer.subscribe(List.of(topicName));
            while (true) {
                ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(TIMEOUT));
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(record);
                    ServerInstance.getInstance().processReadMessage(Message.jsonToMessage(record.value()));
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static Consumer from(String topicName, String groupName) {
        return new Consumer(topicName, groupName);
    }
}
