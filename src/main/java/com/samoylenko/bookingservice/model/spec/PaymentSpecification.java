package com.samoylenko.bookingservice.model.spec;

import com.samoylenko.bookingservice.model.entity.PaymentEntity;
import com.samoylenko.bookingservice.model.status.PaymentStatus;
import org.springframework.data.jpa.domain.Specification;

public class PaymentSpecification {
    public static Specification<PaymentEntity> withStatus(PaymentStatus status) {
        return (root, query, cb) -> status == null ?
                cb.conjunction() :
                cb.equal(root.get("status"), status);
    }
}
