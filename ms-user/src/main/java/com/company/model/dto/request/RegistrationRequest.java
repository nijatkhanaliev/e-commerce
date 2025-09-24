package com.company.model.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RegistrationRequest {
    @Pattern(regexp = "[a-zA-ZƏÖĞIŞÇ]+" , message = "FirstName is invalid")
    private String firstName;

    @Pattern(regexp = "[a-zA-ZƏÖĞIŞÇ]+", message = "LastName is invalid")
    private String lastName;

    @Email(message = "Email is invalid")
    private String email;

    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "Password must be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, and one number."
    )
    private String password;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate birthDate;
}
