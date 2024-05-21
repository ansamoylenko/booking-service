package com.samoylenko.bookingservice.model.entity;

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
    private String email = "ivan@mail.ru";
    private String phone = "77777777777";

    @Override
    public EmployeeEntity build() {
        return EmployeeEntity.builder()
                .role(role)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .build();
    }
}
