package com.samoylenko.bookingservice.model.employee;

import lombok.*;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {
    private String id;
    private EmployeeRole role;
    private String firstName;
    private String lastName;
    private String phone;
}
