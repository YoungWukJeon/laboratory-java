package kafka.chatting.server.middleware;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;

import java.util.Properties;

public enum KafkaAdminConnector {
    INSTANCE;
    private static final Properties PROPS = new Properties();
    private static AdminClient adminClient;

    private static void init() {
        PROPS.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        PROPS.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, "10000");
    }

    public AdminClient getAdminClient() {
        return adminClient;
    }

    public static KafkaAdminConnector getInstance() {
        if (adminClient == null) {
            init();
            adminClient = AdminClient.create(PROPS);
        }
        return INSTANCE;
    }
}
