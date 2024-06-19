package com.samoylenko.bookingservice.model.spec;

import com.samoylenko.bookingservice.model.entity.BookingEntity;
import com.samoylenko.bookingservice.model.status.BookingStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class BookingSpecification {
    public static Specification<BookingEntity> withClientId(String clientId) {
        return (root, query, cb) -> clientId == null ?
                cb.conjunction() :
                cb.equal(root.get("client").get("id"), clientId);
    }
    public static Specification<BookingEntity> withPhone(String phone) {
        return (root, query, cb) -> phone == null ?
                cb.conjunction() :
                cb.equal(root.get("client").get("phone"), phone);
    }

    public static Specification<BookingEntity> withEmail(String email) {
        return (root, query, cb) -> email == null ?
                cb.conjunction() :
                cb.equal(root.get("client").get("email"), email);
    }

    public static Specification<BookingEntity> withWalk(String walkId) {
        return (root, query, cb) -> walkId == null ?
                cb.conjunction() :
                cb.equal(root.get("walk").get("id"), walkId);
    }

    public static Specification<BookingEntity> withStatus(BookingStatus status) {
        return (root, query, cb) -> status == null ?
                cb.conjunction() :
                cb.equal(root.get("status"), status);
    }

    public static Specification<BookingEntity> withStatus(List<BookingStatus> statusList) {
        return (root, query, cb) -> statusList == null ?
                cb.conjunction() :
                root.get("status").in(statusList);
    }
}
