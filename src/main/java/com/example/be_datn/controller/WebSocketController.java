package com.example.be_datn.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/invoice")
    @SendTo("/topic/invoices")
    public InvoiceData sendInvoice(InvoiceData invoiceData) {
        System.out.println("Received invoice: " + invoiceData);
        return invoiceData;
    }

    @MessageMapping("/select-invoice")
    @SendTo("/topic/selected-invoice")
    public InvoiceData selectInvoice(InvoiceData invoiceData) {
        System.out.println("Invoice selected: " + invoiceData);
        return invoiceData;
    }
}

class InvoiceData {
    private String invoiceNumber;
    private String cashier;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private Item[] items;
    private double discountAmount;
    private double shippingFee;
    private String paymentMethod;
    private String paymentStatus;

    // Getters và Setters
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public String getCashier() { return cashier; }
    public void setCashier(String cashier) { this.cashier = cashier; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public Item[] getItems() { return items; }
    public void setItems(Item[] items) { this.items = items; }
    public double getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(double discountAmount) { this.discountAmount = discountAmount; }
    public double getShippingFee() { return shippingFee; }
    public void setShippingFee(double shippingFee) { this.shippingFee = shippingFee; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}

class Item {
    private int id;
    private String name;
    private String code;
    private double price;
    private int quantity;
    private double total;
    private String color;
    private String storage;
    private boolean isNew;

    // Getters và Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getStorage() { return storage; }
    public void setStorage(String storage) { this.storage = storage; }
    public boolean isIsNew() { return isNew; }
    public void setIsNew(boolean isNew) { this.isNew = isNew; }
}