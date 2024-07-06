package com.samoylenko.bookingservice.model.employee;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Generated
@ToString
@AllArgsConstructor
public enum EmployeeRole implements GrantedAuthority {
    ROLE_OWNER("Владелец"),
    ROLE_ADMIN("Администратор"),
    ROLE_MANAGER("Менеджер"),
    ROLE_INSTRUCTOR("Инструктор"),
    ROLE_ASSISTANT("Ассистент");

    private final String description;

    @Override
    public String getAuthority() {
        return this.name();
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    public static EmployeeRole fromDescription(String description) {
        if (description == null) {
            return null;
        }
        for (EmployeeRole role : EmployeeRole.values()) {
            if (role.getDescription().equals(description)) {
                return role;
            }
        }
        return null;
    }
}
