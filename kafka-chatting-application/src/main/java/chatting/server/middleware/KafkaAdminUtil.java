package chatting.server.middleware;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class KafkaAdminUtil {
    public static final long TIMEOUT = 3L;
    public static final int DEFAULT_PARTITION_NUM = 1;
    public static final short DEFAULT_REPLICA_NUM = 1;

    public static Collection<String> getTopics(AdminClient adminClient) {
        ListTopicsResult listTopicsResult = adminClient.listTopics();
        try {
            return listTopicsResult.names().get(TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptySet();
        }
    }

    public static void createTopic(AdminClient adminClient, String topicName) {
        adminClient.createTopics(
                Collections.singletonList(new NewTopic(topicName, DEFAULT_PARTITION_NUM, DEFAULT_REPLICA_NUM))
        ).all();
    }

    public static void createTopics(AdminClient adminClient, List<String> topicNames) {
        adminClient.createTopics(
                topicNames.stream()
                        .map(s -> new NewTopic(s, DEFAULT_PARTITION_NUM, DEFAULT_REPLICA_NUM))
                        .collect(Collectors.toList())
        ).all();
    }
}
