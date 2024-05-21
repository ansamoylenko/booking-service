package com.samoylenko.bookingservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Generated;
import lombok.ToString;

@ToString
@Generated
@AllArgsConstructor
public enum EmployeeRole {
    INSTRUCTOR("Инструктор"),
    ASSISTANT("Ассистент");

    private final String description;
}
