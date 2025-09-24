package com.company.client;

import com.company.model.dto.UserDto;
import com.company.model.dto.request.RegistrationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "USER-SERVICE", url = "http://localhost:8083/api/")
public interface UserClient {

    @PostMapping("v1/users")
    void register(@RequestBody RegistrationRequest request);

    @GetMapping("v1/users/email/{email}")
    UserDto getUserByEmail(@PathVariable String email);

}
