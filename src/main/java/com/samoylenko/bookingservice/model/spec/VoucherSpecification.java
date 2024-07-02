package com.samoylenko.bookingservice.model.spec;

import com.samoylenko.bookingservice.model.promotion.VoucherStatus;
import com.samoylenko.bookingservice.model.voucher.VoucherEntity;
import org.springframework.data.jpa.domain.Specification;

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
}


