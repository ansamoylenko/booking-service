package com.samoylenko.bookingservice.repository;

import com.samoylenko.bookingservice.model.employee.EmployeeEntity;

public interface EmployeeRepository extends BaseEntityRepository<EmployeeEntity> {
    boolean existsById(String id);
}
