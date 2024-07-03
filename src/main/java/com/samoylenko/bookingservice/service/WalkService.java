package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.model.exception.EntityCreateException;
import com.samoylenko.bookingservice.model.exception.EntityNotFoundException;
import com.samoylenko.bookingservice.model.exception.LimitExceededException;
import com.samoylenko.bookingservice.model.walk.*;
import com.samoylenko.bookingservice.repository.WalkRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.function.Consumer;

import static com.samoylenko.bookingservice.model.exception.EntityType.WALK;
import static com.samoylenko.bookingservice.model.walk.WalkSpecification.*;
import static java.time.temporal.ChronoUnit.MINUTES;

@Slf4j
@Service
@Validated
public class WalkService {
    private final RouteService routeService;
    private final BookingService bookingService;
    private final WalkRepository walkRepository;
    private final ModelMapper modelMapper;

    public WalkService(RouteService routeService, @Lazy BookingService bookingService, WalkRepository walkRepository, ModelMapper modelMapper) {
        this.routeService = routeService;
        this.bookingService = bookingService;
        this.walkRepository = walkRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public CompositeAdminWalkDto createWalk(@Valid WalkCreateDto walk) {
        log.info("Creating walk: {}", walk);
        try {
            var route = routeService.getRouteEntityById(walk.getRouteId());
            var entity = WalkEntity.builder()
                    .route(route)
                    .status(WalkStatus.BOOKING_IN_PROGRESS)
                    .startTime(walk.getStartTime())
                    .duration(walk.getDurationInMinutes())
                    .endTime(walk.getStartTime().plus(walk.getDurationInMinutes(), MINUTES))
                    .maxPlaces(walk.getMaxPlaces())
                    .availablePlaces(walk.getMaxPlaces())
                    .reservedPlaces(0)
                    .priceForOne(walk.getPriceForOne())
                    .build();

            var saved = walkRepository.save(entity);
            modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
            var dto = modelMapper.map(saved, CompositeAdminWalkDto.class)
                    .withBookings(List.of());
            log.info("Walk has been created with id {}", dto.getId());
            return dto;
        } catch (Exception e) {
            throw new EntityCreateException(WALK, e);
        }
    }

    public void decreaseAvailablePlaces(@NotBlank String walkId, int numberOfPlaces) {
        var walk = getWalkEntityById(walkId);
        if (walk.getAvailablePlaces() < numberOfPlaces) {
            throw new LimitExceededException(walkId);
        }
        walk.setAvailablePlaces(walk.getAvailablePlaces() - numberOfPlaces);
        walkRepository.save(walk);
        log.info("Locked {} places of walk {}, available: {}", numberOfPlaces, walkId, walk.getAvailablePlaces());
    }

    public void increaseAvailablePlaces(@NotBlank String walkId, int numberOfPlaces) {
        var walk = walkRepository.findById(walkId)
                .orElseThrow(() -> new EntityNotFoundException(WALK, walkId));
        walk.setAvailablePlaces(walk.getAvailablePlaces() + numberOfPlaces);
        walkRepository.save(walk);
        log.info("Unlocked {} places of walk {}, available: {}", numberOfPlaces, walkId, walk.getAvailablePlaces());
    }

    @Transactional
    public CompositeAdminWalkDto getWalkForAdmin(@NotBlank String id) {
        var entity = getWalkEntityById(id);
        var dto = modelMapper.map(entity, CompositeAdminWalkDto.class);
        var bookings = dto.getBookings().stream()
                .map(booking -> bookingService.getBookingForAdmin(booking.getId()))
                .toList();
        dto.setBookings(bookings);
        return dto;
    }

    public WalkEntity getWalkEntityById(@NotBlank String id) {
        return walkRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(WALK, id));
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
                .and(withStatus(request.getStatus()))
                .and(startTimeAfter(request.getStartAfter()))
                .and(startTimeBefore(request.getStartBefore()))
                .and(endTimeAfter(request.getEndAfter()))
                .and(endTimeBefore(request.getEndBefore()))
                .and(withAvailablePlacesMoreOrEqualTo(request.getAvailablePlaces()));

        return walkRepository
                .findAll(spec, request.getPageRequest())
                .map((element) -> modelMapper.map(element, WalkDto.class));
    }

    @Transactional
    public CompositeUserWalkDto getWalkForUser(@NotBlank String id) {
        var found = getWalkEntityById(id);
        return modelMapper.map(found, CompositeUserWalkDto.class);
    }

    @Transactional
    public CompositeAdminWalkDto updateWalk(@NotBlank String walkId, @Valid WalkUpdateDto walkDto) {
        var walkEntity = getWalkEntityById(walkId);
        updateIfNotNull(walkDto.getStatus(), walkEntity::setStatus);
        updateIfNotNull(walkDto.getMaxPlaces(), walkEntity::setMaxPlaces);
        updateIfNotNull(walkDto.getPriceForOne(), walkEntity::setPriceForOne);
        updateIfNotNull(walkDto.getDurationInMinutes(), walkEntity::setDuration);
        updateIfNotNull(walkDto.getStartTime(), startTime -> {
            walkEntity.setStartTime(startTime);
            walkEntity.setEndTime(startTime.plus(walkEntity.getDuration(), MINUTES));
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
        var found = getWalkEntityById(id);
        found.setStatus(WalkStatus.DELETED);
        walkRepository.save(found);
    }

    public void setStatus(@NotBlank String id, @NotNull WalkStatus walkStatus) {
        var walk = getWalkEntityById(id);
        var oldStatus = walk.getStatus();
        walk.setStatus(walkStatus);
        walkRepository.save(walk);
        log.info("Updated status of walk {} from {} to {}", id, oldStatus, walkStatus);
    }
}
