package com.example.be_datn.dto.chat;

import java.time.Instant;

public class ChatMessage {

    private String type; // text, image, file
    private String text;
    private String url;
    private String name;
    private String sender; // customer, employee
    private Instant time;
    private Integer customerId;
    private Integer employeeId;

    public ChatMessage() {
    }

    public ChatMessage(String type, String text, String url, String name, String sender, Instant time, Integer customerId, Integer employeeId) {
        this.type = type;
        this.text = text;
        this.url = url;
        this.name = name;
        this.sender = sender;
        this.time = time;
        this.customerId = customerId;
        this.employeeId = employeeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Instant getTime() {
        return time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }
}
