package kafka.chatting;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class Producer {
    private static final String TOPIC_NAME = "chatting_message";
    private Properties properties = new Properties();

    public Producer() {
        init();
    }

    private void init() {
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    }

    public void run() {
        KafkaProducer<String, String> producer = new KafkaProducer<> (properties);

        try {
            String message = "";
            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC_NAME, message);
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    System.out.println("Exception Occurred!");
                    exception.printStackTrace();
                }
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            producer.flush();
            producer.close();
        }
    }
}
