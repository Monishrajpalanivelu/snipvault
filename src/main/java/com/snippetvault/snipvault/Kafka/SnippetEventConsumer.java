package com.snippetvault.snipvault.Kafka;

import tools.jackson.databind.ObjectMapper;
import com.snippetvault.snipvault.DTO.SnippetActivityEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SnippetEventConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "snippet-events", groupId = "snipvault-group")
    public void consumeEvent(String message) {
        try {
            SnippetActivityEvent event = objectMapper.readValue(message, SnippetActivityEvent.class);
            log.info("Consumed Kafka event: action={}, user={}, snippet={}, language={}, time={}",
                    event.getAction(),
                    event.getUsername(),
                    event.getSnippetTitle(),
                    event.getLanguage(),
                    event.getTimestamp()
            );
        } catch (Exception e) {
            log.error("Failed to deserialize Kafka message: {}", message, e);
        }
    }
}