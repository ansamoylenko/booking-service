package com.samoylenko.bookingservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samoylenko.bookingservice.model.dto.payment.InvoiceCreateDto;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

import static java.math.BigDecimal.valueOf;
import static org.assertj.core.api.Assertions.assertThat;

public class PayKeeperClientTest {
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PayKeeperClient payKeeperClient = new PayKeeperClient(restTemplate, objectMapper);

    @Test
    public void getToken() {
        var response = payKeeperClient.getToken();

        assertThat(response).isNotNull();
    }

    @Test
    public void getInvoice() {
        var createDto = InvoiceCreateDto.builder()
                .clientId("Романов Иван Иванович")
                .orderId("190720-081-1")
                .clientPhone("79633100056")
                .clientEmail("test@example.com")
                .quantity(2)
                .price(valueOf(3200))
                .cost(valueOf(6400))
                .expiry(Instant.now().plusSeconds(60))
                .build();

        var response = payKeeperClient.createInvoice(createDto);
        System.out.println(response);

        assertThat(response).isNotNull();
    }

    @Test
    public void getInvoiceInfo() {
        var info = payKeeperClient.getInvoiceInfo("20240619172556153");

        assertThat(info).isNotNull();
    }

    @Test
    public void getPaymentInfo() {
        var info = payKeeperClient.getPaymentInfo("221376218");

        assertThat(info).isNotNull();
    }

}
