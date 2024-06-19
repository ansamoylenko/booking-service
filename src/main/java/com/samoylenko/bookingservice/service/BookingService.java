package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.config.ServiceProperties;
import com.samoylenko.bookingservice.model.dto.booking.BookingCreateDto;
import com.samoylenko.bookingservice.model.dto.booking.BookingDto;
import com.samoylenko.bookingservice.model.dto.booking.BookingInfo;
import com.samoylenko.bookingservice.model.dto.booking.CompositeBookingDto;
import com.samoylenko.bookingservice.model.dto.client.ClientDto;
import com.samoylenko.bookingservice.model.dto.payment.PaymentCreateDto;
import com.samoylenko.bookingservice.model.dto.payment.PaymentDto;
import com.samoylenko.bookingservice.model.dto.request.BookingRequest;
import com.samoylenko.bookingservice.model.entity.BookingEntity;
import com.samoylenko.bookingservice.model.exception.BookingNotFoundException;
import com.samoylenko.bookingservice.model.spec.BookingSpecification;
import com.samoylenko.bookingservice.model.status.BookingStatus;
import com.samoylenko.bookingservice.model.status.PaymentStatus;
import com.samoylenko.bookingservice.repository.BookingRepository;
import com.samoylenko.bookingservice.repository.ClientRepository;
import com.samoylenko.bookingservice.repository.PaymentRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static com.samoylenko.bookingservice.model.spec.BookingSpecification.*;
import static java.time.Duration.between;
import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.MINUTES;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class BookingService {
    private final ServiceProperties properties;
    private final WalkService walkService;
    private final PaymentService paymentService;
    private final ClientService clientService;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final ClientRepository clientRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public CompositeBookingDto create(@Valid BookingCreateDto dto) {
        log.info("Creating booking: {}", dto);
        walkService.decreaseAvailablePlaces(dto.getWalkId(), dto.getNumberOfPeople());
        var walk = walkService.getWalkEntityById(dto.getWalkId());
        var client = clientService.create(dto.getClient());
        var endTime = now().plus(properties.getBookingLifetime(), MINUTES);
        var booking = bookingRepository.save(BookingEntity.builder()
                .walk(walk)
                .status(BookingStatus.ACTIVE)
                .client(clientRepository.findById(client.getId()).orElse(null))
                .numberOfPeople(dto.getNumberOfPeople())
                .comment(dto.getBookingInfo().getComment())
                .hasChildren(dto.getBookingInfo().isHasChildren())
                .agreementConfirmed(dto.getBookingInfo().isAgreementConfirmed())
                .endTime(endTime)
                .build());
        booking = bookingRepository.save(booking);
        log.info("Booking created with id: {}, status: {}", booking.getId(), booking.getStatus());
        return getBookingById(booking.getId());
    }

    @Transactional
    public CompositeBookingDto createInvoice(@NotBlank String id, String voucher) {
        log.info("Creating invoice for booking: {}", id);
        try {
            var booking = getBookingEntity(id);
            var walk = booking.getWalk();
            log.info("Walk:  {}", walk.getId());
            var client = modelMapper.map(booking.getClient(), ClientDto.class);
            log.info("Client: {}", client);

            if (booking.getPayment() != null) {
                log.info("Attempting to create second invoice for booking: {}", booking.getId());
                return getBookingById(booking.getId());
            }

            var payment = paymentService.createInvoice(PaymentCreateDto.builder()
                    .orderId(booking.getId())
                    .routeId(walk.getRoute().getId())
                    .serviceName(walk.getRoute().getServiceName())
                    .amount(booking.getNumberOfPeople())
                    .priceForOne(walk.getPriceForOne())
                    .voucher(voucher)
                    .client(client)
                    .expiryTime(booking.getEndTime())
                    .build());
            log.info("Invoice created: {}", payment);
            booking.setPayment(paymentRepository.findById(payment.getId()).orElse(null));
            if (payment.getStatus().equals(PaymentStatus.PAID)) {
                booking.setStatus(BookingStatus.PAID);
            } else {
                booking.setStatus(BookingStatus.WAITING_FOR_PAYMENT);
            }
            bookingRepository.save(booking);
            log.info("Updating status for booking:  {}, status:  {}", booking.getId(), booking.getStatus());
            return getBookingById(booking.getId());
        } catch (BookingNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Page<BookingDto> getBookings(BookingRequest request) {
        var spec = BookingSpecification
                .withClientId(request.getClientId())
                .and(withPhone(request.getClientPhone()))
                .and(withEmail(request.getClientEmail()))
                .and(withWalk(request.getWalkId()))
                .and(withStatus(request.getStatus()));
        var pageRequest = request.getPageRequest();

        return bookingRepository.findAll(spec, pageRequest)
                .map(booking -> modelMapper.map(booking, BookingDto.class));
    }

    public List<BookingDto> getBookingList(BookingRequest request) {
        var spec = BookingSpecification
                .withClientId(request.getClientId())
                .and(withPhone(request.getClientPhone()))
                .and(withEmail(request.getClientEmail()))
                .and(withWalk(request.getWalkId()))
                .and(withStatus(request.getStatus()));
        return bookingRepository.findAll(spec).stream()
                .map(booking -> modelMapper.map(booking, BookingDto.class))
                .toList();
    }

    public CompositeBookingDto getBookingById(@NotBlank String id) {
        var booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        PaymentDto payment = null;
        if (booking.getPayment() != null) {
            payment = paymentService.getPaymentForUser(booking.getPayment().getId());
        }

        var client = modelMapper.map(booking.getClient(), ClientDto.class);
        var bookingInfo = modelMapper.map(booking, BookingInfo.class);
        var timeLeft = between(now(), booking.getEndTime()).compareTo(Duration.ofMinutes(0)) > 0 ?
                between(Instant.now(), booking.getEndTime()) :
                Duration.ofMinutes(0);

        return CompositeBookingDto.builder()
                .id(booking.getId())
                .createdDate(booking.getCreatedDate())
                .lastModifiedDate(booking.getLastModifiedDate())
                .status(booking.getStatus())
                .walkId(booking.getWalk().getId())
                .numberOfPeople(booking.getNumberOfPeople())
                .payment(payment)
                .client(client)
                .info(bookingInfo)
                .timeLeft(timeLeft)
                .build();
    }

    public void deleteOrder(String id) {
    }

    public CompositeBookingDto updateOrder(String id, CompositeBookingDto dto) {
        return null;
    }

    private BookingEntity getBookingEntity(@NotBlank String id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
    }

    @Transactional
    public void setExpired(@NotBlank String id) {
        log.info("Setting expired for booking:  {}", id);
        var booking = getBookingEntity(id);
        booking.setStatus(BookingStatus.EXPIRED);
        bookingRepository.save(booking);
        walkService.increaseAvailablePlaces(booking.getWalk().getId(), booking.getNumberOfPeople());
    }

    @Transactional
    public CompositeBookingDto setPaid(String bookingId) {
        log.info("Setting paid for booking:  {}", bookingId);
        var booking = getBookingEntity(bookingId);
        booking.setStatus(BookingStatus.PAID);
        bookingRepository.save(booking);
        return getBookingById(booking.getId());
    }
}
