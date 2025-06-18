package com.example.be_datn.controller.chatbot;

import com.example.be_datn.dto.chatbot.request.ChatRequest;
import com.example.be_datn.dto.chatbot.response.ChatResponse;
import com.example.be_datn.service.chatbot.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) throws Exception {
        return chatbotService.getChatbotResponse(request);
    }
}
