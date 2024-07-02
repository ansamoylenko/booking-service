package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.config.ServiceProperties;
import com.samoylenko.bookingservice.model.dto.request.BookingRequest;
import com.samoylenko.bookingservice.model.dto.request.WalkRequest;
import com.samoylenko.bookingservice.model.status.BookingStatus;
import com.samoylenko.bookingservice.model.status.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.samoylenko.bookingservice.model.status.WalkStatus.*;
import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
@Service
@AllArgsConstructor
public class Scheduler {
    private final WalkService walkService;
    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final ServiceProperties properties;

    @Scheduled(cron = "${service.checkWalkToCompleteBooking}")
    public void scanWalksToCompleteBookings() {
        log.info("Scanning walks with status: {}", BOOKING_IN_PROGRESS);
        var walkPage = walkService.getWalksForUser(WalkRequest.builder()
                .status(BOOKING_IN_PROGRESS)
                .startBefore(now().plus(properties.getWalkInBookingStatusDeadLine(), MINUTES))
                .build());
        walkPage.get().forEach(walk -> {
            walkService.setStatus(walk.getId(), BOOKING_FINISHED);
        });
    }

    @Scheduled(cron = "${service.checkWalksToFinish}")
    public void scanWalksToFinish() {
        log.info("Scanning walks with status: {}", BOOKING_FINISHED);
        var walkPage = walkService.getWalksForUser(WalkRequest.builder()
                .status(BOOKING_FINISHED)
                .endAfter(now())
                .build());
        walkPage.get().forEach(walk -> {
            walkService.setStatus(walk.getId(), FINISHED);
        });
    }

    @Scheduled(fixedDelayString = "${service.bookingCheckPeriod:5}", timeUnit = SECONDS)
    public void checkBookings() {
        var bookingRequest = BookingRequest.of().withStatus(List.of(BookingStatus.ACTIVE, BookingStatus.WAITING_FOR_PAYMENT));
        var bookings = bookingService.getBookingList(bookingRequest);
        for (var booking : bookings) {
            if (booking.getEndTime() == null || booking.getEndTime().isBefore(now())) {
                bookingService.setStatus(booking.getId(), BookingStatus.EXPIRED);
                if (booking.getStatus().equals(BookingStatus.WAITING_FOR_PAYMENT)) {
                    var payment = bookingService.getBookingById(booking.getId()).getPayment();
                    paymentService.setStatus(payment.getId(), PaymentStatus.EXPIRED);
                }
            }
        }
    }

    @Scheduled(fixedDelayString = "${service.invoiceCheckPeriod:5}", timeUnit = SECONDS)
    public void checkPayments() {
        var bookingRequest = BookingRequest.of().withStatus(List.of(BookingStatus.WAITING_FOR_PAYMENT));
        var bookings = bookingService.getBookingList(bookingRequest);
        for (var booking : bookings) {
            var payment = bookingService.getBookingById(booking.getId()).getPayment();
            var result = paymentService.checkPaymentDocument(payment.getId());
            if (result.equals(PaymentStatus.PAID)) {
                bookingService.setStatus(booking.getId(), BookingStatus.PAID);
                paymentService.setStatus(payment.getId(), PaymentStatus.PAID);
            }
        }
    }
}
