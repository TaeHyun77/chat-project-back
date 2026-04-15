package com.example.chat.airport.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    // 항공편 ES 인덱싱 토픽 (INSERT + UPDATE 모두)
    @Bean
    public NewTopic planeIndexingTopic() {
        return TopicBuilder.name("airport.plane.indexing")
                .partitions(3)
                .replicas(1)
                .config("retention.ms", String.valueOf(7 * 24 * 60 * 60 * 1000L))
                .build();
    }

    // 항공편 변경 알림 토픽 (의미 있는 UPDATE만)
    @Bean
    public NewTopic planeChangedTopic() {
        return TopicBuilder.name("airport.plane.changed")
                .partitions(3)
                .replicas(1)
                .config("retention.ms", String.valueOf(7 * 24 * 60 * 60 * 1000L))
                .build();
    }

    // 혼잡도 변화 알림 토픽
    @Bean
    public NewTopic congestionChangedTopic() {
        return TopicBuilder.name("airport.congestion.changed")
                .partitions(3)
                .replicas(1)
                .config("retention.ms", String.valueOf(3 * 24 * 60 * 60 * 1000L))
                .build();
    }
}