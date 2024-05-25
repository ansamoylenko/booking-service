package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.model.dto.request.WalkRequest;
import com.samoylenko.bookingservice.model.dto.walk.*;
import com.samoylenko.bookingservice.model.entity.WalkEntity;
import com.samoylenko.bookingservice.model.exception.RouteNotFoundException;
import com.samoylenko.bookingservice.model.exception.WalkNotFoundException;
import com.samoylenko.bookingservice.model.spec.WalkSpecification;
import com.samoylenko.bookingservice.model.status.WalkStatus;
import com.samoylenko.bookingservice.repository.WalkRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.function.Consumer;

import static com.samoylenko.bookingservice.model.spec.WalkSpecification.*;

@Service
@Validated
@AllArgsConstructor
public class WalkService {
    private final RouteService routeService;
    private final WalkRepository walkRepository;
    private final ModelMapper modelMapper;

    public CompositeAdminWalkDto createWalk(@Valid WalkCreateDto walk) {
        try {
            var route = routeService.getRouteEntityById(walk.getRouteId());
            var entity = WalkEntity.builder()
                    .route(route)
                    .status(WalkStatus.BOOKING_IN_PROGRESS)
                    .startTime(walk.getStartTime())
                    .duration(walk.getDurationInMinutes())
                    .endTime(walk.getStartTime().plusMinutes(walk.getDurationInMinutes()))
                    .maxPlaces(walk.getMaxPlaces())
                    .availablePlaces(walk.getMaxPlaces())
                    .reservedPlaces(0)
                    .priceForOne(walk.getPriceForOne())
                    .build();

            var saved = walkRepository.save(entity);
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            return modelMapper.map(saved, CompositeAdminWalkDto.class)
                    .withBookings(List.of())
                    .withEmployees(List.of());
        } catch (RouteNotFoundException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public CompositeAdminWalkDto getWalkForAdmin(@NotBlank String id) {
        var entity = walkRepository.findById(id)
                .orElseThrow(() -> new WalkNotFoundException(id));
        return modelMapper.map(entity, CompositeAdminWalkDto.class);
    }

    @Transactional
    public Page<WalkDto> getWalksForAdmin(WalkRequest request) {
        var spec = WalkSpecification
                .withStatus(request.getStatus())
                .and(withRoute(request.getRouteId()))
                .and(startTimeAfter(request.getStartAfter()))
                .and(startTimeBefore(request.getStartBefore()))
                .and(withAvailablePlacesMoreOrEqualTo(request.getAvailablePlaces()))
                .and(withEmployee(request.getEmployeeId()));

        return walkRepository
                .findAll(spec, request.getPageRequest())
                .map((element) -> modelMapper.map(element, WalkDto.class));
    }

    @Transactional
    public Page<WalkDto> getWalksForUser(WalkRequest request) {
        var spec = WalkSpecification
                .withRoute(request.getRouteId())
                .and(withStatus(WalkStatus.BOOKING_IN_PROGRESS))
                .and(startTimeAfter(request.getStartAfter()))
                .and(startTimeBefore(request.getStartBefore()))
                .and(withAvailablePlacesMoreOrEqualTo(request.getAvailablePlaces()));

        return walkRepository
                .findAll(spec, request.getPageRequest())
                .map((element) -> modelMapper.map(element, WalkDto.class));
    }

    @Transactional
    public CompositeUserWalkDto getWalkForUser(@NotBlank String id) {
        var found = walkRepository.findById(id)
                .orElseThrow(() -> new WalkNotFoundException(id));
        return modelMapper.map(found, CompositeUserWalkDto.class);
    }

    @Transactional
    public CompositeAdminWalkDto updateWalk(@NotBlank String walkId, @Valid WalkUpdateDto walkDto) {
        var walkEntity = walkRepository.findById(walkId)
                .orElseThrow(() -> new WalkNotFoundException(walkId));
        updateIfNotNull(walkDto.getStatus(), walkEntity::setStatus);
        updateIfNotNull(walkDto.getMaxPlaces(), walkEntity::setMaxPlaces);
        updateIfNotNull(walkDto.getPriceForOne(), walkEntity::setPriceForOne);
        updateIfNotNull(walkDto.getDurationInMinutes(), walkEntity::setDuration);
        updateIfNotNull(walkDto.getStartTime(), startTime -> {
            walkEntity.setStartTime(startTime);
            walkEntity.setEndTime(startTime.plusMinutes(walkEntity.getDuration()));
        });

        var updated = walkRepository.save(walkEntity);
        return modelMapper.map(updated, CompositeAdminWalkDto.class);
    }

    private <T> void updateIfNotNull(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }

    public void markDeleted(String id) {
        var found = walkRepository.findById(id)
                .orElseThrow(() -> new WalkNotFoundException(id));
        found.setStatus(WalkStatus.DELETED);
        walkRepository.save(found);
    }

    //todo getRecordsForAdmin with filter by walk
    public Page<WalkDto> getOrdersByWalk(String id, PageRequest pageRequest) {
        return null;
    }
}
