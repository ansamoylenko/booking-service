package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.model.dto.route.RouteCreateDto;
import com.samoylenko.bookingservice.model.dto.route.RouteUpdateDto;
import com.samoylenko.bookingservice.model.entity.DefaultRouteEntityBuilder;
import com.samoylenko.bookingservice.model.exception.RouteNotFoundException;
import com.samoylenko.bookingservice.repository.*;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class RouteServiceTest extends BaseServiceTest {
    private final RouteService routeService;

    public RouteServiceTest(RouteService routeService, WalkRepository walkRepository, RouteRepository routeRepository, EmployeeRepository employeeRepository, BookingRepository bookingRepository, ClientRepository clientRepository, PaymentRepository paymentRepository) {
        super(walkRepository, routeRepository, employeeRepository, bookingRepository, clientRepository, paymentRepository);
        this.routeService = routeService;
    }

    @Test
    public void createRoute_shouldReturnCreatedRouteDto() {
        var routeDto = RouteCreateDto.builder()
                .name("testName")
                .description("testDescription")
                .priceForOne(10)
                .serviceName("testService")
                .build();

        var created = routeService.createRoute(routeDto);

        assertThat(created).isNotNull();
        assertThat(created.getName()).isEqualTo(routeDto.getName());
        assertThat(created.getDescription()).isEqualTo(routeDto.getDescription());
        assertThat(created.getPriceForOne()).isEqualTo(routeDto.getPriceForOne());
    }

    @Test
    public void createRoute_withIncorrectDto_shouldThrowIllegalArgumentException() {
        var routeDto = RouteCreateDto.builder()
                .name("")
                .description(null)
                .priceForOne(-1)
                .build();

        assertThatThrownBy(() -> routeService.createRoute(routeDto))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("описание маршрута не может быть пустым")
                .hasMessageContaining("описание маршрута не может быть пустым")
                .hasMessageContaining("цена не может быть отрицательной");
    }

    @Test
    public void updateRoute_shouldReturnUpdatedRouteDto() {
        var route = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var routeUpdateDto = RouteUpdateDto.builder()
                .name("newName")
                .description("newDescription")
                .priceForOne(10)
                .build();

        var updated = routeService.updateRoute(route.getId(), routeUpdateDto);

        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(route.getId());
        assertThat(updated.getDescription()).isEqualTo(routeUpdateDto.getDescription());
        assertThat(updated.getPriceForOne()).isEqualTo(routeUpdateDto.getPriceForOne());
        assertThat(updated.getName()).isEqualTo(routeUpdateDto.getName());
    }

    @Test
    public void updateRoute_withNullFields_shouldReturnOldRouteDto() {
        var route = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var routeUpdateDto = RouteUpdateDto.builder().build();

        var updated = routeService.updateRoute(route.getId(), routeUpdateDto);

        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(route.getId());
        assertThat(updated.getDescription()).isEqualTo(route.getDescription());
        assertThat(updated.getPriceForOne()).isEqualTo(route.getPriceForOne());
        assertThat(updated.getName()).isEqualTo(route.getName());
    }

    @Test
    public void updateRoute_withIncorrectDto_shouldThrowValidationException() {
        var route = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var routeUpdateDto = RouteUpdateDto.builder()
                .name("")
                .description("")
                .priceForOne(-1)
                .build();

        assertThatThrownBy(() -> routeService.updateRoute(route.getId(), routeUpdateDto))
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("название маршрута не может быть пустым")
                .hasMessageContaining("описание маршрута не может быть пустым")
                .hasMessageContaining("цена не может быть отрицательной");
    }

    @Test
    public void getRouteById_shouldReturnRouteDto() {
        var routeEntity = DefaultRouteEntityBuilder.of().build();
        var savedEntity = routeRepository.save(routeEntity);

        var found = routeService.getRouteById(savedEntity.getId());

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo(routeEntity.getName());
        assertThat(found.getDescription()).isEqualTo(routeEntity.getDescription());
        assertThat(found.getPriceForOne()).isEqualTo(routeEntity.getPriceForOne());
    }

    @Test
    public void getRouteById_withUnknownId_shouldReturnRouteNotFoundException() {
        assertThatThrownBy(() -> routeService.getRouteById("unknown"))
                .isInstanceOf(RouteNotFoundException.class)
                .hasMessageContaining("unknown");
    }

    @Test
    public void getAllRoutes_shouldReturnListOfRouteDto() {
        var route1 = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var route2 = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var route3 = routeRepository.save(DefaultRouteEntityBuilder.of().build());

        var found = routeService.getAllRoutes();

        assertThat(found).isNotEmpty();
        assertThat(found.size()).isEqualTo(3);
        assertThat(found.get(0).getId()).isEqualTo(route3.getId());
    }

    @Test
    public void markDeleted_shouldMarkRouteAsDeleted() {
        var route1 = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var route2 = routeRepository.save(DefaultRouteEntityBuilder.of().build());
        var before = routeService.getAllRoutes();

        routeService.markDeleted(route1.getId());

        var after = routeService.getAllRoutes();
        assertThat(before).hasSize(2);
        assertThat(after).hasSize(1);
        assertThat(after.get(0).getId()).isEqualTo(route2.getId());
    }
}
