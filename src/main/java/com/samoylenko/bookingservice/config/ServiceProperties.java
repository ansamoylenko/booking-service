package com.samoylenko.bookingservice.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static org.springframework.util.Assert.notNull;

@Slf4j
@Getter
@Setter
@ConfigurationProperties(prefix = "service")
public class ServiceProperties {
    private Integer bookingLifetime;
    private Integer bookingCheckPeriodImMs;
    private Integer invoiceCheckPeriodImMs;

    @PostConstruct
    public void afterPropertiesSet() {
        log.info("Scanning service properties...");

        notNull(bookingLifetime, "bookingLifetime must be set");
        notNull(bookingCheckPeriodImMs, "bookingCheckPeriodImMs must be set");
        notNull(invoiceCheckPeriodImMs, "invoiceCheckPeriodImMs must be set");
        log.info("booking lifetime: " + bookingLifetime);
        log.info("bookingCheckPeriodImMs: " + bookingCheckPeriodImMs);
        log.info("invoiceCheckPeriodImMs:  " + invoiceCheckPeriodImMs);
    }
}
