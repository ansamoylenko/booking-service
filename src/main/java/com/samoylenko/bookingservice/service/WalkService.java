package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.model.dto.request.WalkRequest;
import com.samoylenko.bookingservice.model.dto.walk.WalkAdminDto;
import com.samoylenko.bookingservice.model.dto.walk.WalkCreateDto;
import com.samoylenko.bookingservice.model.dto.walk.WalkUpdateDto;
import com.samoylenko.bookingservice.model.dto.walk.WalkUserDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    public WalkAdminDto createWalk(@Valid WalkCreateDto walk) {
        try {
            var route = routeService.getRouteEntityById(walk.getRouteId());
            var entity = WalkEntity.builder()
                    .route(route)
                    .status(WalkStatus.DRAFT)
                    .startTime(walk.getStartTime())
                    .duration(walk.getDurationInMinutes())
                    .endTime(walk.getStartTime().plusMinutes(walk.getDurationInMinutes()))
                    .maxPlaces(walk.getMaxPlaces())
                    .availablePlaces(walk.getMaxPlaces())
                    .reservedPlaces(0)
                    .priceForOne(walk.getPriceForOne())
                    .build();

            var saved = walkRepository.save(entity);
            return modelMapper.map(saved, WalkAdminDto.class)
                    .withBookings(List.of());
        } catch (RouteNotFoundException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public Page<WalkAdminDto> getWalksForAdmin(WalkRequest request) {
        var spec = WalkSpecification
                .withStatus(request.getStatus())
                .and(withRoute(request.getRouteId()))
                .and(startTimeAfter(request.getStartAfter())
                        .and(startTimeBefore(request.getStartBefore()))
                        .and(withAvailablePlacesMoreOrEqualTo(request.getPlaceCount()))
                        .and(withEmployee(request.getEmployeeId())));

        var pageRequest = PageRequest.of(
                request.getPageNumber() == null ? 0 : request.getPageNumber(),
                request.getPageSize() == null ? 10 : request.getPageSize(),
                Sort.by(Sort.Direction.ASC, "startTime")
        );

        var walks = walkRepository.findAll(spec, pageRequest);

        return walks.map(walk -> modelMapper.map(walk, WalkAdminDto.class));
    }

    @Transactional
    public List<WalkUserDto> getWalksForUser(WalkRequest request) {
        var spec = WalkSpecification
                .withRoute(request.getRouteId())
                .and(withStatus(WalkStatus.BOOKING_IN_PROGRESS))
                .and(startTimeAfter(request.getStartAfter()))
                .and(startTimeBefore(request.getStartBefore().plusDays(1)))
                .and(withAvailablePlacesMoreOrEqualTo(request.getPlaceCount()));
        var pageRequest = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.by(Sort.Direction.ASC, "startTime"));

        var walks = walkRepository.findAll(spec, pageRequest).getContent();

        return walks.stream()
                .map(walk -> modelMapper.map(walk, WalkUserDto.class))
                .toList();
    }

    @Transactional
    public WalkUserDto getWalkForUser(@NotBlank String id) {
        var found = walkRepository.findById(id)
                .orElseThrow(() -> new WalkNotFoundException(id));
        var userDto = modelMapper.map(found, WalkUserDto.class);
        userDto.setEndTime(found.getStartTime().plusMinutes(found.getDuration()));
        return userDto;
    }

    @Transactional
    public WalkAdminDto updateWalk(@NotBlank String walkId, @Valid WalkUpdateDto walkDto) {
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
        return modelMapper.map(updated, WalkAdminDto.class);
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
    public Page<WalkUserDto> getOrdersByWalk(String id, PageRequest pageRequest) {
        return null;
    }

    //todo change to getWalksForUser with filter by route
    public Page<WalkUserDto> getWalksByRoute(String id, PageRequest pageRequest) {
        return null;
    }
}
