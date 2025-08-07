package com.example.be_datn.dto.chatbot.response;

import lombok.Data;

@Data
public class ChatResponse {
    private String reply;

    public ChatResponse() {
    }

    public ChatResponse(String reply) {
        this.reply = reply;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
