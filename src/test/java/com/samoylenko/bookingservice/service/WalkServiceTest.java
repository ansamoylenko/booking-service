package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.model.dto.request.WalkRequest;
import com.samoylenko.bookingservice.model.dto.walk.WalkCreateDto;
import com.samoylenko.bookingservice.model.dto.walk.WalkUpdateDto;
import com.samoylenko.bookingservice.model.entity.DefaultEmployeeEntityBuilder;
import com.samoylenko.bookingservice.model.entity.DefaultRouteEntityBuilder;
import com.samoylenko.bookingservice.model.entity.DefaultWalkEntityBuilder;
import com.samoylenko.bookingservice.model.exception.WalkNotFoundException;
import com.samoylenko.bookingservice.model.status.WalkStatus;
import com.samoylenko.bookingservice.repository.EmployeeRepository;
import com.samoylenko.bookingservice.repository.RouteRepository;
import com.samoylenko.bookingservice.repository.WalkRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Set;

import static java.time.LocalDateTime.now;
import static java.time.LocalDateTime.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
public class WalkServiceTest {
    @Autowired
    private WalkService walkService;
    @Autowired
    private WalkRepository walkRepository;
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    public void setUp() {
        walkRepository.deleteAll();
        routeRepository.deleteAll();
    }

    @Test
    public void createWalk_shouldReturnCreatedWalkDto() {
        var savedRoute = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var walkDto = WalkCreateDto.builder()
                .routeId(savedRoute.getId())
                .maxPlaces(20)
                .priceForOne(3500)
                .durationInMinutes(120)
                .startTime(now().plusHours(1))
                .build();

        var createdWalk = walkService.createWalk(walkDto);

        assertThat(createdWalk).isNotNull();
        assertThat(createdWalk.getStatus()).isEqualTo(WalkStatus.DRAFT);
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
                .startTime(now().plusDays(1))
                .build();

        assertThatThrownBy(() -> walkService.createWalk(walkDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("notExistingRoute");
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
    }

    @Test
    public void getWalkForUser_withNotExistingWalk_shouldReturnNotFoundException() {
        assertThatThrownBy(() -> walkService.getWalkForUser("notExistingWalk"))
                .isInstanceOf(WalkNotFoundException.class)
                .hasMessageContaining("notExistingWalk");
    }

    @Test
    public void getWalkForUser_shouldReturnWalkAdminDto() {
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
    }

    @Test
    public void getAllForUser_shouldReturnWalkUserDtos() {
        var savedRoute = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var otherRoute = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var walk1 = DefaultWalkEntityBuilder.of()
                .withAvailablePlaces(10)
                .withStartTime(of(2024, Month.JUNE, 1, 6, 0))
                .withStatus(WalkStatus.BOOKING_IN_PROGRESS)
                .withRoute(savedRoute);
        var savedWalk1 = walkRepository.save(walk1.build());
        var walk2 = walk1
                .withAvailablePlaces(5)
                .withStartTime(of(2024, Month.JUNE, 2, 6, 0));
        var savedWalk2 = walkRepository.save(walk2.build());
        var walk3 = walk1.withRoute(otherRoute);
        var walk4 = walk1.withAvailablePlaces(1);
        var walk5 = walk1.withStatus(WalkStatus.BOOKING_FINISHED);
        var walk6 = walk1.withStartTime(of(2024, Month.JUNE, 3, 6, 0));
        var walk7 = walk1.withStartTime(of(2024, Month.MAY, 29, 6, 0));
        var saved = walkRepository.saveAll(List.of(walk3.build(), walk4.build(), walk5.build(), walk6.build(), walk7.build()));
        var request = WalkRequest.builder()
                .startAfter(LocalDate.of(2024, Month.JUNE, 1).atStartOfDay())
                .startBefore(LocalDate.of(2024, Month.JUNE, 2).atStartOfDay())
                .placeCount(3)
                .routeId(savedRoute.getId())
                .pageNumber(0)
                .pageSize(10)
                .build();

        var found = walkService.getWalksForUser(request);

        assertThat(found).hasSize(2);
        assertThat(found.get(0).getId()).isEqualTo(savedWalk1.getId());
        assertThat(found.get(1).getId()).isEqualTo(savedWalk2.getId());
    }

    @Test
    public void getAllForAdmin_shouldReturnWalkAdminDtos() {
        var savedRoute = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var employee1 = employeeRepository.save(DefaultEmployeeEntityBuilder.of().build());
        var employee2 = employeeRepository.save(DefaultEmployeeEntityBuilder.of().build());
        var walkBuilder = DefaultWalkEntityBuilder.of()
                .withRoute(savedRoute)
                .withStatus(WalkStatus.BOOKING_IN_PROGRESS)
                .withStartTime(of(2024, Month.JUNE, 2, 6, 0))
                .withEmployees(Set.of(employee1));
        var walk1 = walkRepository.save(walkBuilder.build());
        var walk2 = walkRepository.save(walkBuilder
                .withEmployees(Set.of(employee2))
                .build());
        var walk3 = walkRepository.save(walkBuilder
                .withEmployees(Set.of(employee1, employee2))
                .withStartTime(of(2024, Month.JUNE, 1, 6, 0))
                .build());
        var walk4 = walkRepository.save(walkBuilder
                .withStatus(WalkStatus.BOOKING_FINISHED)
                .build());
        var request = WalkRequest.builder()
                .employeeId(employee1.getId())
                .status(WalkStatus.BOOKING_IN_PROGRESS)
                .build();

        var found = walkService.getWalksForAdmin(request);

        assertThat(found.getContent()).hasSize(2);
        assertThat(found.getContent().get(0).getId()).isEqualTo(walk3.getId());
        assertThat(found.getContent().get(1).getId()).isEqualTo(walk1.getId());
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
        var savedWalk = walkRepository.save(DefaultWalkEntityBuilder.of().withRoute(savedRoute).build());
        var updateDto = WalkUpdateDto.builder()
                .status(WalkStatus.BOOKING_FINISHED)
                .durationInMinutes(30)
                .priceForOne(2222)
                .maxPlaces(100)
                .startTime(of(2024, Month.JUNE, 1, 6, 0))
                .build();

        var updatedWalk = walkService.updateWalk(savedWalk.getId(), updateDto);

        assertThat(updatedWalk).isNotNull();
        assertThat(updatedWalk.getStatus()).isEqualTo(updateDto.getStatus());
        assertThat(updatedWalk.getDuration()).isEqualTo(updateDto.getDurationInMinutes());
        assertThat(updatedWalk.getPriceForOne()).isEqualTo(updateDto.getPriceForOne());
        assertThat(updatedWalk.getMaxPlaces()).isEqualTo(updateDto.getMaxPlaces());
        assertThat(updatedWalk.getStartTime()).isEqualTo(updateDto.getStartTime());
        assertThat(updatedWalk.getRoute()).isNotNull();
        assertThat(updatedWalk.getRoute().getId()).isEqualTo(savedRoute.getId());
    }

    @Test
    void updateWalk_withNotExistingWalk_shouldReturnWalkNotFoundException() {
        var updateDto = WalkUpdateDto.builder().build();

        assertThatThrownBy(() -> walkService.updateWalk("notExistingWalk", updateDto))
                .isInstanceOf(WalkNotFoundException.class)
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
                .startTime(of(2020, Month.JUNE, 1, 6, 0))
                .build();

        assertThatThrownBy(() -> walkService.updateWalk(savedWalk.getId(), updateDto))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("должно быть в будущем")
                .hasMessageContaining("должно быть больше нуля")
                .hasMessageContaining("должно быть больше или равно нулю");
    }


}
