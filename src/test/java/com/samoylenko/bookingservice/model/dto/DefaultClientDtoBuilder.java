package com.samoylenko.bookingservice.model.dto;

import com.samoylenko.bookingservice.model.dto.client.ClientCreateDto;
import com.samoylenko.bookingservice.model.entity.DefaultEntityBuilder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.LocalDate;

@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "of")
public class DefaultClientDtoBuilder implements DefaultEntityBuilder<ClientCreateDto> {
    private String firstName = "Alexander";
    private String lastName = "Samoylenko";
    private String email = "a.n.samoylenko@outlook.com";
    private String phone = "79999999999";
    private LocalDate dateOfBirth = LocalDate.parse("1998-09-15");

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
