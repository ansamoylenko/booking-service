package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.model.dto.booking.BookingCreateDto;
import com.samoylenko.bookingservice.model.dto.booking.BookingInfo;
import com.samoylenko.bookingservice.model.dto.client.ClientCreateDto;
import com.samoylenko.bookingservice.model.dto.payment.InvoiceCreateDto;
import com.samoylenko.bookingservice.model.dto.payment.paykeeper.InvoiceResponse;
import com.samoylenko.bookingservice.model.dto.request.BookingRequest;
import com.samoylenko.bookingservice.model.entity.DefaultBookingEntityBuilder;
import com.samoylenko.bookingservice.model.entity.DefaultClientEntityBuilder;
import com.samoylenko.bookingservice.model.entity.DefaultRouteEntityBuilder;
import com.samoylenko.bookingservice.model.entity.DefaultWalkEntityBuilder;
import com.samoylenko.bookingservice.model.exception.LimitExceededException;
import com.samoylenko.bookingservice.model.status.BookingStatus;
import com.samoylenko.bookingservice.model.status.PaymentStatus;
import com.samoylenko.bookingservice.repository.*;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestConstructor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class BookingServiceTest extends BaseServiceTest {
    private final BookingService bookingService;

    @Autowired
    private ModelMapper modelMapper;
    @MockBean
    private PayKeeperClient payKeeperClient;

    public BookingServiceTest(BookingService bookingService, WalkRepository walkRepository, RouteRepository routeRepository, EmployeeRepository employeeRepository, BookingRepository bookingRepository, ClientRepository clientRepository, PaymentRepository paymentRepository) {
        super(walkRepository, routeRepository, employeeRepository, bookingRepository, clientRepository, paymentRepository);
        this.bookingService = bookingService;
    }

    @Test
    public void create_shouldReturnCreatedBooking() {
        var route = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var walk = walkRepository.save(DefaultWalkEntityBuilder.of().withRoute(route).build());
        var client = modelMapper.map(DefaultClientEntityBuilder.of().build(), ClientCreateDto.class);
        var bookingInfo = BookingInfo.builder().agreementConfirmed(true).hasChildren(false).build();
        var createDto = BookingCreateDto.builder()
                .walkId(walk.getId())
                .numberOfPeople(2)
                .client(client)
                .bookingInfo(bookingInfo)
                .build();

        var created = bookingService.create(createDto);

        assertThat(created).isNotNull();
        assertThat(created.getStatus()).isEqualTo(BookingStatus.ACTIVE);
        assertThat(created.getWalkId()).isEqualTo(walk.getId());
        assertThat(created.getNumberOfPeople()).isEqualTo(createDto.getNumberOfPeople());
        assertThat(created.getInfo()).isNotNull();
        assertThat(created.getClient()).isNotNull();
        assertThat(created.getPayment()).isNull();
        var updatedWalk = walkRepository.findById(created.getWalkId());
        assertThat(updatedWalk).isNotEmpty();
        assertThat(updatedWalk.get().getAvailablePlaces()).isEqualTo(walk.getAvailablePlaces() - createDto.getNumberOfPeople());
    }

    @Test
    public void create_withEmptyAvailablePlaces_shouldThrowRuntimeException() {
        var route = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var walk = walkRepository.save(DefaultWalkEntityBuilder.of()
                .withAvailablePlaces(0)
                .withRoute(route)
                .build());
        var contact = modelMapper.map(DefaultClientEntityBuilder.of().build(), ClientCreateDto.class);
        var bookingInfo = BookingInfo.builder().agreementConfirmed(true).hasChildren(false).build();
        var createDto = BookingCreateDto.builder()
                .walkId(walk.getId())
                .numberOfPeople(2)
                .client(contact)
                .bookingInfo(bookingInfo)
                .build();

        assertThatThrownBy(() -> bookingService.create(createDto))
                .isInstanceOf(LimitExceededException.class);
    }

    @Test
    public void createInvoice_shouldReturnUpdatedBooking() {
        var route = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var walk = walkRepository.save(DefaultWalkEntityBuilder.of().withRoute(route).build());
        var client = clientRepository.save(DefaultClientEntityBuilder.of().build());
        var booking = bookingRepository.save(DefaultBookingEntityBuilder.of()
                .withClient(client)
                .withWalk(walk).build());
        when(payKeeperClient.getToken()).thenReturn("token");
        when(payKeeperClient.createInvoice(any(InvoiceCreateDto.class)))
                .thenReturn(new InvoiceResponse("invoiceId", "invoiceUrl"));

        var updated = bookingService.createInvoice(booking.getId(), null);

        assertThat(updated).isNotNull();
        assertThat(updated.getWalkId()).isEqualTo(walk.getId());
        assertThat(updated.getStatus()).isEqualTo(BookingStatus.WAITING_FOR_PAYMENT);
        assertThat(updated.getPayment()).isNotNull();
        assertThat(updated.getPayment().getInvoiceUrl()).isEqualTo("invoiceUrl");
        assertThat(updated.getPayment().getStatus()).isEqualTo(PaymentStatus.PENDING);
    }

    @Test
    public void createInvoice_withNotExistBooking_shouldThrowIllegalArgumentException() {
        assertThatThrownBy(() -> bookingService.createInvoice("notExistBooking", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void createInvoice_withExistingPayment_shouldReturnBookingWithFirstPayment() {
        var route = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var walk = walkRepository.save(DefaultWalkEntityBuilder.of().withRoute(route).build());
        var client = clientRepository.save(DefaultClientEntityBuilder.of().build());
        var booking = bookingRepository.save(DefaultBookingEntityBuilder.of()
                .withClient(client)
                .withWalk(walk).build());
        when(payKeeperClient.getToken()).thenReturn("token");
        when(payKeeperClient.createInvoice(any(InvoiceCreateDto.class)))
                .thenReturn(new InvoiceResponse("invoiceId", "invoiceUrl"));
        var first = bookingService.createInvoice(booking.getId(), null);

        var second = bookingService.createInvoice(booking.getId(), null);

        assertThat(first).isNotNull();
        assertThat(second).isNotNull();
        assertThat(second.getPayment()).isNotNull();
        assertThat(second.getPayment().getId()).isEqualTo(first.getPayment().getId());
    }


    @Test
    public void getBookings_withFilterByStatus_shouldReturnAllBookingDto() {
        var client = clientRepository.save(DefaultClientEntityBuilder.of().build());
        var booking1 = bookingRepository.save(DefaultBookingEntityBuilder.of()
                .withStatus(BookingStatus.WAITING_FOR_PAYMENT)
                .withClient(client)
                .build());
        var booking2 = bookingRepository.save(DefaultBookingEntityBuilder.of()
                .withStatus(BookingStatus.COMPLETED)
                .withClient(client)
                .build());
        var booking3 = bookingRepository.save(DefaultBookingEntityBuilder.of()
                .withStatus(BookingStatus.COMPLETED)
                .withClient(client)
                .build());
        var request = BookingRequest.of().withStatus(List.of(BookingStatus.COMPLETED));

        var found = bookingService.getBookings(request);

        assertThat(found.getTotalElements()).isEqualTo(2);
        assertThat(found.getContent().get(0).getId()).isEqualTo(booking3.getId());
        assertThat(found.getContent().get(1).getId()).isEqualTo(booking2.getId());
    }

    @Test
    public void getBookings_withFilterByClientId_shouldReturnAllFilteredBookings() {
        var client1 = clientRepository.save(DefaultClientEntityBuilder.of().build());
        var client2 = clientRepository.save(DefaultClientEntityBuilder.of().build());
        var booking1 = bookingRepository.save(DefaultBookingEntityBuilder.of()
                .withClient(client1)
                .build());
        var booking2 = bookingRepository.save(DefaultBookingEntityBuilder.of()
                .withClient(client2)
                .build());

        var request = BookingRequest.of().withClientId(client2.getId());

        var found = bookingService.getBookings(request);

        assertThat(found.getTotalElements()).isEqualTo(1);
        assertThat(found.getContent().get(0).getId()).isEqualTo(booking2.getId());
    }

    @Test
    public void getBookings_withFilterByWalkId_shouldReturnAllFilteredBookings() {
        var route = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var walk = walkRepository.save(DefaultWalkEntityBuilder.of().withRoute(route).build());
        var otherWalk = walkRepository.save(DefaultWalkEntityBuilder.of().withRoute(route).build());
        var client = clientRepository.save(DefaultClientEntityBuilder.of().build());
        var booking1 = bookingRepository.save(DefaultBookingEntityBuilder.of()
                .withWalk(walk)
                .withClient(client)
                .build());
        var booking2 = bookingRepository.save(DefaultBookingEntityBuilder.of()
                .withWalk(otherWalk)
                .withClient(client)
                .build());
        var booking3 = bookingRepository.save(DefaultBookingEntityBuilder.of()
                .withWalk(walk)
                .withClient(client)
                .build());
        var request = BookingRequest.of().withWalkId(walk.getId());

        var found = bookingService.getBookings(request);

        assertThat(found.getTotalElements()).isEqualTo(2);
        assertThat(found.getContent().get(0).getId()).isEqualTo(booking3.getId());
        assertThat(found.getContent().get(1).getId()).isEqualTo(booking1.getId());
    }

    @Test
    public void getBookings_withFilterByClientEmail_shouldReturnAllFilteredBookings() {
        var client1 = clientRepository.save(DefaultClientEntityBuilder.of().withEmail("target@gmail.com").build());
        var client2 = clientRepository.save(DefaultClientEntityBuilder.of().build());
        var client3 = clientRepository.save(DefaultClientEntityBuilder.of().withEmail("target@gmail.com").build());
        var client4 = clientRepository.save(DefaultClientEntityBuilder.of().build());
        var booking1 = bookingRepository.save(DefaultBookingEntityBuilder.of()
                .withClient(client1)
                .build());
        var booking2 = bookingRepository.save(DefaultBookingEntityBuilder.of()
                .withClient(client2)
                .build());
        var booking3 = bookingRepository.save(DefaultBookingEntityBuilder.of()
                .withClient(client3)
                .build());
        var booking4 = bookingRepository.save(DefaultBookingEntityBuilder.of()
                .withClient(client4)
                .build());

        var request = BookingRequest.of().withClientEmail("target@gmail.com");

        var found = bookingService.getBookings(request);

        assertThat(found.getTotalElements()).isEqualTo(2);
        assertThat(found.getContent().get(0).getId()).isEqualTo(booking3.getId());
        assertThat(found.getContent().get(1).getId()).isEqualTo(booking1.getId());
    }

    @Test
    public void getBookings_withFilterByClientPhone_shouldReturnAllFilteredBookings() {
        var client1 = clientRepository.save(DefaultClientEntityBuilder.of().withPhone("71234567890").build());
        var client2 = clientRepository.save(DefaultClientEntityBuilder.of().build());
        var client3 = clientRepository.save(DefaultClientEntityBuilder.of().build());
        var booking1 = bookingRepository.save(DefaultBookingEntityBuilder.of()
                .withClient(client1)
                .build());
        var booking2 = bookingRepository.save(DefaultBookingEntityBuilder.of()
                .withClient(client2)
                .build());
        var booking3 = bookingRepository.save(DefaultBookingEntityBuilder.of()
                .withClient(client3)
                .build());
        var booking4 = bookingRepository.save(DefaultBookingEntityBuilder.of()
                .withClient(client1)
                .build());

        var request = BookingRequest.of().withClientPhone("71234567890");

        var found = bookingService.getBookings(request);

        assertThat(found.getTotalElements()).isEqualTo(2);
        assertThat(found.getContent().get(0).getId()).isEqualTo(booking4.getId());
        assertThat(found.getContent().get(1).getId()).isEqualTo(booking1.getId());
    }

    @Test
    public void getBookingById_shouldReturnCompositeBookingDto() {
        var route = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var client = clientRepository.save(DefaultClientEntityBuilder.of().build());
        var walk = walkRepository.save(DefaultWalkEntityBuilder.of().withRoute(route).build());
        var booking = bookingRepository.save(DefaultBookingEntityBuilder.of()
                .withClient(client)
                .withWalk(walk)
                .build());

        var found = bookingService.getBookingById(booking.getId());

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(booking.getId());
    }
}
