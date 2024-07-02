package com.samoylenko.bookingservice.model.voucher;

import com.samoylenko.bookingservice.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Generated
@Table(name = "voucher")
@EntityListeners(AuditingEntityListener.class)
public class VoucherEntity extends BaseEntity {

    @Column(name = "type", nullable = false)
    private DiscountType type;

    @Column(name = "status", nullable = false)
    private VoucherStatus status;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "distributor")
    private String distributor;

    @Column(name = "route_id")
    private String routeId;

    @Column(name = "expired_at")
    private Instant expiredAt;

    @Column(name = "discount_percent", nullable = false)
    private Integer discountPercent;

    @Column(name = "discount_absolute", nullable = false)
    private Integer discountAbsolute;

    @Column(name = "count", nullable = false)
    private Integer count;
}
