package com.company.model.dto.response;

import com.company.model.enums.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseWithPassword {
    private Long id;
    private String email;
    private UserRole userRole;
    private String hashedPassword;
}
