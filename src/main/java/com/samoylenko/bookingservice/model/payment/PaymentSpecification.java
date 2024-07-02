package com.samoylenko.bookingservice.model.payment;

import org.springframework.data.jpa.domain.Specification;

public class PaymentSpecification {
    public static Specification<PaymentEntity> withStatus(PaymentStatus status) {
        return (root, query, cb) -> status == null ?
                cb.conjunction() :
                cb.equal(root.get("status"), status);
    }
}
