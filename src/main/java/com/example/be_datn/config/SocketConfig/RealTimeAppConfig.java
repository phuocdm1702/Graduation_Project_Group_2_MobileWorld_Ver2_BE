package com.example.be_datn.config.SocketConfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class RealTimeAppConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Existing endpoint for /ws with SockJS and all origins
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();

        // Existing endpoint for /ws without SockJS (for native WebSocket clients like Android)
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");

        // Endpoints from the other WebSocketConfig.java
        registry.addEndpoint("/chat")
                .setAllowedOriginPatterns("http://localhost:3000", "http://localhost:5173", "http://localhost:5174", "*") // Added "*" to cover all
                .withSockJS();

        registry.addEndpoint("/chat-sockjs")
                .setAllowedOriginPatterns("http://localhost:3000", "http://localhost:5173", "http://localhost:5174", "*") // Added "*" to cover all
                .withSockJS();
    }
}