package com.company.service.impl;

import com.company.dao.entity.User;
import com.company.dao.repository.UserRepository;
import com.company.exception.AlreadyExistsException;
import com.company.exception.NotFoundException;
import com.company.messaging.AccountProducer;
import com.company.model.dto.AccountCreatedEvent;
import com.company.model.dto.request.RegistrationRequest;
import com.company.model.dto.response.UserResponse;
import com.company.model.dto.response.UserResponseWithPassword;
import com.company.model.mapper.UserMapper;
import com.company.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.company.config.RabbitMQConfig.ACCOUNT_EXCHANGE;
import static com.company.exception.constant.ErrorCode.ALREADY_EXISTS;
import static com.company.exception.constant.ErrorCode.DATA_NOT_FOUND;
import static com.company.exception.constant.ErrorMessage.ALREADY_EXISTS_MESSAGE;
import static com.company.exception.constant.ErrorMessage.DATA_NOT_FOUND_MESSAGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AccountProducer accountProducer;

    @Override
    public void register(RegistrationRequest request) {
        log.info("Registering user, userEmail {}", request.getEmail());
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistsException(ALREADY_EXISTS_MESSAGE, ALREADY_EXISTS);
        }

        User user = userMapper.toUser(request, passwordEncoder);
        User savedUser = userRepository.save(user);

        AccountCreatedEvent accountCreatedEvent = new AccountCreatedEvent();
        accountCreatedEvent.setUserId(savedUser.getId());

        accountProducer.send(ACCOUNT_EXCHANGE, "account.created", accountCreatedEvent);
    }

    @Override
    public UserResponse getUserById(Long userId) {
        log.info("Getting user by id, ID {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(DATA_NOT_FOUND_MESSAGE, DATA_NOT_FOUND));

        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponseWithPassword getUserByEmail(String email) {
        log.info("Getting user by email, userEmail {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(DATA_NOT_FOUND_MESSAGE, DATA_NOT_FOUND));

        return userMapper.toUserResponseWithPassword(user, passwordEncoder);
    }

    @Override
    public Boolean userExists(Long id) {
        log.info("User exists method called, userId {}", id);

        return userRepository.existsById(id);
    }

}
