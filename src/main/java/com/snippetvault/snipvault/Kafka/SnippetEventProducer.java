package com.snippetvault.snipvault.Kafka;


import tools.jackson.databind.ObjectMapper;
import com.snippetvault.snipvault.DTO.SnippetActivityEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SnippetEventProducer {

    private static final String TOPIC = "snippet-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishEvent(SnippetActivityEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, event.getAction(), message);
            log.info("Published Kafka event: {} - {} by {}",
                    event.getAction(),
                    event.getSnippetTitle(),
                    event.getUsername());
        } catch (Exception e) {
            log.error("Failed to serialize Kafka event: {}", event, e);
        }
    }
}