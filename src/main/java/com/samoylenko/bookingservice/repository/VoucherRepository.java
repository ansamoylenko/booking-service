package com.samoylenko.bookingservice.repository;

import com.samoylenko.bookingservice.model.voucher.VoucherEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface VoucherRepository extends BaseEntityRepository<VoucherEntity> {
    Optional<VoucherEntity> findByCode(String code);

    List<VoucherEntity> findAll(Specification spec, Sort sort);
}
