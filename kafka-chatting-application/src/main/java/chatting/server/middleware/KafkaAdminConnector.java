package chatting.server.middleware;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;

import java.util.Properties;

public enum KafkaAdminConnector {
    INSTANCE;

    public static final String DEFAULT_SERVER_URL = "localhost:9092";
    public static final String TIMEOUT = "10000";
    private static final Properties PROPS = new Properties();
    private static AdminClient adminClient;

    private static void init() {
        PROPS.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, DEFAULT_SERVER_URL);
        PROPS.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, TIMEOUT);
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
