package com.samoylenko.bookingservice.repository;

import com.samoylenko.bookingservice.model.entity.*;
import com.samoylenko.bookingservice.model.spec.BookingSpecification;
import com.samoylenko.bookingservice.model.status.BookingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingRepositoryTest extends BaseRepositoryTest {
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private WalkRepository walkRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void setUp() {
        routeRepository.deleteAll();
        walkRepository.deleteAll();
        bookingRepository.deleteAll();
        contactRepository.deleteAll();
        paymentRepository.deleteAll();
    }

    @Test
    void save_shouldSaveBooking() {
        var savedRoute = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var savedWalk = walkRepository.save(DefaultWalkEntityBuilder.of().withRoute(savedRoute).build());
        var savedContact = contactRepository.save(DefaultContactEntityBuilder.of().build());
        var booking = DefaultBookingEntityBuilder.of()
                .withWalk(savedWalk)
                .withContact(savedContact)
                .build();

        var savedBooking = bookingRepository.save(booking);

        assertThat(savedBooking).isNotNull();
        assertThat(savedBooking.getId()).isNotNull();
        assertThat(savedBooking.getCreatedDate()).isNotNull();
        assertThat(savedBooking.getLastModifiedDate()).isNotNull();
        assertThat(savedBooking.getVersion()).isNotNull();
        assertThat(savedBooking.getWalk()).isNotNull();
        assertThat(savedBooking.getContact()).isNotNull();
    }

    @Test
    void findById_shouldReturnBooking() {
        var savedRoute = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var savedWalk = walkRepository.save(DefaultWalkEntityBuilder.of().withRoute(savedRoute).build());
        var savedContact = contactRepository.save(DefaultContactEntityBuilder.of().build());
        var booking = DefaultBookingEntityBuilder.of()
                .withWalk(savedWalk)
                .withContact(savedContact)
                .build();
        var savedBooking = bookingRepository.save(booking);

        var foundBooking = bookingRepository.findById(savedBooking.getId());

        assertThat(foundBooking).isNotEmpty();
        assertThat(foundBooking.get().getId()).isEqualTo(savedBooking.getId());
        assertThat(foundBooking.get().getContact()).isNotNull();
        assertThat(foundBooking.get().getContact()).isEqualTo(savedContact);
        assertThat(foundBooking.get().getWalk()).isNotNull();
        assertThat(foundBooking.get().getWalk()).isEqualTo(savedWalk);
    }

    @Test
    void removeById_shouldRemoveBookingAndContactAndPayment() {
        var savedContact = contactRepository.save(DefaultContactEntityBuilder.of().build());
        var savedPayment = paymentRepository.save(DefaultPaymentEntityBuilder.of().build());
        var booking = DefaultBookingEntityBuilder.of()
                .withContact(savedContact)
                .withPayment(savedPayment)
                .build();
        var savedBooking = bookingRepository.save(booking);

        bookingRepository.deleteById(savedBooking.getId());

        var foundBooking = bookingRepository.findById(savedBooking.getId());
        var foundContact = contactRepository.findById(savedContact.getId());
        var foundPayment = paymentRepository.findById(savedPayment.getId());
        assertThat(foundBooking).isEmpty();
        assertThat(foundContact).isEmpty();
        assertThat(foundPayment).isEmpty();
    }

    @Test
    void findAll_shouldReturnBookingFilterByStatus() {
        var booking1 = DefaultBookingEntityBuilder.of()
                .withStatus(BookingStatus.COMPLETED)
                .build();
        var booking2 = DefaultBookingEntityBuilder.of()
                .withStatus(BookingStatus.CANCELED)
                .build();
        var booking3 = DefaultBookingEntityBuilder.of()
                .withStatus(BookingStatus.COMPLETED)
                .build();
        bookingRepository.saveAll(List.of(booking1, booking2, booking3));
        var spec = BookingSpecification.withStatus(BookingStatus.COMPLETED);

        var foundBookings = bookingRepository.findAll(spec);
        assertThat(foundBookings).hasSize(2);
        assertThat(foundBookings).contains(booking1, booking3);
    }

    @Test
    void findAll_shouldReturnBookingFilteredByWalk() {
        var savedRoute = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var walk = DefaultWalkEntityBuilder.of().withRoute(savedRoute).build();
        var otherWalk = DefaultWalkEntityBuilder.of().withRoute(savedRoute).build();
        var savedWalks = walkRepository.saveAll(List.of(walk, otherWalk));
        var booking1 = DefaultBookingEntityBuilder.of().withWalk(walk).build();
        var booking2 = DefaultBookingEntityBuilder.of().withWalk(otherWalk).build();
        var booking3 = DefaultBookingEntityBuilder.of().withWalk(walk).build();
        bookingRepository.saveAll(List.of(booking1, booking2, booking3));
        var spec = BookingSpecification.withWalk(savedWalks.get(0).getId());

        var foundBookings = bookingRepository.findAll(spec);

        assertThat(foundBookings).hasSize(2);
        assertThat(foundBookings).contains(booking1, booking3);
    }

    @Test
    void findAll_shouldReturnBookingFilteredByPhone() {
        var contact1 = DefaultContactEntityBuilder.of().withPhone("79999999999").build();
        var contact2 = DefaultContactEntityBuilder.of().withPhone("79999999998").build();
        contactRepository.saveAll(List.of(contact1, contact2));
        var booking1 = DefaultBookingEntityBuilder.of().withContact(contact1).build();
        var booking2 = DefaultBookingEntityBuilder.of().withContact(contact2).build();
        bookingRepository.saveAll(List.of(booking1, booking2));
        var spec = BookingSpecification.withPhone("79999999999");

        var foundBookings = bookingRepository.findAll(spec);

        assertThat(foundBookings).hasSize(1);
        assertThat(foundBookings).contains(booking1);
    }

    @Test
    void findAll_shouldReturnBookingFilteredByEmail() {
        var contact1 = DefaultContactEntityBuilder.of().withEmail("vasya@gmail.com").build();
        var contact2 = DefaultContactEntityBuilder.of().withEmail("vasya@gmail.com").build();
        var contact3 = DefaultContactEntityBuilder.of().withEmail("kolya@gmail.com").build();
        contactRepository.saveAll(List.of(contact1, contact2, contact3));
        var booking1 = DefaultBookingEntityBuilder.of().withContact(contact1).build();
        var booking2 = DefaultBookingEntityBuilder.of().withContact(contact2).build();
        var booking3 = DefaultBookingEntityBuilder.of().withContact(contact3).build();
        bookingRepository.saveAll(List.of(booking1, booking2, booking3));
        var spec = BookingSpecification.withEmail("vasya@gmail.com");

        var foundBookings = bookingRepository.findAll(spec);

        assertThat(foundBookings).hasSize(2);
        assertThat(foundBookings).contains(booking1);
    }
}
