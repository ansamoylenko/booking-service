package com.samoylenko.bookingservice.model.dto.payment.paykeeper;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import java.time.Instant;

@Value
public class InvoiceDto {
    @JsonProperty("id")
    String id;

    @JsonProperty("user_id")
    String userId;

    @JsonProperty("status")
    String status;

    @JsonProperty("pay_amount")
    Float payAmount;

    @JsonProperty("clientid")
    String clientId;

    @JsonProperty("client_email")
    String clientEmail;

    @JsonProperty("client_phone")
    String clientPhone;

    @JsonProperty("orderid")
    String orderId;

    @JsonProperty("paymentid")
    String paymentId;

    @JsonProperty("service_name")
    String serviceName;

    @JsonProperty("expiry_datetime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    Instant expiryDatetime;

    @JsonProperty("created_datetime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    Instant createdDatetime;

    @JsonProperty("paid_datetime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    Instant paidDatetime;
}