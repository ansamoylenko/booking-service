package com.samoylenko.bookingservice.model.employee;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class EmployeeRoleConverter implements Converter<String, EmployeeRole> {
    @Override
    public EmployeeRole convert(String source) {
        return EmployeeRole.fromDescription(source);
    }
}
