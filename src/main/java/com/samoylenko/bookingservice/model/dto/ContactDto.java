package com.samoylenko.bookingservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactDto extends BaseDto implements Serializable {
    @Pattern(message = "имя должно содержать только буквы", regexp = "^[A-ЯЁ][а-яё]+\\s[A-ЯЁ][а-яё]+$")
    @NotBlank(message = "{invalid.notBlank}")
    private String firstName;

    @Pattern(message = "фамилия должно содержать только буквы", regexp = "^[A-ЯЁ][а-яё]+\\s[A-ЯЁ][а-яё]+$")
    @NotBlank(message = "{invalid.notBlank}")
    private String lastName;

    @Email(message = "email должен быть валидным")
    @NotBlank(message = "{invalid.notBlank}")
    private String email;

    @NotBlank(message = "{invalid.notBlank}")
    private String phone;

    @NotNull
    @PastOrPresent(message = "дата рождения должна быть валидной")
    private LocalDateTime dateOfBirth;
}