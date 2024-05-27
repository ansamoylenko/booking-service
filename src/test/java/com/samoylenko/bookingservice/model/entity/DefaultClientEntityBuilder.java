package com.samoylenko.bookingservice.model.entity;

import com.samoylenko.bookingservice.model.dto.client.ClientCreateDto;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.LocalDateTime;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "of")
public class DefaultClientEntityBuilder implements DefaultEntityBuilder<ClientCreateDto> {
    private String firstName = "Alexander";
    private String lastName = "Samoylenko";
    private String email = "a.n.samoylenko@outlook.com";
    private String phone = "79999999999";
    private LocalDateTime dateOfBirth = LocalDateTime.parse("1998-09-15T00:00:01");

    @Override
    public ClientCreateDto build() {
        return ClientCreateDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .build();
    }
}
