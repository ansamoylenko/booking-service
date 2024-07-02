package com.samoylenko.bookingservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.LocalDate;
import java.util.Random;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "of")
public class DefaultClientEntityBuilder implements DefaultEntityBuilder<ClientEntity> {
    private String firstName = "Alexander";
    private String lastName = "Samoylenko";
    private String email = "a.n.samoylenko@outlook.com";
    private String phone = String.valueOf(new Random().nextLong(700003939));
    private LocalDate dateOfBirth = LocalDate.parse("1998-09-15");

    @Override
    public ClientEntity build() {
        return ClientEntity.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .dateOfBirth(dateOfBirth)
                .build();
    }
}
