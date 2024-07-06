package com.samoylenko.bookingservice.model.employee;

import org.springframework.data.jpa.domain.Specification;

public class EmployeeSpecification {
    public static Specification<EmployeeEntity> withRole(EmployeeRole role) {
        return (root, query, cb) -> role == null ?
                cb.conjunction() :
                cb.in(root.get("roles")).value(role);
    }
}
