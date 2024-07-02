package com.samoylenko.bookingservice.model.entity;

import com.samoylenko.bookingservice.model.promotion.DiscountStatus;
import com.samoylenko.bookingservice.model.status.PaymentStatus;
import com.samoylenko.bookingservice.model.voucher.DiscountType;
import com.samoylenko.bookingservice.model.voucher.VoucherEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Generated
@Table(name = "payment")
@EntityListeners(AuditingEntityListener.class)
public class PaymentEntity extends BaseEntity {
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus status;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "pricae_for_one", nullable = false)
    private BigDecimal priceForOne;

    @Column(name = "total_cost", nullable = false)
    private BigDecimal totalCost;

    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;

    @Column(name = "discount_status", nullable = false)
    private DiscountStatus discountStatus;

    @Column(name = "discount_code")
    private String discountCode;

    @Column(name = "discount_percent", nullable = false)
    private Integer discountPercent;

    @Column(name = "discount_absolute", nullable = false)
    private Integer discountAbsolute;

    @Column(name = "invoice_url")
    private String invoiceUrl;

    @Column(name = "invoice_id")
    private String invoiceId;

    @Column(name = "latest_payment_time")
    private Instant latestPaymentTime;


    @OneToOne(cascade = CascadeType.ALL, mappedBy = "payment")
    @JoinColumn(name = "booking_id")
    private BookingEntity booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    private VoucherEntity voucher;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        PaymentEntity that = (PaymentEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
               "id = " + getId() + ", " +
               "createdDate = " + getCreatedDate() + ", " +
               "lastModifiedDate = " + getLastModifiedDate() + ", " +
               "status = " + getStatus() + ", " +
               "amount = " + getQuantity() + ")";
    }
}
