package com.samoylenko.bookingservice.model.entity;

import com.samoylenko.bookingservice.model.status.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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

    @Column(name = "order_id", nullable = false)
    private String orderId;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "pricae_for_one", nullable = false)
    private Integer priceForOne;

    @Column(name = "total_cost", nullable = false)
    private Integer totalCost;

    @Column(name = "link", nullable = false)
    private String link;

    @Column(name = "latest_payment_time", nullable = false)
    private Instant latestPaymentTime;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "booking_id")
    private BookingEntity booking;

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
                "amount = " + getAmount() + ")";
    }
}
