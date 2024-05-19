package com.samoylenko.bookingservice.model.entity;

import com.samoylenko.bookingservice.model.status.OrderStatus;
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
@Table(name = "order")
@EntityListeners(AuditingEntityListener.class)
public class OrderEntity extends BaseEntity {
    private OrderStatus status;
    private String walkId;
    private Integer priceForOne;
    private Integer numberOfPeople;
    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "contact_entity_id")
    private ContactEntity contactEntity;
    private String comment;
    private Boolean hasChildren;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        OrderEntity that = (OrderEntity) o;
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
                "status = " + getStatus() + ", " +
                "walkId = " + getWalkId() + ", " +
                "priceForOne = " + getPriceForOne() + ", " +
                "numberOfPeople = " + getNumberOfPeople() + ", " +
                "comment = " + getComment() + ", " +
                "hasChildren = " + getHasChildren() + ")";
    }
}
