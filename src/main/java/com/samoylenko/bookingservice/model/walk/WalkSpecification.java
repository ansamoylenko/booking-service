package com.samoylenko.bookingservice.model.walk;

import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public class WalkSpecification {
    public static Specification<WalkEntity> startTimeAfter(Instant time) {
        return (root, query, cb) -> time == null ?
                cb.conjunction() :
                cb.greaterThanOrEqualTo(root.get("startTime"), time);
    }

    public static Specification<WalkEntity> startTimeBefore(Instant time) {
        return (root, query, cb) -> time == null ?
                cb.conjunction() :
                cb.lessThanOrEqualTo(root.get("startTime"), time);
    }

    public static Specification<WalkEntity> endTimeAfter(Instant time) {
        return (root, query, cb) -> time == null ?
                cb.conjunction() :
                cb.greaterThanOrEqualTo(root.get("endTime"), time);
    }

    public static Specification<WalkEntity> endTimeBefore(Instant time) {
        return (root, query, cb) -> time == null ?
                cb.conjunction() :
                cb.lessThanOrEqualTo(root.get("endTime"), time);
    }

    public static Specification<WalkEntity> withRoute(String routeId) {
        return (root, query, cb) -> routeId == null ?
                cb.isFalse(root.get("route").get("deleted")) :
                cb.and(
                        cb.equal(root.get("route").get("id"), routeId),
                        cb.isFalse(root.get("deleted"))
                );
    }

    public static Specification<WalkEntity> withEmployee(String employeeId) {
        return (root, query, cb) -> employeeId == null ?
                cb.conjunction() :
                cb.in(root.get("employees").get("id")).value(employeeId);
    }

    public static Specification<WalkEntity> withStatus(WalkStatus status) {
        return (root, query, cb) -> status == null ?
                cb.conjunction() :
                cb.equal(root.get("status"), status);
    }

    public static Specification<WalkEntity> withReservedPlacesLessOrEqualTo(Integer reservedPlaces) {
        return (root, query, cb) -> reservedPlaces == null ?
                cb.conjunction() :
                cb.lessThanOrEqualTo(root.get("reservedPlaces"), reservedPlaces);
    }

    public static Specification<WalkEntity> withReservedPlacesMoreOrEqualTo(Integer reservedPlaces) {
        return (root, query, cb) -> reservedPlaces == null ?
                cb.conjunction() :
                cb.greaterThanOrEqualTo(root.get("reservedPlaces"), reservedPlaces);
    }

    public static Specification<WalkEntity> withAvailablePlacesMoreOrEqualTo(Integer availablePlaces) {
        return (root, query, cb) -> availablePlaces == null ?
                cb.conjunction() :
                cb.greaterThanOrEqualTo(root.get("availablePlaces"), availablePlaces);
    }


}
