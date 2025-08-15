package com.example.be_datn.service.chatclient;

import com.example.be_datn.dto.chat.ChatMessage;
import com.example.be_datn.entity.account.KhachHang;
import com.example.be_datn.repository.account.KhachHang.KhachHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ChatMessageService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private KhachHangRepository khachHangRepository;

    private final Map<Integer, List<ChatMessage>> messageStore = new HashMap<>();
    private final Set<Integer> activeCustomerIds = ConcurrentHashMap.newKeySet();


    public void sendMessageToCustomer(ChatMessage message) {
        if (message == null || message.getCustomerId() == null) {
            throw new IllegalArgumentException("Message or customerId cannot be null");
        }

        Integer customerId = message.getCustomerId();
        boolean isNewCustomer = !activeCustomerIds.contains(customerId);

        if ("customer".equals(message.getSender()) && isNewCustomer) {
            activeCustomerIds.add(customerId);
            khachHangRepository.findById(customerId).ifPresent(customer -> {
                messagingTemplate.convertAndSend("/topic/new-customer", customer);
            });
        }

        if (!messageStore.containsKey(customerId)) {
            messageStore.put(customerId, new ArrayList<>());
        }
        ChatMessage storedMessage = new ChatMessage();
        storedMessage.setCustomerId(customerId);
        storedMessage.setSender(message.getSender());
        storedMessage.setText(message.getText());
        storedMessage.setType(message.getType());
        storedMessage.setTime(Instant.parse(Instant.now().toString()));
        messageStore.get(customerId).add(storedMessage);

        messagingTemplate.convertAndSend("/topic/customer/" + customerId, storedMessage);
        messagingTemplate.convertAndSend("/topic/admin/" + customerId, storedMessage);
    }

    public List<ChatMessage> getMessagesForCustomer(Integer customerId) {
        if (customerId == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(messageStore.getOrDefault(customerId, new ArrayList<>()));
    }

    public List<KhachHang> getActiveCustomers() {
        return activeCustomerIds.stream()
                .map(id -> khachHangRepository.findById(id))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public List<KhachHang> getAllCustomers() {
        return khachHangRepository.findAll();
    }

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
        sendMessageToCustomer(response);
        return response;
    }
}
