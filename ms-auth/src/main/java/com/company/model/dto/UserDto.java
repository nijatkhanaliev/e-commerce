package com.company.model.dto;

import com.company.model.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private String firstName;
    private String lastName;
    private String email;
    private String hashedPassword;
    private UserRole userRole;
}
