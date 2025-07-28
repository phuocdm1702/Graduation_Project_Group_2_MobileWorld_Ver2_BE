package com.example.be_datn.service.chatclient;

import com.example.be_datn.dto.chat.ChatMessage;
import com.example.be_datn.entity.account.KhachHang;
import com.example.be_datn.repository.account.KhachHang.KhachHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatMessageService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private KhachHangRepository khachHangRepository;

    // Lưu trữ tin nhắn tạm thời (chỉ dùng cho demo, không đồng bộ trên nhiều instance)
    private final Map<Integer, List<ChatMessage>> messageStore = new HashMap<>();

    public void sendMessageToCustomer(ChatMessage message) {
        if (message == null || message.getCustomerId() == null) {
            throw new IllegalArgumentException("Message or customerId cannot be null");
        }

        Integer customerId = message.getCustomerId();
        if (!messageStore.containsKey(customerId)) {
            messageStore.put(customerId, new ArrayList<>());
        }
        ChatMessage storedMessage = new ChatMessage();
        storedMessage.setCustomerId(customerId);
        storedMessage.setSender(message.getSender());
        storedMessage.setText(message.getText());
        storedMessage.setType(message.getType());
        storedMessage.setTime(Instant.parse(Instant.now().toString())); // Đảm bảo thời gian đồng bộ từ server
        messageStore.get(customerId).add(storedMessage);

        // Gửi tin nhắn tới client
        messagingTemplate.convertAndSend("/topic/customer/" + customerId, storedMessage);

        // Gửi tin nhắn tới admin
        messagingTemplate.convertAndSend("/topic/admin/" + customerId, storedMessage);
    }

    public List<ChatMessage> getMessagesForCustomer(Integer customerId) {
        if (customerId == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(messageStore.getOrDefault(customerId, new ArrayList<>()));
    }

    public List<KhachHang> getAllCustomers() {
        return khachHangRepository.findAll();
    }

    // Phương thức hỗ trợ AI (tùy chọn)
    public ChatMessage processAIResponse(String inputMessage, Integer customerId) {
        if (inputMessage == null || inputMessage.trim().isEmpty() || customerId == null) {
            return null;
        }
        ChatMessage response = new ChatMessage();
        response.setCustomerId(customerId);
        response.setSender("ai");
        response.setText("AI: Tôi nhận được tin nhắn của bạn: " + inputMessage);
        response.setType("text");
        response.setTime(Instant.parse(Instant.now().toString()));
        sendMessageToCustomer(response); // Lưu và gửi ngay
        return response;
    }
}