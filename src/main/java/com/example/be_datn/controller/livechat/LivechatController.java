package com.example.be_datn.controller.livechat;

import com.example.be_datn.dto.chat.ChatMessage;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


@Controller
public class LivechatController {

    private static final Logger logger = LoggerFactory.getLogger(LivechatController.class);
    private final SimpMessagingTemplate messagingTemplate;


    public LivechatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/sendMessage")
    public void handleChatMessage(@Payload ChatMessage message) {
        logger.info("Received message: {}", message);
        String destination = "/topic/messages/" + message.getRecipient();
        messagingTemplate.convertAndSend(destination, message);
    }
}
