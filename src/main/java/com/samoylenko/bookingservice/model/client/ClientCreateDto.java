package com.samoylenko.bookingservice.model.client;

import com.samoylenko.bookingservice.annotations.Phone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ClientCreateDto {
    @Schema(description = "Имя клиента", example = "Иван")
    @Pattern(message = "имя должно содержать только буквы", regexp = "^[A-ЯЁ][а-яё]+\\s[A-ЯЁ][а-яё]+$")
    @NotBlank(message = "{invalid.notBlank}")
    private String firstName;

    @Schema(description = "Фамилия клиента", example = "Иванов")
    @Pattern(message = "фамилия должно содержать только буквы", regexp = "^[A-ЯЁ][а-яё]+\\s[A-ЯЁ][а-яё]+$")
    @NotBlank(message = "{invalid.notBlank}")
    private String lastName;

    @Schema(description = "Email клиента", example = "ivan.ivanov@gmail.com")
    @Email(message = "email должен быть валидным")
    @NotBlank(message = "{invalid.notBlank}")
    private String email;

    @Schema(description = "Номер телефона клиента", example = "79999999999")
    @NotBlank(message = "{invalid.notBlank}")
    @Phone(message = "Номер телефона должен быть валидным")
    private String phone;

    @Schema(description = "Дата рождения клиента", example = "1990-01-01")
    @NotNull
    private LocalDate dateOfBirth;
}
