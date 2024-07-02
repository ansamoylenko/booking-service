package com.samoylenko.bookingservice.repository;

import com.samoylenko.bookingservice.model.employee.EmployeeRole;
import com.samoylenko.bookingservice.model.employee.EmployeeSpecification;
import com.samoylenko.bookingservice.model.entity.DefaultEmployeeEntityBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EmployeeRepositoryTest extends BaseRepositoryTest {
    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    public void setUp() {
        employeeRepository.deleteAll();
    }

    @Test
    public void save_shouldSaveEmployee() {
        var employee = DefaultEmployeeEntityBuilder.of().build();

        var saved = employeeRepository.save(employee);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedDate()).isNotNull();
        assertThat(saved.getLastModifiedDate()).isNotNull();
        assertThat(saved.getVersion()).isNotNull();
    }

    @Test
    public void saveAll_WithIncorrectData_shouldThrowException() {
        var employee = DefaultEmployeeEntityBuilder.of()
                .withFirstName("")
                .withLastName(null)
                .withPhone("777")
                .build();

        assertThrows(Exception.class, () -> employeeRepository.saveAll(List.of(employee)));
    }

    @Test
    public void findById_shouldReturnEmployee() {
        var employee = DefaultEmployeeEntityBuilder.of().build();
        var saved = employeeRepository.save(employee);

        var found = employeeRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get()).isEqualTo(saved);
    }

    @Test
    public void findAll_shouldReturnAllEmployees() {
        var employee1 = DefaultEmployeeEntityBuilder.of().build();
        var employee2 = DefaultEmployeeEntityBuilder.of().build();
        employeeRepository.saveAll(List.of(employee1, employee2));

        var found = employeeRepository.findAll();

        assertThat(found).hasSize(2);
        assertThat(found).contains(employee1, employee2);
    }

    @Test
    public void findAll_withRoleFilterByRole_shouldReturnEmployeesWithRole() {
        var employee1 = DefaultEmployeeEntityBuilder.of().withRole(EmployeeRole.ASSISTANT).build();
        var employee2 = DefaultEmployeeEntityBuilder.of().withRole(EmployeeRole.INSTRUCTOR).build();
        var employee3 = DefaultEmployeeEntityBuilder.of().withRole(EmployeeRole.INSTRUCTOR).build();

        employeeRepository.saveAll(List.of(employee1, employee2, employee3));
        var spec = EmployeeSpecification.withRole(EmployeeRole.INSTRUCTOR);

        var found = employeeRepository.findAll(spec);

        assertThat(found).hasSize(2);
        assertThat(found).contains(employee2, employee3);
    }

    @Test
    public void deleteById_shouldDeleteEmployee() {
        var employee = DefaultEmployeeEntityBuilder.of().build();
        var saved = employeeRepository.save(employee);

        employeeRepository.deleteById(saved.getId());

        var found = employeeRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }
}
