package com.samoylenko.bookingservice.model.spec;

import com.samoylenko.bookingservice.model.entity.EmployeeEntity;
import com.samoylenko.bookingservice.model.entity.EmployeeRole;
import org.springframework.data.jpa.domain.Specification;

public class EmployeeSpecification {
    public static Specification<EmployeeEntity> withRole(EmployeeRole role) {
        return (root, query, cb) -> role == null ?
                cb.conjunction() :
                cb.equal(root.get("role"), role);

    }
}
