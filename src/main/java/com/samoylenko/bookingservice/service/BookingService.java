package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.model.dto.booking.BookingCreateDto;
import com.samoylenko.bookingservice.model.dto.booking.BookingDto;
import com.samoylenko.bookingservice.model.dto.booking.BookingInfo;
import com.samoylenko.bookingservice.model.dto.booking.CompositeBookingDto;
import com.samoylenko.bookingservice.model.dto.client.ClientDto;
import com.samoylenko.bookingservice.model.dto.payment.PaymentCreateDto;
import com.samoylenko.bookingservice.model.dto.request.BookingRequest;
import com.samoylenko.bookingservice.model.entity.BookingEntity;
import com.samoylenko.bookingservice.model.exception.BookingNotFoundException;
import com.samoylenko.bookingservice.model.spec.BookingSpecification;
import com.samoylenko.bookingservice.model.status.BookingStatus;
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

import static com.samoylenko.bookingservice.model.spec.BookingSpecification.*;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class BookingService {
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
        var booking = bookingRepository.save(BookingEntity.builder()
                .walk(walk)
                .status(BookingStatus.DRAFT)
                .client(clientRepository.findById(client.getId()).get())
                .numberOfPeople(dto.getNumberOfPeople())
                .comment(dto.getBookingInfo().getComment())
                .hasChildren(dto.getBookingInfo().isHasChildren())
                .agreementConfirmed(dto.getBookingInfo().isAgreementConfirmed())
                .build());
        var payment = paymentService.create(PaymentCreateDto.builder()
                .orderId(booking.getId())
                .serviceName(walk.getRoute().getServiceName())
                .amount(dto.getNumberOfPeople())
                .priceForOne(walk.getPriceForOne())
                .promoCode(dto.getBookingInfo().getPromoCode())
                .certificate(dto.getBookingInfo().getCertificate())
                .client(client)
                .build());
        booking.setPayment(paymentRepository.findById(payment.getId()).get());
        booking.setStatus(BookingStatus.WAITING_FOR_PAYMENT);
        booking = bookingRepository.save(booking);
        return getBookingById(booking.getId());
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

    public CompositeBookingDto getBookingById(@NotBlank String id) {
        var booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
        var payment = paymentService.getPaymentForUser(booking.getPayment().getId());
        var client = modelMapper.map(booking.getClient(), ClientDto.class);
        var bookingInfo = modelMapper.map(booking, BookingInfo.class);

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
                .build();
    }

    public void deleteOrder(String id) {
    }

    public CompositeBookingDto updateOrder(String id, CompositeBookingDto dto) {
        return null;
    }

    public BookingEntity getBookingEntityById(@NotBlank String id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
    }
}
