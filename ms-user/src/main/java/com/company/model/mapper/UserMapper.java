package com.company.model.mapper;

import com.company.dao.entity.User;
import com.company.model.dto.request.RegistrationRequest;
import com.company.model.dto.response.UserResponse;
import com.company.model.dto.response.UserResponseWithPassword;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface UserMapper {

    @Mapping(target = "password", source = "password", qualifiedByName = "encryptPassword")
    @Mapping(target = "userRole", expression = "java(com.company.model.enums.UserRole.USER)")
    User toUser(RegistrationRequest request, @Context PasswordEncoder encoder);

    UserResponse toUserResponse(User user);

   // @Mapping(target = "userRole", expression = "java(com.company.model.enums.UserRole.USER)")
    @Mapping(target = "hashedPassword", source = "password")
    UserResponseWithPassword toUserResponseWithPassword(User user, @Context PasswordEncoder encoder);

    @Named("encryptPassword")
    default String encryptPassword(String password, @Context PasswordEncoder encoder) {
        return encoder.encode(password);
    }
}
