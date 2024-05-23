package com.samoylenko.bookingservice.model.entity;

import com.samoylenko.bookingservice.model.status.WalkStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Generated
@Table(name = "walk")
@EntityListeners(AuditingEntityListener.class)
public class WalkEntity extends BaseEntity {
    private WalkStatus status;

    @NotNull
    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private RouteEntity route;

    @OneToMany(mappedBy = "walk", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @OrderBy("createdDate")
    private List<BookingEntity> bookings;

    @NotNull
    @Positive
    private Integer maxPlaces;

    @NotNull
    @PositiveOrZero
    private Integer reservedPlaces;

    @NotNull
    @PositiveOrZero
    private Integer availablePlaces;

    @NotNull
    @PositiveOrZero
    private Integer priceForOne;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    private LocalDateTime endTime;

    @NotNull
    @Positive
    private Integer duration;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "walk_employee",
            joinColumns = @JoinColumn(name = "walk_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id"))
    private Set<EmployeeEntity> employees = new HashSet<>();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        WalkEntity that = (WalkEntity) o;
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
                "maxCount = " + getMaxPlaces() + ", " +
                "priceForOne = " + getPriceForOne() + ", " +
                "startTime = " + getStartTime() + ", " +
                "endTime = " + getEndTime() + ")";
    }
}