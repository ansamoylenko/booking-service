package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.model.dto.request.BookingRequest;
import com.samoylenko.bookingservice.model.status.BookingStatus;
import com.samoylenko.bookingservice.model.status.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

import static java.time.Instant.now;

@Slf4j
@Service
@AllArgsConstructor
public class Scheduler {
    private final BookingService bookingService;
    private final PaymentService paymentService;

    @Scheduled(fixedDelayString = "${service.bookingCheckPeriodImMs:5000}")
    public void checkBookings() {
        var bookingRequest = BookingRequest.of().withStatus(List.of(BookingStatus.ACTIVE, BookingStatus.WAITING_FOR_PAYMENT));
        var bookings = bookingService.getBookingList(bookingRequest);
        for (var booking : bookings) {
            if (booking.getEndTime() == null || booking.getEndTime().isBefore(now())) {
                log.info("Booking {} has been expired. Updating status to EXPIRED", booking.getId());
                bookingService.setExpired(booking.getId());
                if (booking.getStatus().equals(BookingStatus.WAITING_FOR_PAYMENT)) {
                    var payment = bookingService.getBookingById(booking.getId()).getPayment();
                    Assert.notNull(payment, "Payment is null");
                    log.info("Updating payment status to EXPIRED, payment id: {}", booking.getId());
                    paymentService.setStatus(payment.getId(), PaymentStatus.EXPIRED);
                }
            }
        }
    }

    @Scheduled(fixedDelayString = "${service.invoiceCheckPeriodImMs:5000}")
    public void checkPayments() {
        var bookingRequest = BookingRequest.of().withStatus(List.of(BookingStatus.WAITING_FOR_PAYMENT));
        var bookings = bookingService.getBookingList(bookingRequest);
        for (var booking : bookings) {
            var payment = bookingService.getBookingById(booking.getId()).getPayment();
            var result = paymentService.checkInvoice(payment.getId());
            if (result.equals(PaymentStatus.PAID)) {
                log.info("Payment {} has been paid. Updating status to PAID", payment.getId());
                log.info("Updating booking status to PAID, booking id: {}", booking.getId());
                bookingService.setPaid(booking.getId());
                paymentService.setStatus(payment.getId(), PaymentStatus.PAID);
            }
        }
    }
}
