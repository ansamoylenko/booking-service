package com.samoylenko.bookingservice.model.entity;

import com.samoylenko.bookingservice.model.employee.EmployeeEntity;
import com.samoylenko.bookingservice.model.employee.EmployeeRole;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "of")
public class DefaultEmployeeEntityBuilder implements DefaultEntityBuilder<EmployeeEntity> {
    private EmployeeRole role = EmployeeRole.INSTRUCTOR;
    private String firstName = "Иван";
    private String lastName = "Иванов";
    private String phone = "77777777777";

    @Override
    public EmployeeEntity build() {
        return EmployeeEntity.builder()
                .role(role)
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .build();
    }
}
