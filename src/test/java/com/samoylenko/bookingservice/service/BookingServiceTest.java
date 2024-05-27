package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.model.dto.booking.BookingCreateDto;
import com.samoylenko.bookingservice.model.dto.booking.BookingInfo;
import com.samoylenko.bookingservice.model.dto.client.ClientCreateDto;
import com.samoylenko.bookingservice.model.dto.request.BookingRequest;
import com.samoylenko.bookingservice.model.entity.DefaultBookingEntityBuilder;
import com.samoylenko.bookingservice.model.entity.DefaultContactEntityBuilder;
import com.samoylenko.bookingservice.model.entity.DefaultRouteEntityBuilder;
import com.samoylenko.bookingservice.model.entity.DefaultWalkEntityBuilder;
import com.samoylenko.bookingservice.model.exception.LimitExceededException;
import com.samoylenko.bookingservice.model.status.BookingStatus;
import com.samoylenko.bookingservice.model.status.PaymentStatus;
import com.samoylenko.bookingservice.repository.*;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestConstructor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class BookingServiceTest extends BaseServiceTest {
    private final BookingService bookingService;

    @Autowired
    private ModelMapper modelMapper;

    public BookingServiceTest(BookingService bookingService, WalkRepository walkRepository, RouteRepository routeRepository, EmployeeRepository employeeRepository, BookingRepository bookingRepository, ClientRepository clientRepository, PaymentRepository paymentRepository) {
        super(walkRepository, routeRepository, employeeRepository, bookingRepository, clientRepository, paymentRepository);
        this.bookingService = bookingService;
    }

    @Test
    public void create_shouldReturnCreatedBooking() {
        var route = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var walk = walkRepository.save(DefaultWalkEntityBuilder.of().withRoute(route).build());
        var client = modelMapper.map(DefaultContactEntityBuilder.of().build(), ClientCreateDto.class);
        var bookingInfo = BookingInfo.builder().agreementConfirmed(true).hasChildren(false).build();
        var createDto = BookingCreateDto.builder()
                .walkId(walk.getId())
                .numberOfPeople(2)
                .client(client)
                .bookingInfo(bookingInfo)
                .build();

        var created = bookingService.create(createDto);

        assertThat(created).isNotNull();
        assertThat(created.getStatus()).isEqualTo(BookingStatus.WAITING_FOR_PAYMENT);
        assertThat(created.getWalkId()).isEqualTo(walk.getId());
        assertThat(created.getNumberOfPeople()).isEqualTo(createDto.getNumberOfPeople());
        assertThat(created.getInfo()).isNotNull();
        assertThat(created.getClient()).isNotNull();
        assertThat(created.getPayment()).isNotNull();
        assertThat(created.getPayment().getStatus()).isEqualTo(PaymentStatus.PENDING);
        assertThat(created.getPayment().getTimeToPay()).isNotNull();
        assertThat(created.getPayment().getLink()).isNotNull();
        assertThat(created.getPayment().getTotalCost()).isEqualTo(walk.getPriceForOne() * createDto.getNumberOfPeople());
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
        var contact = modelMapper.map(DefaultContactEntityBuilder.of().build(), ClientCreateDto.class);
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
    public void getBookings_withFilterByStatus_shouldReturnAllBookingDto() {
        var booking1 = bookingRepository.save(DefaultBookingEntityBuilder.of()
                .withStatus(BookingStatus.WAITING_FOR_PAYMENT)
                .build());
        var booking2 = bookingRepository.save(DefaultBookingEntityBuilder.of()
                .withStatus(BookingStatus.COMPLETED)
                .build());
        var booking3 = bookingRepository.save(DefaultBookingEntityBuilder.of()
                .withStatus(BookingStatus.COMPLETED)
                .build());
        var request = BookingRequest.of().withStatus(BookingStatus.COMPLETED);

        var found = bookingService.getBookings(request);

        assertThat(found.getTotalElements()).isEqualTo(2);
        assertThat(found.getContent().get(0).getId()).isEqualTo(booking3.getId());
        assertThat(found.getContent().get(1).getId()).isEqualTo(booking2.getId());
    }

    @Test
    public void getBookings_withFilterByClientId_shouldReturnAllFilteredBookings() {
        var client1 = clientRepository.save(DefaultContactEntityBuilder.of().build());
        var client2 = clientRepository.save(DefaultContactEntityBuilder.of().build());
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
        var booking1 = bookingRepository.save(DefaultBookingEntityBuilder.of()
                .withWalk(walk)
                .build());
        var booking2 = bookingRepository.save(DefaultBookingEntityBuilder.of()
                .withWalk(otherWalk)
                .build());
        var booking3 = bookingRepository.save(DefaultBookingEntityBuilder.of()
                .withWalk(walk)
                .build());
        var request = BookingRequest.of().withWalkId(walk.getId());

        var found = bookingService.getBookings(request);

        assertThat(found.getTotalElements()).isEqualTo(2);
        assertThat(found.getContent().get(0).getId()).isEqualTo(booking3.getId());
        assertThat(found.getContent().get(1).getId()).isEqualTo(booking1.getId());
    }

    @Test
    public void getBookings_withFilterByClientEmail_shouldReturnAllFilteredBookings() {
        var client1 = clientRepository.save(DefaultContactEntityBuilder.of().withEmail("target@gmail.com").build());
        var client2 = clientRepository.save(DefaultContactEntityBuilder.of().build());
        var client3 = clientRepository.save(DefaultContactEntityBuilder.of().withEmail("target@gmail.com").build());
        var client4 = clientRepository.save(DefaultContactEntityBuilder.of().build());
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
        var client1 = clientRepository.save(DefaultContactEntityBuilder.of().withPhone("71234567890").build());
        var client2 = clientRepository.save(DefaultContactEntityBuilder.of().build());
        var client3 = clientRepository.save(DefaultContactEntityBuilder.of().build());
        var client4 = clientRepository.save(DefaultContactEntityBuilder.of().withPhone("71234567890").build());
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

        var request = BookingRequest.of().withClientPhone("71234567890");

        var found = bookingService.getBookings(request);

        assertThat(found.getTotalElements()).isEqualTo(2);
        assertThat(found.getContent().get(0).getId()).isEqualTo(booking4.getId());
        assertThat(found.getContent().get(1).getId()).isEqualTo(booking1.getId());
    }
}
