package com.samoylenko.bookingservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samoylenko.bookingservice.model.dto.payment.InvoiceCreateDto;
import com.samoylenko.bookingservice.model.dto.payment.paykeeper.InvoiceDto;
import com.samoylenko.bookingservice.model.dto.payment.paykeeper.InvoiceResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.web.util.UriComponentsBuilder.fromHttpUrl;

@Slf4j
@Service
@AllArgsConstructor
public class PayKeeperClient {
    private final RestTemplate restTemplate;
    private static final String baseUrl = "https://demo.paykeeper.ru";
    private static final String login = "demo";
    private static final String password = "demo";

    private final ObjectMapper mapper;

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        String auth = login + ":" + password;
        String base64Creds = Base64.getEncoder().encodeToString(auth.getBytes());
        headers.add("Authorization", "Basic " + base64Creds);
        headers.add("Content-Type", APPLICATION_FORM_URLENCODED_VALUE);
        return headers;
    }

    public String getToken() {
        var headers = getHeaders();
        var url = fromHttpUrl(baseUrl).path("/info/settings/token/").toUriString();
        var request = new HttpEntity<>(headers);

        var response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);

        if (response.getBody() != null && response.getBody().containsKey("token")) {
            return response.getBody().get("token").toString();
        } else {
            throw new RuntimeException("Error getting token");
        }
    }

    public InvoiceResponse createInvoice(InvoiceCreateDto data) {
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
        try {
            var token = getToken();
            var headers = getHeaders();
            var url = fromHttpUrl(baseUrl).path("/change/invoice/preview/").toUriString();
            var serviceData = mapper.writeValueAsString(data.getServiceData());
            var body = new LinkedMultiValueMap<String, String>();
            body.add("pay_amount", data.getPayAmount().toString());
            body.add("clientid", data.getClientId());
            body.add("orderid", data.getOrderId());
            body.add("service_name", serviceData);
            body.add("client_email", data.getClientEmail());
            body.add("client_phone", data.getClientPhone());
//            body.add("expiry", formatter.format(data.getExpiry()));
            body.add("token", token);

            var request = new HttpEntity<>(body, headers);
            var response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

            Assert.notNull(response, "Not found response");
            Assert.notNull(response.getBody(), "Not found response body");
            var invoiceId = response.getBody().get("invoice_id").toString();
            var invoiceUrl = response.getBody().get("invoice_url").toString();
            Assert.notNull(invoiceId, "Not found invoice id");
            Assert.notNull(invoiceUrl, "Not found invoice url");

            return new InvoiceResponse(invoiceId, invoiceUrl);
        } catch (Exception e) {
            log.error("Error creating invoice", e);
            throw new RuntimeException(e);
        }
    }

    public String getPaymentInfo(String id) {
        HttpHeaders headers = getHeaders();
        String url = fromHttpUrl(baseUrl)
                .path("/info/payments/byid/")
                .queryParam("id", id)
                .toUriString();

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);


        var responseBody = response.getBody();
        log.info("Response: {}", responseBody);
        if (responseBody != null) {
            return responseBody.toString();
        } else {
            throw new RuntimeException();
        }
    }

    public InvoiceDto getInvoiceInfo(String id) {
        var headers = getHeaders();
        var url = fromHttpUrl(baseUrl)
                .path("/info/invoice/byid/")
                .queryParam("id", id)
                .toUriString();

        var request = new HttpEntity<>(headers);
        var response = restTemplate.exchange(url, HttpMethod.GET, request, InvoiceDto.class);

        if (response.getBody() != null) {
            return response.getBody();
        } else {
            throw new RuntimeException();
        }
    }
}
