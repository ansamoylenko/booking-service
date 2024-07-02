package com.samoylenko.bookingservice.model.booking;

import com.samoylenko.bookingservice.model.employee.EmployeeDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AdminBookingDto extends CompositeBookingDto {
    private List<EmployeeDto> employees;
}
