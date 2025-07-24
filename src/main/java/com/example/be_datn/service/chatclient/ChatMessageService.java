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

    // Lưu trữ tin nhắn tạm thời (trong thực tế nên dùng database)
    private final Map<Integer, List<ChatMessage>> messageStore = new HashMap<>();

    public void sendMessageToCustomer(ChatMessage message) {
        // Lưu tin nhắn
        Integer customerId = message.getCustomerId();
        if (!messageStore.containsKey(customerId)) {
            messageStore.put(customerId, new ArrayList<>());
        }
        message.setTime(Instant.now());
        messageStore.get(customerId).add(message);

        // Gửi tin nhắn tới client
        messagingTemplate.convertAndSend("/topic/customer/" + customerId, message);

        // Gửi tin nhắn tới admin
        messagingTemplate.convertAndSend("/topic/admin/" + customerId, message);
    }

    public List<ChatMessage> getMessagesForCustomer(Integer customerId) {
        return messageStore.getOrDefault(customerId, new ArrayList<>());
    }

    public List<KhachHang> getAllCustomers() {
        return khachHangRepository.findAll();
    }

}
