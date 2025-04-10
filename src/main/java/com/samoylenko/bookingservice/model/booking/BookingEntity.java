package com.samoylenko.bookingservice.model.booking;

import com.samoylenko.bookingservice.model.BaseEntity;
import com.samoylenko.bookingservice.model.client.ClientEntity;
import com.samoylenko.bookingservice.model.employee.EmployeeEntity;
import com.samoylenko.bookingservice.model.payment.PaymentEntity;
import com.samoylenko.bookingservice.model.walk.WalkEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Generated
@Table(name = "booking")
@EntityListeners(AuditingEntityListener.class)
public class BookingEntity extends BaseEntity {
    private BookingStatus status;

    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(name = "walk_id")
    private WalkEntity walk;

    @Positive
    private Integer numberOfPeople;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private ClientEntity client;

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "payment_id")
    private PaymentEntity payment;

    private String comment;
    private Boolean hasChildren;
    private Boolean agreementConfirmed;
    private Instant endTime;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "booking_employee",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id"))
    private Set<EmployeeEntity> employees = new HashSet<>();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        BookingEntity that = (BookingEntity) o;
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
                "numberOfPeople = " + getNumberOfPeople() + ", " +
                "comment = " + getComment() + ", " +
               "endTime  =  " + getEndTime() +
                "hasChildren = " + getHasChildren() + ")";
    }
}
