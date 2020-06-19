package kafka.chatting;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class KafkaAdminUtil {
    public static Collection<String> getTopics(AdminClient adminClient) {
        ListTopicsResult listTopicsResult = adminClient.listTopics();
        try {
            return listTopicsResult.names().get(3L, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptySet();
        }
    }

    public static void createTopic(AdminClient adminClient, String topicName) {
        adminClient.createTopics(
                Collections.singletonList(new NewTopic(topicName, 1, (short) 1)))
                .all();
    }

    public static void createTopics(AdminClient adminClient, List<String> topicNames) {
        adminClient.createTopics(
                topicNames.stream()
                        .map(s -> new NewTopic(s, 1, (short) 1))
                        .collect(Collectors.toList()))
                .all();
    }
}
