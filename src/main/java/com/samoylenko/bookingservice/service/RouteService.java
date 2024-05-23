package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.model.dto.route.RouteCreateDto;
import com.samoylenko.bookingservice.model.dto.route.RouteDto;
import com.samoylenko.bookingservice.model.dto.route.RouteUpdateDto;
import com.samoylenko.bookingservice.model.entity.RouteEntity;
import com.samoylenko.bookingservice.model.exception.RouteNotFoundException;
import com.samoylenko.bookingservice.repository.RouteRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.function.Consumer;

@Service
@Validated
@AllArgsConstructor
public class RouteService {
    private final RouteRepository routeRepository;
    private final ModelMapper modelMapper;

    public RouteDto createRoute(@Valid RouteCreateDto routeDto) {
        var routeEntity = modelMapper.map(routeDto, RouteEntity.class);
        var saved = routeRepository.save(routeEntity);
        return modelMapper.map(saved, RouteDto.class);
    }

    public RouteDto getRouteById(String id) {
        var found = routeRepository.findById(id)
                .orElseThrow(() -> new RouteNotFoundException(id));
        return modelMapper.map(found, RouteDto.class);
    }

    public RouteEntity getRouteEntityById(String id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new RouteNotFoundException(id));
    }

    public RouteDto updateRoute(String id, @Valid RouteUpdateDto route) {
        RouteEntity existingRoute = routeRepository.findById(id)
                .orElseThrow(() -> new RouteNotFoundException(id));
        updateIfNotNull(route.getName(), existingRoute::setName);
        updateIfNotNull(route.getDescription(), existingRoute::setDescription);
        updateIfNotNull(route.getPriceForOne(), existingRoute::setPriceForOne);
        RouteEntity updatedRoute = routeRepository.save(existingRoute);
        return modelMapper.map(updatedRoute, RouteDto.class);
    }

    private <T> void updateIfNotNull(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    public void markDeleted(String id) {
        var found = routeRepository.findById(id)
                .orElseThrow(() -> new RouteNotFoundException(id));
        found.setDeleted(true);
        routeRepository.save(found);
    }

    public List<RouteDto> getAllRoutes() {
        var sort = Sort.by(Sort.Direction.DESC, "createdDate");
        var routes = routeRepository.findAll(sort);
        return routes.stream()
                .filter(route -> !route.isDeleted())
                .map(route -> modelMapper.map(route, RouteDto.class))
                .toList();
    }
}
