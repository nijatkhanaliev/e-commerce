package com.company.service;

import com.company.model.dto.request.RegistrationRequest;
import com.company.model.dto.response.UserResponse;
import com.company.model.dto.response.UserResponseWithPassword;

public interface UserService {

    void register(RegistrationRequest request);

    UserResponse getUserById(Long userId);

    UserResponseWithPassword getUserByEmail(String email);

    Boolean userExists(Long id);
}
