package chatting.server.middleware;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class Producer {
    private static final Properties PROPS = new Properties();
    private static KafkaProducer<String, String> kafkaProducer;

    public Producer() {
        init();
        kafkaProducer = new KafkaProducer<> (PROPS);
    }

    private static void init() {
        PROPS.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        PROPS.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        PROPS.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    }

    public void publish(String topicName, String message) {
        ProducerRecord<String, String> record = new ProducerRecord<> (topicName, message);
        kafkaProducer.send(record, (metadata, exception) -> {
            if (exception != null) {
                System.out.println("Exception Occurred!");
                exception.printStackTrace();
            }
        });
        System.out.println("Kafka Producing in topic: ... " + topicName + " => " + message);
        kafkaProducer.flush();
    }
}
