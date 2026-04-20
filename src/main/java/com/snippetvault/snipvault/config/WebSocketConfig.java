package com.snippetvault.snipvault.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // turns on STOMP WebSocket support
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Step A: Enable a simple in-memory broker for topics
        registry.enableSimpleBroker("/topic");

        // Step B: Prefix for messages going FROM client TO server
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // This is the URL clients use to do the initial WebSocket handshake
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // allow all origins for dev
                .withSockJS(); // fallback for browsers that don't support WebSocket
    }
}

/*
enableSimpleBroker("/topic") — Spring runs a mini message broker in memory. Any message sent to /topic/anything gets routed to all subscribers of that topic
setApplicationDestinationPrefixes("/app") — When a client sends a message TO the server, it must start with /app. Spring routes it to your @MessageMapping methods
addEndpoint("/ws") — The handshake URL. Client connects here first to upgrade from HTTP to WebSocket
withSockJS() — If the browser doesn't support WebSocket, SockJS falls back to long-polling automatically
 */