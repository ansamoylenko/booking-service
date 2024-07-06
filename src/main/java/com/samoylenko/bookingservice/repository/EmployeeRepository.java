package com.samoylenko.bookingservice.repository;

import com.samoylenko.bookingservice.model.employee.EmployeeEntity;

import java.util.Optional;

public interface EmployeeRepository extends BaseEntityRepository<EmployeeEntity> {
    Optional<EmployeeEntity> findByEmail(String email);
    boolean existsById(String id);

    boolean existsByEmail(String email);
}
