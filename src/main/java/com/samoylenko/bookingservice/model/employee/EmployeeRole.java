package com.samoylenko.bookingservice.model.employee;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.Getter;

@Getter
@Generated
@AllArgsConstructor
public enum EmployeeRole {
    INSTRUCTOR("Инструктор"),
    ASSISTANT("Ассистент"),
    MANAGER("Менеджер");

    private final String description;

    @Override
    public String toString() {
        return description;
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
