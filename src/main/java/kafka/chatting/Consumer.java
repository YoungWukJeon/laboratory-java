package kafka.chatting;

import com.sun.tools.javac.util.List;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Properties;

public class Consumer {
    private static final String TOPIC_NAME = "chatting_message";
    private static final long TIMEOUT = 1000L;  // 1 second
    private Properties properties = new Properties();

    public Consumer() {
        init();
    }

    private void init() {
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, TOPIC_NAME);
    }

    public void run() {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<> (properties);

        try {
            consumer.subscribe(List.of(TOPIC_NAME));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(TIMEOUT));
                for (ConsumerRecord<String, String> record: records) {
                    String message = record.value();
                    String key = record.key();
                    System.out.println(record);
                    System.out.println(key + ", " + message);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            consumer.close();
        }
    }
}
