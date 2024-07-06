package com.samoylenko.bookingservice.model.entity;

import com.github.javafaker.Faker;
import com.samoylenko.bookingservice.model.employee.EmployeeEntity;
import com.samoylenko.bookingservice.model.employee.EmployeeRole;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.Set;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "of")
public class DefaultEmployeeEntityBuilder implements DefaultEntityBuilder<EmployeeEntity> {
    private static Faker faker = new Faker();
    private EmployeeRole role = EmployeeRole.ROLE_INSTRUCTOR;
    private String firstName = "Иван";
    private String lastName = "Иванов";
    private String phone = faker.phoneNumber().cellPhone();
    private String email = faker.internet().emailAddress();
    private String password = "password";

    @Override
    public EmployeeEntity build() {
        return EmployeeEntity.builder()
                .roles(Set.of(role))
                .firstName(firstName)
                .lastName(lastName)
                .phone(phone)
                .email(email)
                .password(password)
                .build();
    }
}
