package com.samoylenko.bookingservice.model.employee;

import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto implements UserDetails {
    private String id;
    private Set<EmployeeRole> authorities;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private String password;
    boolean enabled;
    boolean accountNonExpired;
    boolean accountNonLocked;
    boolean credentialsNonExpired;

    @Override
    public String getUsername() {
        return email;
    }
}
