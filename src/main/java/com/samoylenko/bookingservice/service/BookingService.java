package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.config.ServiceProperties;
import com.samoylenko.bookingservice.model.booking.*;
import com.samoylenko.bookingservice.model.client.ClientDto;
import com.samoylenko.bookingservice.model.exception.EntityCreateException;
import com.samoylenko.bookingservice.model.exception.EntityNotFoundException;
import com.samoylenko.bookingservice.model.payment.PaymentCreateDto;
import com.samoylenko.bookingservice.model.payment.PaymentDto;
import com.samoylenko.bookingservice.model.payment.PaymentStatus;
import com.samoylenko.bookingservice.repository.BookingRepository;
import com.samoylenko.bookingservice.repository.PaymentRepository;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static com.samoylenko.bookingservice.model.booking.BookingSpecification.*;
import static com.samoylenko.bookingservice.model.exception.EntityType.BOOKING;
import static com.samoylenko.bookingservice.model.exception.EntityType.PAYMENT;
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
    private final EmployeeService employeeService;
    private final ModelMapper mapper;

    @PostConstruct
    public void init() {
        mapper.createTypeMap(BookingEntity.class, BookingDto.class)
                .addMappings(mapper -> mapper.map(src -> src.getClient().getId(), BookingDto::setClientId))
                .addMappings(mapper -> mapper.map(src -> src.getClient().getFirstName(), BookingDto::setClientFirstName))
                .addMappings(mapper -> mapper.map(src -> src.getClient().getLastName(), BookingDto::setClientLastName))
                .addMappings(mapper -> mapper.map(src -> src.getWalk().getId(), BookingDto::setWalkId))
                .addMappings(mapper -> mapper.map(src -> src.getWalk().getStartTime(), BookingDto::setWalkStartTime))
                .addMappings(mapper -> mapper.map(src -> src.getWalk().getRoute().getName(), BookingDto::setRouteName))
                .addMappings(mapper -> mapper.map(src -> src.getPayment().getTotalCost(), BookingDto::setTotalCost));

        mapper.createTypeMap(BookingEntity.class, CompositeBookingDto.class)
                .addMappings(mapper -> mapper.map(BookingEntity::getId, CompositeBookingDto::setId))
                .addMappings(mapper -> mapper.map(BookingEntity::getCreatedDate, CompositeBookingDto::setCreatedDate))
                .addMappings(mapper -> mapper.map(BookingEntity::getLastModifiedDate, CompositeBookingDto::setLastModifiedDate))
                .addMappings(mapper -> mapper.map(BookingEntity::getStatus, CompositeBookingDto::setStatus))
                .addMappings(mapper -> mapper.map(src -> src.getWalk().getId(), CompositeBookingDto::setStatus))
                .addMappings(mapper -> mapper.map(BookingEntity::getNumberOfPeople, CompositeBookingDto::setNumberOfPeople));
    }

    @Transactional
    public CompositeBookingDto create(@Valid BookingCreateDto dto) {
        try {
            log.info("Creating booking: {}", dto);
            walkService.decreaseAvailablePlaces(dto.getWalkId(), dto.getNumberOfPeople());
            var walk = walkService.getWalkEntityById(dto.getWalkId());
            var client = clientService.createIfNotExist(dto.getClient());
            var endTime = now().plus(properties.getBookingLifetime(), MINUTES);
            var booking = bookingRepository.save(BookingEntity.builder()
                    .walk(walk)
                    .status(BookingStatus.ACTIVE)
                    .client(client)
                    .numberOfPeople(dto.getNumberOfPeople())
                    .comment(dto.getBookingInfo().getComment())
                    .hasChildren(dto.getBookingInfo().isHasChildren())
                    .agreementConfirmed(dto.getBookingInfo().isAgreementConfirmed())
                    .endTime(endTime)
                    .build());
            booking = bookingRepository.save(booking);
            var bookingDto = getBookingForUser(booking.getId());
            log.info("Booking created with id: {}, status: {}", booking.getId(), booking.getStatus());
            return bookingDto;
        } catch (Exception e) {
            throw new EntityCreateException(BOOKING, e);
        }
    }

    @Transactional
    public CompositeBookingDto createInvoice(@NotBlank String id, String voucher) {
        log.info("Creating invoice for booking: {}", id);
        try {
            var booking = getBookingEntity(id);
            var oldStatus = booking.getStatus();
            var walk = booking.getWalk();
            log.info("Walk:  {}", walk.getId());
            var client = mapper.map(booking.getClient(), ClientDto.class);
            log.info("Client: {}", client);

            if (booking.getPayment() != null) {
                log.info("Attempting to create second invoice for booking: {}", booking.getId());
                return getBookingForUser(booking.getId());
            }

            var payment = paymentService.createPaymentDocument(PaymentCreateDto.builder()
                    .bookingId(booking.getId())
                    .routeId(walk.getRoute().getId())
                    .serviceName(walk.getRoute().getServiceName())
                    .quantity(booking.getNumberOfPeople())
                    .priceForOne(BigDecimal.valueOf(walk.getPriceForOne()))
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
            var bookingDto = getBookingForUser(booking.getId());
            log.info("Updating status for booking {} from {} to {}", booking.getId(), oldStatus, booking.getStatus());
            return bookingDto;
        } catch (Exception e) {
            throw new EntityCreateException(PAYMENT, e);
        }
    }

    @Transactional
    public Page<BookingDto> getBookings(BookingRequest request) {
        var spec = BookingSpecification
                .withClientId(request.getClientId())
                .and(withPhone(request.getClientPhone()))
                .and(withEmail(request.getClientEmail()))
                .and(withWalk(request.getWalkId()))
                .and(withStatus(request.getStatus()));
        var pageRequest = request.getPageRequest();

        return bookingRepository.findAll(spec, pageRequest)
                .map(booking -> mapper.map(booking, BookingDto.class));
    }

    @Transactional
    public List<BookingDto> getBookingList(BookingRequest request) {
        var spec = BookingSpecification
                .withClientId(request.getClientId())
                .and(withPhone(request.getClientPhone()))
                .and(withEmail(request.getClientEmail()))
                .and(withWalk(request.getWalkId()))
                .and(withStatus(request.getStatus()));
        return bookingRepository.findAll(spec).stream()
                .map(booking -> mapper.map(booking, BookingDto.class))
                .toList();
    }

    @Transactional
    public CompositeBookingDto getBookingForUser(@NotBlank String id) {
        var booking = getBookingEntity(id);

        PaymentDto payment = null;
        if (booking.getPayment() != null) {
            payment = paymentService.getPaymentById(booking.getPayment().getId());
        }

        var client = mapper.map(booking.getClient(), ClientDto.class);
        var bookingInfo = mapper.map(booking, BookingInfo.class);
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

    @Transactional
    public AdminBookingDto getBookingForAdmin(@NotBlank String id) {
        var bookingEntity = getBookingEntity(id);
        var client = mapper.map(bookingEntity.getClient(), ClientDto.class);
        var bookingInfo = mapper.map(bookingEntity, BookingInfo.class);
        var timeLeft = between(now(), bookingEntity.getEndTime()).compareTo(Duration.ofMinutes(0)) > 0 ?
                between(Instant.now(), bookingEntity.getEndTime()) :
                Duration.ofMinutes(0);
        var dto = mapper.map(bookingEntity, AdminBookingDto.class);
        dto.setClient(client);
        dto.setInfo(bookingInfo);
        dto.setTimeLeft(timeLeft);
        if (bookingEntity.getPayment() != null) {
            var payment = paymentService.getPaymentById(bookingEntity.getPayment().getId());
            dto.setPayment(payment);
        }
        var employees = bookingEntity.getEmployees().stream()
                .map(employeeService::toDto)
                .toList();
        dto.setEmployees(employees);
        return dto;
    }

    public void deleteOrder(String id) {
    }

    private BookingEntity getBookingEntity(@NotBlank String id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(BOOKING, id));
    }

    @Transactional
    public void setStatus(@NotBlank String bookingId, @NotNull BookingStatus status) {
        var booking = getBookingEntity(bookingId);
        var oldStatus = booking.getStatus();
        booking.setStatus(status);
        bookingRepository.save(booking);
        log.info("Updated status of booking {} from {} to {}", bookingId, oldStatus, status);
    }

    @Transactional
    public AdminBookingDto addEmployee(@NotBlank String id, @NotBlank String employeeId) {
        log.info("Adding employee {} to booking {}", employeeId, id);
        var employee = employeeService.getReferenceById(employeeId);
        var booking = getBookingEntity(id);
        booking.getEmployees().add(employee);
        booking = bookingRepository.save(booking);
        return getBookingForAdmin(id);
    }

    @Transactional
    public AdminBookingDto removeEmployee(@NotBlank String id, @NotNull String employeeId) {
        log.info("Removing employee {} from booking {}", employeeId, id);
        var booking = getBookingEntity(id);
        var employee = employeeService.getReferenceById(employeeId);
        booking.getEmployees().remove(employee);
        booking = bookingRepository.save(booking);
        return getBookingForAdmin(id);
    }
}
