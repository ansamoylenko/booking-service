package com.samoylenko.bookingservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link }
 */
@Getter
@Setter
@SuperBuilder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactDto extends BaseDto implements Serializable {
    @Pattern(message = "имя должно содержать только буквы", regexp = "^[A-ЯЁ][а-яё]+\\s[A-ЯЁ][а-яё]+$")
    @NotBlank(message = "имя не должно быть пустым")
    private String firstName;

    @Pattern(message = "фамилия должно содержать только буквы", regexp = "^[A-ЯЁ][а-яё]+\\s[A-ЯЁ][а-яё]+$")
    @NotBlank(message = "фамилия не должна быть пустой")
    private String lastName;

    @Email(message = "email должен быть валидным")
    @NotBlank(message = "email не должно быть пустым")
    private String email;

    @NotBlank(message = "телефон не должен быть пустым")
    private String phone;

    @NotNull
    @PastOrPresent(message = "дата рождения должна быть валидной")
    private LocalDateTime dateOfBirth;
}