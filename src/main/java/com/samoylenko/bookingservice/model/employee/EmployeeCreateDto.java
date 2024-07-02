package com.samoylenko.bookingservice.model.employee;

import com.samoylenko.bookingservice.annotations.Phone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCreateDto {
    @Schema(description = "Роль сотрудника")
    @NotNull
    private EmployeeRole role;

    @Schema(description = "Имя", example = "Иван")
    @NotBlank(message = "{invalid.notBlank}")
    private String firstName;

    @Schema(description = "Фамилия", example = "Иванов")
    @NotBlank(message = "{invalid.notBlank}")
    private String lastName;

    @Schema(description = "Номер телефона клиента", example = "77999999999")
    @NotBlank(message = "{invalid.notBlank}")
    @Phone(message = "Номер телефона должен быть валидным")
    private String phone;
}
