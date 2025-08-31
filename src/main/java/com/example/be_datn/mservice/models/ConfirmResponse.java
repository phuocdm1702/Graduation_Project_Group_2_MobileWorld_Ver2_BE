package com.example.be_datn.mservice.models;

import com.example.be_datn.mservice.enums.ConfirmRequestType;

public class ConfirmResponse extends Response {
    private Long amount;
    private Long transId;
    private String requestId;
    private ConfirmRequestType requestType;
}
