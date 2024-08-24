package com.samoylenko.bookingservice.model.voucher;

import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

public class VoucherSpecification {
    public static Specification<VoucherEntity> withRoute(String route) {
        return (root, query, cb) -> route == null ?
                cb.conjunction() :
                cb.equal(root.get("routeId"), route);
    }

    public static Specification<VoucherEntity> withStatus(VoucherStatus status) {
        return (root, query, cb) -> status == null ?
                cb.conjunction() :
                cb.equal(root.get("status"), status);
    }

    public static Specification<VoucherEntity> withType(DiscountType type) {
        return (root, query, cb) -> type == null ?
                cb.conjunction() :
                cb.equal(root.get("type"), type);
    }

    public static Specification<VoucherEntity> expiredTimeAfter(Instant time) {
        return (root, query, cb) -> time == null ?
                cb.conjunction() :
                cb.greaterThan(root.get("expiredAt"), time);
    }

    public static Specification<VoucherEntity> expiredTimeBefore(Instant time) {
        return (root, query, cb) -> time == null ?
                cb.conjunction() :
                cb.lessThan(root.get("expiredAt"), time);
    }
}


