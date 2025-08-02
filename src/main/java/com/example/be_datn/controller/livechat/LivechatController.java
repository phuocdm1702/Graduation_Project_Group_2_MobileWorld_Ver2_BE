package com.example.be_datn.controller.livechat;

import com.example.be_datn.dto.chat.ChatMessage;
import com.example.be_datn.entity.account.KhachHang;
import com.example.be_datn.service.chatclient.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class LivechatController {

    @Autowired
    private ChatMessageService chatMessageService;

    @MessageMapping("/chat/customer/{customerId}")
    public void handleCustomerMessage(@DestinationVariable Integer customerId, ChatMessage message) {
        message.setCustomerId(customerId);
        message.setSender("customer");
        chatMessageService.sendMessageToCustomer(message);
    }

    @MessageMapping("/chat/employee/{customerId}")
    public void handleEmployeeMessage(@DestinationVariable Integer customerId, ChatMessage message) {
        message.setCustomerId(customerId);
        message.setSender("employee");
        chatMessageService.sendMessageToCustomer(message);
    }

    @GetMapping("/api/customers")
    public List<KhachHang> getAllCustomers() {
        return chatMessageService.getAllCustomers();
    }

    @GetMapping("/api/messages/{customerId}")
    public List<ChatMessage> getMessages(@PathVariable Integer customerId) {
        return chatMessageService.getMessagesForCustomer(customerId);
    }


}
