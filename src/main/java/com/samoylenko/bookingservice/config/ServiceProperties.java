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
    private String checkWalkToCompleteBooking;
    private String checkWalksToFinish;
    private Integer walkInBookingStatusDeadLine;

    private Integer bookingLifetime;
    private Integer bookingCheckPeriod;

    private Integer invoiceCheckPeriod;

    private boolean groupDiscountEnabled;
    private Integer groupDiscountValuePercent;
    private Integer groupDiscountValueAbsolute;
    private Integer groupDiscountMinPlaces;

    private boolean repeatedBookingDiscountEnabled;
    private Integer repeatedBookingDiscountPercent;
    private Integer repeatedBookingDiscountAbsolute;

    @PostConstruct
    public void afterPropertiesSet() {
        log.info("Scanning service properties...");

        notNull(checkWalkToCompleteBooking, "checkWalkToCompleteBooking must be set");
        notNull(checkWalksToFinish, "checkWalksToFinish must be set");
        notNull(walkInBookingStatusDeadLine, "walkInBookingStatusDeadLine must be set");
        notNull(bookingLifetime, "bookingLifetime must be set");
        notNull(bookingCheckPeriod, "bookingCheckPeriod must be set");
        notNull(invoiceCheckPeriod, "invoiceCheckPeriod must be set");
        notNull(groupDiscountValuePercent, "groupDiscountValuePercent must be set");
        notNull(groupDiscountValueAbsolute, "groupDiscountValueAbsolute must be set");
        notNull(groupDiscountMinPlaces, "groupDiscountMinPlaces must be set");
        notNull(repeatedBookingDiscountPercent, "secondBookingDiscountPercent must be set");
        notNull(repeatedBookingDiscountAbsolute, "secondBookingDiscountAbsolute must be set");

        log.info("checkWalkToCompleteBooking: " + checkWalkToCompleteBooking);
        log.info("checkWalksToFinish: " + checkWalksToFinish);
        log.info("walkInBookingStatusDeadLine: " + walkInBookingStatusDeadLine);

        log.info("booking lifetime: " + bookingLifetime);
        log.info("bookingCheckPeriod: " + bookingCheckPeriod);

        log.info("invoiceCheckPeriod: " + invoiceCheckPeriod);

        log.info("groupDiscountEnabled: " + groupDiscountEnabled);
        log.info("groupDiscountValuePercent: " + groupDiscountValuePercent);
        log.info("groupDiscountValueAbsolute: " + groupDiscountValueAbsolute);
        log.info("groupDiscountMinPlaces: " + groupDiscountMinPlaces);

        log.info("repeatedBookingDiscountEnabled: " + repeatedBookingDiscountEnabled);
        log.info("repeatedBookingDiscountEnabled: " + repeatedBookingDiscountEnabled);
        log.info("repeatedBookingDiscountPercent: " + repeatedBookingDiscountPercent);
        log.info("repeatedBookingDiscountAbsolute: " + repeatedBookingDiscountAbsolute);
    }
}
