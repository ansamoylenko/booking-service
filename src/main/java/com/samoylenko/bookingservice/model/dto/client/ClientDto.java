package com.samoylenko.bookingservice.model.dto.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.samoylenko.bookingservice.model.dto.BaseDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link }
 */
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientDto extends BaseDto implements Serializable {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
}