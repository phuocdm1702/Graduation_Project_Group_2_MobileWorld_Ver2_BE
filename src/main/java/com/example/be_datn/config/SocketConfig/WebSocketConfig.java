package com.example.be_datn.config.SocketConfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register native WebSocket endpoint - NO AUTHENTICATION REQUIRED
        registry.addEndpoint("/chat")
                .setAllowedOriginPatterns("http://localhost:3000", "http://localhost:5173"); // Allow all localhost ports for development

        // Also register SockJS fallback endpoint
        registry.addEndpoint("/chat-sockjs")
                .setAllowedOriginPatterns("http://localhost:3000", "http://localhost:5173")
                .withSockJS();
    }

}
