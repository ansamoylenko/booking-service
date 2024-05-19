package com.samoylenko.bookingservice.model.entity;

import com.samoylenko.bookingservice.model.status.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Objects;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payment")
@EntityListeners(AuditingEntityListener.class)
public class PaymentEntity extends BaseEntity {
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus status;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "order_entity_id")
    private OrderEntity orderEntity;

    @Column(name = "amount", nullable = false)
    private Integer amount;

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
