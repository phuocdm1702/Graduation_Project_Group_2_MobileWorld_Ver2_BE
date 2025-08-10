// File: backend/src/main/java/com/example/be_datn/config/SocketConfig/WebSocketConfig.java
package com.example.be_datn.config.SocketConfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // Kênh để gửi thông điệp
        config.setApplicationDestinationPrefixes("/app"); // Prefix cho các endpoint client gửi tới
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register native WebSocket endpoint - NO AUTHENTICATION REQUIRED
        registry.addEndpoint("/chat")
                .setAllowedOriginPatterns("http://localhost:3000", "http://localhost:5173", "http://localhost:5174") // Thêm localhost:5174
                .withSockJS(); // Kích hoạt SockJS cho tất cả endpoint

        // Cũng có thể giữ endpoint SockJS riêng nếu cần
        registry.addEndpoint("/chat-sockjs")
                .setAllowedOriginPatterns("http://localhost:3000", "http://localhost:5173", "http://localhost:5174") // Thêm localhost:5174
                .withSockJS();
    }
}