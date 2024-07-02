package com.samoylenko.bookingservice.repository;

import com.samoylenko.bookingservice.model.entity.DefaultBookingEntityBuilder;
import com.samoylenko.bookingservice.model.entity.DefaultClientEntityBuilder;
import com.samoylenko.bookingservice.model.entity.DefaultRouteEntityBuilder;
import com.samoylenko.bookingservice.model.entity.DefaultWalkEntityBuilder;
import com.samoylenko.bookingservice.model.walk.WalkSpecification;
import com.samoylenko.bookingservice.model.walk.WalkStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.time.Instant.now;
import static org.assertj.core.api.Assertions.assertThat;

public class WalkRepositoryTest extends BaseRepositoryTest {
    @Autowired
    private WalkRepository walkRepository;
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ClientRepository clientRepository;

    @BeforeEach
    public void setUp() {
        bookingRepository.deleteAll();
        walkRepository.deleteAll();
        routeRepository.deleteAll();
        employeeRepository.deleteAll();
        clientRepository.deleteAll();
    }

    @Test
    public void save_shouldSaveWalk() {
        var route = DefaultRouteEntityBuilder.of().build();
        var savedRoute = routeRepository.save(route);
        var walk = DefaultWalkEntityBuilder.of().withRoute(savedRoute).build();
        var savedWalk = walkRepository.save(walk);

        assertThat(savedWalk).isNotNull();
        assertThat(savedWalk.getId()).isNotNull();
        assertThat(savedRoute).isNotNull();
        assertThat(savedWalk.getRoute()).isNotNull();
        assertThat(savedWalk.getRoute().getId()).isEqualTo(savedRoute.getId());
    }

    @Test
    public void delete_shouldDeleteWalkIfRouteDeleted() {
        var savedRoute = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var savedWalk1 = walkRepository.save(DefaultWalkEntityBuilder.of().withRoute(savedRoute).build());
        var savedWalk2 = walkRepository.save(DefaultWalkEntityBuilder.of().withRoute(savedRoute).build());
        savedRoute.setWalks(List.of(savedWalk1, savedWalk2));
        routeRepository.save(savedRoute);

        var walksBefore = walkRepository.findAll();
        routeRepository.deleteById(savedRoute.getId());
        var walksAfter = walkRepository.findAll();

        assertThat(walksBefore).hasSize(2);
        assertThat(walksAfter).isEmpty();
    }

    @Test
    public void delete_shouldDeleteAllBookings() {
        var savedRoute = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var savedWalk = walkRepository.save(DefaultWalkEntityBuilder.of().withRoute(savedRoute).build());
        var client = clientRepository.save(DefaultClientEntityBuilder.of().build());
        var savedBooking = bookingRepository.save(DefaultBookingEntityBuilder.of()
                .withClient(client)
                .withWalk(savedWalk)
                .build());

        var bookingsBefore = bookingRepository.findAll();
        walkRepository.deleteById(savedWalk.getId());
        var bookingsAfter = bookingRepository.findAll();

        assertThat(bookingsBefore).hasSize(1);
        assertThat(bookingsAfter).isEmpty();
    }

    @Test
    public void findAll_shouldReturnPageOfWalksFilteredByAvailablePaces() {
        var savedRoute = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var walk1 = walkRepository.save(DefaultWalkEntityBuilder.of()
                .withRoute(savedRoute)
                .withAvailablePlaces(2)
                .withStartTime(now().plus(2, ChronoUnit.HOURS))
                .build());
        var walk2 = walkRepository.save(DefaultWalkEntityBuilder.of().withRoute(savedRoute).withAvailablePlaces(0).build());
        var walk3 = walkRepository.save(DefaultWalkEntityBuilder.of()
                .withRoute(savedRoute)
                .withAvailablePlaces(1)
                .withStartTime(now().plus(1, ChronoUnit.HOURS))
                .build());
        var spec = WalkSpecification.withAvailablePlacesMoreOrEqualTo(1);
        var pageRequest = PageRequest.of(0, 10, Sort.by("startTime").ascending());

        var walks = walkRepository.findAll(spec, pageRequest);

        assertThat(walks).isNotNull();
        assertThat(walks.getTotalElements()).isEqualTo(2);
        assertThat(walks.getContent()).hasSize(2);
        assertThat(walks.getContent()).contains(walk1, walk3);
        assertThat(walks.getContent().get(0).getId()).isEqualTo(walk3.getId());
    }

    @Test
    public void findAll_shouldFindAllWalksFilteredByRoute() {
        var savedRoute = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var otherRoute = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var savedWalk1 = walkRepository.save(DefaultWalkEntityBuilder.of().withRoute(savedRoute).build());
        walkRepository.save(DefaultWalkEntityBuilder.of().withRoute(otherRoute).build());
        var spec = WalkSpecification.withRoute(savedRoute.getId());

        var walks = walkRepository.findAll(spec);

        assertThat(walks).hasSize(1);
        assertThat(walks).contains(savedWalk1);
    }

    @Test
    public void findAll_shouldFindAllFilteredByStartTime() {
        var savedRoute = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var savedWalk1 = walkRepository.save(DefaultWalkEntityBuilder.of()
                .withStartTime(now().minus(3, ChronoUnit.DAYS))
                .withRoute(savedRoute)
                .build());
        var savedWalk2 = walkRepository.save(DefaultWalkEntityBuilder.of()
                .withStartTime(now().minus(1, ChronoUnit.DAYS))
                .withRoute(savedRoute)
                .build());
        var savedWalk3 = walkRepository.save(DefaultWalkEntityBuilder.of()
                .withStartTime(now().plus(1, ChronoUnit.DAYS))
                .withRoute(savedRoute)
                .build());
        var spec = WalkSpecification
                .startTimeAfter(now().minus(2, ChronoUnit.DAYS))
                .and(WalkSpecification.startTimeBefore(now().plus(2, ChronoUnit.DAYS)));

        var walks = walkRepository.findAll(spec);

        assertThat(walks).hasSize(2);
        assertThat(walks).contains(savedWalk2, savedWalk3);
    }

    @Test
    public void findAll_shouldFindAllFilteredByStatus() {
        var savedRoute = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var savedWalk1 = walkRepository.save(DefaultWalkEntityBuilder.of()
                .withStatus(WalkStatus.BOOKING_FINISHED)
                .withRoute(savedRoute)
                .build());
        var savedWalk2 = walkRepository.save(DefaultWalkEntityBuilder.of()
                .withStatus(WalkStatus.BOOKING_IN_PROGRESS)
                .withRoute(savedRoute)
                .build());
        var savedWalk3 = walkRepository.save(DefaultWalkEntityBuilder.of()
                .withStatus(WalkStatus.BOOKING_FINISHED)
                .withRoute(savedRoute)
                .build());
        var spec = WalkSpecification.withStatus(WalkStatus.BOOKING_IN_PROGRESS);

        var walks = walkRepository.findAll(spec);

        assertThat(walks).hasSize(1);
        assertThat(walks).contains(savedWalk2);
    }

    @Test
    public void findAll_shouldFindAllFilteredByReservedPlaces() {
        var savedRoute = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var savedWalk1 = walkRepository.save(DefaultWalkEntityBuilder.of()
                .withRoute(savedRoute)
                .withReservedPlaces(20)
                .build());
        var savedWalk2 = walkRepository.save(DefaultWalkEntityBuilder.of()
                .withRoute(savedRoute)
                .withReservedPlaces(10)
                .build());
        var savedWalk3 = walkRepository.save(DefaultWalkEntityBuilder.of()
                .withRoute(savedRoute)
                .withReservedPlaces(4)
                .build());
        var spec = WalkSpecification.withReservedPlacesLessOrEqualTo(20)
                .and(WalkSpecification.withReservedPlacesMoreOrEqualTo(5));

        var walks = walkRepository.findAll(spec);

        assertThat(walks).hasSize(2);
        assertThat(walks).contains(savedWalk1, savedWalk2);
    }


}
