package com.samoylenko.bookingservice.controller.admin;

import com.samoylenko.bookingservice.model.employee.EmployeeCreateDto;
import com.samoylenko.bookingservice.model.employee.EmployeeDto;
import com.samoylenko.bookingservice.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/admin/employees")
@Tag(name = "Сотрудники (для администратора)")
@AllArgsConstructor
public class AdminEmployeeController {
    private final EmployeeService employeeService;

    @Operation(summary = "Создать нового сотрудника")
    @PostMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<EmployeeDto> createEmployee(@RequestBody EmployeeCreateDto createDto, UriComponentsBuilder uriBuilder) {
        var crated = employeeService.create(createDto);
        var location = uriBuilder.path("/api/v1/admin/employees/{id}")
                .buildAndExpand(crated.getId())
                .toUri();
        return ResponseEntity.created(location).body(crated);
    }

    @Operation(summary = "Получить сотрудника по id")
    @GetMapping(value = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public EmployeeDto getEmployee(@PathVariable String id) {
        return employeeService.getEmployeeById(id);
    }

    @Operation(summary = "Получить всех сотрудников")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<EmployeeDto> getAllEmployees() {
        return employeeService.getAllEmployees();
    }
}
