package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.model.entity.*;
import com.samoylenko.bookingservice.model.exception.EntityCreateException;
import com.samoylenko.bookingservice.model.exception.EntityNotFoundException;
import com.samoylenko.bookingservice.model.exception.EntityUpdateException;
import com.samoylenko.bookingservice.model.walk.WalkCreateDto;
import com.samoylenko.bookingservice.model.walk.WalkRequest;
import com.samoylenko.bookingservice.model.walk.WalkStatus;
import com.samoylenko.bookingservice.model.walk.WalkUpdateDto;
import com.samoylenko.bookingservice.repository.*;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.time.Instant.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class WalkServiceTest extends BaseServiceTest {
    private final WalkService walkService;

    public WalkServiceTest(WalkService walkService, WalkRepository walkRepository, RouteRepository routeRepository, EmployeeRepository employeeRepository, BookingRepository bookingRepository, ClientRepository clientRepository, PaymentRepository paymentRepository) {
        super(walkRepository, routeRepository, employeeRepository, bookingRepository, clientRepository, paymentRepository);
        this.walkService = walkService;
    }

    @Test
    public void createWalk_shouldReturnCreatedWalkDto() {
        var savedRoute = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var walkDto = WalkCreateDto.builder()
                .routeId(savedRoute.getId())
                .maxPlaces(20)
                .priceForOne(3500)
                .durationInMinutes(120)
                .startTime(now().plus(1, ChronoUnit.HOURS))
                .build();

        var createdWalk = walkService.createWalk(walkDto);

        assertThat(createdWalk).isNotNull();
        assertThat(createdWalk.getStatus()).isEqualTo(WalkStatus.BOOKING_IN_PROGRESS);
        assertThat(createdWalk.getRoute()).isNotNull();
        assertThat(createdWalk.getRoute().getId()).isEqualTo(savedRoute.getId());
        assertThat(createdWalk.getId()).isNotNull();
        assertThat(createdWalk.getMaxPlaces()).isEqualTo(walkDto.getMaxPlaces());
        assertThat(createdWalk.getReservedPlaces()).isEqualTo(0);
        assertThat(createdWalk.getAvailablePlaces()).isEqualTo(walkDto.getMaxPlaces());
        assertThat(createdWalk.getPriceForOne()).isEqualTo(walkDto.getPriceForOne());
        assertThat(createdWalk.getDuration()).isEqualTo(walkDto.getDurationInMinutes());
        assertThat(createdWalk.getStartTime()).isEqualTo(walkDto.getStartTime());
        assertThat(createdWalk.getEndTime().isAfter(createdWalk.getStartTime())).isTrue();
        assertThat(createdWalk.getBookings()).isEmpty();
    }

    @Test
    public void createWalk_withNotExistingRoute_shouldReturnIllegalArgumentException() {
        var walkDto = WalkCreateDto.builder()
                .routeId("notExistingRoute")
                .maxPlaces(20)
                .priceForOne(3500)
                .durationInMinutes(120)
                .startTime(now().plus(1, ChronoUnit.DAYS))
                .build();

        assertThatThrownBy(() -> walkService.createWalk(walkDto))
                .isInstanceOf(EntityCreateException.class);
    }

    @Test
    public void createWalk_withInvalidData_shouldReturnConstraintViolationException() {
        var walkDto = WalkCreateDto.builder().build();

        assertThatThrownBy(() -> walkService.createWalk(walkDto))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("поле не должно быть пустым");
    }

    @Test
    public void getWalkForUser_shouldReturnWalkUserDto() {
        var savedRoute = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var savedWalk = walkRepository.save(DefaultWalkEntityBuilder.of()
                .withMaxPlaces(20)
                .withPriceForOne(3500)
                .withDuration(120)
                .withAvailablePlaces(20)
                .withRoute(savedRoute).build());

        var found = walkService.getWalkForUser(savedWalk.getId());

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(savedWalk.getId());
        assertThat(found.getAvailablePlaces()).isEqualTo(savedWalk.getMaxPlaces());
        assertThat(found.getPriceForOne()).isEqualTo(savedWalk.getPriceForOne());
        assertThat(found.getStartTime()).isEqualTo(savedWalk.getStartTime());
        assertThat(found.getEndTime()).isNotNull();
        assertThat(found.getDuration()).isEqualTo(savedWalk.getDuration());
        assertThat(found.getRoute()).isNotNull();
        assertThat(found.getRoute().getId()).isEqualTo(savedRoute.getId());
        assertThat(found.getRoute().getName()).isNotNull();
        assertThat(found.getRoute().getDescription()).isNotNull();
    }

    @Test
    public void getWalkForUser_withNotExistingWalk_shouldReturnNotFoundException() {
        assertThatThrownBy(() -> walkService.getWalkForUser("notExistingWalk"))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("notExistingWalk");
    }

    @Test
    public void getWalkForAdmin_shouldReturnWalkAdminDto() {
        var savedRoute = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var employee1 = employeeRepository.save(DefaultEmployeeEntityBuilder.of().build());
        var employee2 = employeeRepository.save(DefaultEmployeeEntityBuilder.of().build());
        var savedWalk = walkRepository.save(DefaultWalkEntityBuilder.of()
                .withMaxPlaces(20)
                .withPriceForOne(3500)
                .withDuration(120)
                .withAvailablePlaces(20)
                .withRoute(savedRoute)
                .build());
        var client = clientRepository.save(DefaultClientEntityBuilder.of().build());
        var booking1 = bookingRepository.save(DefaultBookingEntityBuilder.of()
                .withWalk(savedWalk)
                .withClient(client)
                .build());
        var booking2 = bookingRepository.save(DefaultBookingEntityBuilder.of()
                .withWalk(savedWalk)
                .withClient(client)
                .build());

        var found = walkService.getWalkForAdmin(savedWalk.getId());

        assertThat(found).isNotNull();
        assertThat(found.getCreatedDate()).isNotNull();
        assertThat(found.getLastModifiedDate()).isNotNull();
        assertThat(found.getStatus()).isNotNull();
        assertThat(found.getRoute()).isNotNull();
        assertThat(found.getMaxPlaces()).isEqualTo(savedWalk.getMaxPlaces());
        assertThat(found.getAvailablePlaces()).isEqualTo(savedWalk.getMaxPlaces());
        assertThat(found.getReservedPlaces()).isEqualTo(0);
        assertThat(found.getPriceForOne()).isEqualTo(savedWalk.getPriceForOne());
        assertThat(found.getDuration()).isEqualTo(savedWalk.getDuration());
        assertThat(found.getStartTime()).isEqualTo(savedWalk.getStartTime());
        assertThat(found.getEndTime()).isNotNull();
        assertThat(found.getBookings()).hasSize(2);
        assertThat(found.getBookings().get(0).getId()).isEqualTo(booking1.getId());
        assertThat(found.getBookings().get(1).getId()).isEqualTo(booking2.getId());
    }

    @Test
    public void getAllForUser_shouldReturnWalkUserDtos() {
        var savedRoute = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var otherRoute = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var walk1 = DefaultWalkEntityBuilder.of()
                .withAvailablePlaces(10)
                .withStartTime(Instant.parse("2024-06-01T06:00:00.00Z"))
                .withStatus(WalkStatus.BOOKING_IN_PROGRESS)
                .withRoute(savedRoute);
        var savedWalk1 = walkRepository.save(walk1.build());
        var walk2 = walk1
                .withAvailablePlaces(5)
                .withStartTime(Instant.parse("2024-06-02T06:00:00.00Z"));
        var savedWalk2 = walkRepository.save(walk2.build());
        var walk3 = walk1.withRoute(otherRoute);
        var walk4 = walk1.withAvailablePlaces(1);
        var walk5 = walk1.withStatus(WalkStatus.BOOKING_FINISHED);
        var saved = walkRepository.saveAll(List.of(walk3.build(), walk4.build(), walk5.build()));
        var request = WalkRequest.builder()
                .status(WalkStatus.BOOKING_IN_PROGRESS)
                .availablePlaces(3)
                .routeId(savedRoute.getId())
                .pageNumber(0)
                .pageSize(10)
                .sortBy(WalkRequest.SortField.START_TIME)
                .direction(Sort.Direction.ASC)
                .build();

        var foundPage = walkService.getWalksForUser(request);
        var found = foundPage.getContent();

        assertThat(found).hasSize(2);
        assertThat(found.get(0).getId()).isEqualTo(savedWalk1.getId());
        assertThat(found.get(1).getId()).isEqualTo(savedWalk2.getId());
    }

    @Test
    void markDeleted_shouldSaveWalkAsDeleted() {
        var savedRoute = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var walk = walkRepository.save(DefaultWalkEntityBuilder.of().withRoute(savedRoute).build());

        walkService.markDeleted(walk.getId());

        var found = walkRepository.findById(walk.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo(WalkStatus.DELETED);
    }

    @Test
    void updateWalk_shouldReturnUpdatedWalkDto() {
        var savedRoute = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var savedWalk = walkRepository.save(DefaultWalkEntityBuilder.of()
                .withRoute(savedRoute)
                .withMaxPlaces(10)
                .withReservedPlaces(5)
                .build());
        var updateDto = WalkUpdateDto.builder()
                .status(WalkStatus.BOOKING_FINISHED)
                .durationInMinutes(30)
                .priceForOne(2222)
                .maxPlaces(12)
                .startTime(Instant.parse("2025-06-01T06:00:00.00Z"))
                .build();

        var updatedWalk = walkService.updateWalk(savedWalk.getId(), updateDto);

        assertThat(updatedWalk).isNotNull();
        assertThat(updatedWalk.getStatus()).isEqualTo(updateDto.getStatus());
        assertThat(updatedWalk.getDuration()).isEqualTo(updateDto.getDurationInMinutes());
        assertThat(updatedWalk.getPriceForOne()).isEqualTo(updateDto.getPriceForOne());
        assertThat(updatedWalk.getMaxPlaces()).isEqualTo(12);
        assertThat(updatedWalk.getAvailablePlaces()).isEqualTo(7);
        assertThat(updatedWalk.getReservedPlaces()).isEqualTo(5);
        assertThat(updatedWalk.getStartTime()).isEqualTo(updateDto.getStartTime());
        assertThat(updatedWalk.getRoute()).isNotNull();
        assertThat(updatedWalk.getRoute().getId()).isEqualTo(savedRoute.getId());
    }

    @Test
    void updateWalk_winIncorrectMaxPlaces_shouldReturnEntityUpdateException() {
        var savedRoute = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var savedWalk = walkRepository.save(DefaultWalkEntityBuilder.of()
                .withRoute(savedRoute)
                .withMaxPlaces(10)
                .withReservedPlaces(5)
                .build());
        var updateDto = WalkUpdateDto.builder()
                .maxPlaces(4)
                .build();

        assertThatThrownBy(() -> walkService.updateWalk(savedWalk.getId(), updateDto))
                .isInstanceOf(EntityUpdateException.class);
    }

    @Test
    void updateWalk_withNotExistingWalk_shouldReturnWalkNotFoundException() {
        var updateDto = WalkUpdateDto.builder().build();

        assertThatThrownBy(() -> walkService.updateWalk("notExistingWalk", updateDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("notExistingWalk");
    }

    @Test
    void updateWalk_withInvalidData_shouldReturnConstraintViolationException() {
        var savedRoute = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var savedWalk = walkRepository.save(DefaultWalkEntityBuilder.of().withRoute(savedRoute).build());
        var updateDto = WalkUpdateDto.builder()
                .durationInMinutes(0)
                .priceForOne(-100)
                .maxPlaces(0)
                .startTime(Instant.parse("2022-06-01T06:00:00.00Z"))
                .build();

        assertThatThrownBy(() -> walkService.updateWalk(savedWalk.getId(), updateDto))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("должно быть в будущем")
                .hasMessageContaining("должно быть больше нуля")
                .hasMessageContaining("должно быть больше или равно нулю");
    }

    @Test
    void getWalksForAdmin_shouldReturnWalkAdminDtos() {
        var route1 = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var walk1 = DefaultWalkEntityBuilder.of()
                .withMaxPlaces(10)
                .withReservedPlaces(2)
                .withStartTime(Instant.parse("2024-06-01T06:00:00.00Z"))
                .withStatus(WalkStatus.BOOKING_IN_PROGRESS)
                .withRoute(route1);
        var savedWalk1 = walkRepository.save(walk1.build());
        var walk2 = walk1
                .withMaxPlaces(5)
                .withStartTime(Instant.parse("2024-06-02T06:00:00.00Z"));
        var savedWalk2 = walkRepository.save(walk2.build());

        var request = WalkRequest.builder()
                .status(WalkStatus.BOOKING_IN_PROGRESS)
                .routeId(route1.getId())
                .pageNumber(0)
                .pageSize(10)
                .sortBy(WalkRequest.SortField.START_TIME)
                .direction(Sort.Direction.ASC)
                .build();

        var foundPage = walkService.getWalksForAdmin(request);
        var found = foundPage.getContent();

        assertThat(found).hasSize(2);
        assertThat(found.get(0).getId()).isEqualTo(savedWalk1.getId());
        assertThat(found.get(0).getRouteId()).isNotNull();
        assertThat(found.get(0).getRouteId()).isEqualTo(route1.getId());
        assertThat(found.get(0).getMaxPlaces()).isEqualTo(10);
        assertThat(found.get(0).getReservedPlaces()).isEqualTo(2);
        assertThat(found.get(0).getAvailablePlaces()).isEqualTo(8);
    }

    @Test
    void getWalksForAdmin_withSortByStartTimeDESC_shouldReturnWalkAdminDtos() {
        var route1 = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var walk1 = DefaultWalkEntityBuilder.of()
                .withStartTime(Instant.parse("2024-06-01T06:00:00.00Z"))
                .withRoute(route1);
        var savedWalk1 = walkRepository.save(walk1.build());
        var walk2 = walk1
                .withStartTime(Instant.parse("2024-06-03T06:00:00.00Z"));
        var savedWalk2 = walkRepository.save(walk2.build());
        var savedWalk3 = walkRepository.save(walk1
                .withStartTime(Instant.parse("2024-06-02T06:00:00.00Z"))
                .build()
        );
        var request = WalkRequest.builder()
                .sortBy(WalkRequest.SortField.START_TIME)
                .direction(Sort.Direction.DESC)
                .build();

        var foundPage = walkService.getWalksForAdmin(request);
        var found = foundPage.getContent();

        assertThat(found).hasSize(3);
        assertThat(found.get(0).getId()).isEqualTo(savedWalk2.getId());
    }

    @Test
    void getWalksForAdmin_filteredByStartTime_shouldReturnWalkAdminDtos() {
        var route = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var walkBuilder = DefaultWalkEntityBuilder.of().withRoute(route);
        var walk1 = walkRepository.save(walkBuilder.withStartTime(Instant.parse("2024-06-01T06:00:00.00Z")).build());
        var walk2 = walkRepository.save(walkBuilder.withStartTime(Instant.parse("2024-06-02T08:00:00.00Z")).build());
        var walk3 = walkRepository.save(walkBuilder.withStartTime(Instant.parse("2024-06-03T23:59:00.00Z")).build());
        var request = WalkRequest.builder()
                .startAfter(LocalDate.of(2024, 6, 2).atTime(LocalTime.MIN).toInstant(ZoneOffset.UTC))
                .startBefore(LocalDate.of(2024, 6, 3).atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC))
                .build();
        var foundPage = walkService.getWalksForAdmin(request);
        var walks = foundPage.getContent();
        assertThat(walks).hasSize(2);
    }
}
