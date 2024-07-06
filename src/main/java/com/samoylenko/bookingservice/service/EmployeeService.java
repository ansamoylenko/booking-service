package com.samoylenko.bookingservice.service;

import com.samoylenko.bookingservice.model.employee.EmployeeCreateDto;
import com.samoylenko.bookingservice.model.employee.EmployeeDto;
import com.samoylenko.bookingservice.model.employee.EmployeeEntity;
import com.samoylenko.bookingservice.model.employee.EmployeeRole;
import com.samoylenko.bookingservice.model.exception.EntityNotFoundException;
import com.samoylenko.bookingservice.repository.EmployeeRepository;
import com.samoylenko.bookingservice.service.utils.CodeGenerator;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Set;

import static com.samoylenko.bookingservice.model.exception.EntityType.EMPLOYEE;

@Slf4j
@Service
@Validated
@AllArgsConstructor
public class EmployeeService implements UserDetailsService {
    private final EmployeeRepository employeeRepository;
    private final ModelMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final CodeGenerator codeGenerator;


    @PostConstruct
    public void init() {
        log.info("Creating default employee for testing purposes");
        if (!employeeRepository.existsByEmail("owner")) {
            var owner = employeeRepository.save(EmployeeEntity.builder()
                    .email("owner")
                    .password(passwordEncoder.encode("owner"))
                    .roles(Set.of(EmployeeRole.ROLE_OWNER))
                    .enabled(true)
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .build());
        }
        if (!employeeRepository.existsByEmail("admin")) {
            var admin = employeeRepository.save(EmployeeEntity.builder()
                    .email("admin")
                    .password(passwordEncoder.encode("admin"))
                    .roles(Set.of(EmployeeRole.ROLE_ADMIN))
                    .enabled(true)
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .build());
        }

        if (!employeeRepository.existsByEmail("instructor")) {
            var manager = employeeRepository.save(EmployeeEntity.builder()
                    .email("manager")
                    .password(passwordEncoder.encode("manager"))
                    .roles(Set.of(EmployeeRole.ROLE_MANAGER))
                    .enabled(true)
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .build());
        }

        if (!employeeRepository.existsByEmail("instructor")) {
            var instructor = employeeRepository.save(EmployeeEntity.builder()
                    .email("instructor")
                    .password(passwordEncoder.encode("instructor"))
                    .roles(Set.of(EmployeeRole.ROLE_INSTRUCTOR))
                    .enabled(true)
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .build());
        }

        mapper.createTypeMap(EmployeeEntity.class, EmployeeDto.class)
                .addMappings(mapper -> mapper.map(EmployeeEntity::getRoles, EmployeeDto::setAuthorities));
    }

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
        var password = codeGenerator.getCode();
        log.debug("Password generated: {}", password);
        var employee = employeeRepository.save(EmployeeEntity.builder()
                .roles(Set.of(createDto.getRole()))
                .firstName(createDto.getFirstName())
                .lastName(createDto.getLastName())
                .phone(createDto.getPhone())
                .email(createDto.getEmail())
                .password(passwordEncoder.encode(password))
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build());
        var dto = toDto(employee);
        //TODO Use for development purposes
        dto.setPassword(password);
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

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return employeeRepository.findByEmail(email)
                .map(this::toDto)
                .orElseThrow(() -> new UsernameNotFoundException(email));
    }
}
