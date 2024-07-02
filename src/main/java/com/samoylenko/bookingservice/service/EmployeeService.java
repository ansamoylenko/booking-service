package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.model.employee.EmployeeCreateDto;
import com.samoylenko.bookingservice.model.employee.EmployeeDto;
import com.samoylenko.bookingservice.model.employee.EmployeeEntity;
import com.samoylenko.bookingservice.model.exception.EntityNotFoundException;
import com.samoylenko.bookingservice.repository.EmployeeRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.samoylenko.bookingservice.model.exception.EntityType.EMPLOYEE;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final ModelMapper mapper;

    public EmployeeEntity getReferenceById(@NotBlank String id) {
        if (employeeRepository.existsById(id)) {
            return employeeRepository.getReferenceById(id);
        } else {
            throw new EntityNotFoundException(EMPLOYEE, id);
        }
    }

    public EmployeeDto toDto(EmployeeEntity entity) {
        return mapper.map(entity, EmployeeDto.class);
    }

    public EmployeeDto create(@Valid EmployeeCreateDto createDto) {
        log.info("Attempting to create employee: {}", createDto);
        var employee = employeeRepository.save(EmployeeEntity.builder()
                .role(createDto.getRole())
                .firstName(createDto.getFirstName())
                .lastName(createDto.getLastName())
                .phone(createDto.getPhone())
                .build());
        var dto = toDto(employee);
        log.info("Employee created with id: {}", dto.getId());
        return dto;
    }

    public EmployeeDto getEmployeeById(@NotBlank String id) {
        return employeeRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new EntityNotFoundException(EMPLOYEE, id));
    }

    public List<EmployeeDto> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }
}
