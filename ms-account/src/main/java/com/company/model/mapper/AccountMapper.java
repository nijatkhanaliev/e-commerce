package com.company.model.mapper;

import com.company.dao.entity.Account;
import com.company.model.dto.response.AccountResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = IGNORE)
public interface AccountMapper {

    @Mapping(target = "status", expression = "java(com.company.model.enums.AccountStatus.ACTIVE)")
    AccountResponse toAccountResponse(Account account);

}
