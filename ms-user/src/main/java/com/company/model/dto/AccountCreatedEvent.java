package com.company.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class AccountCreatedEvent {
    private String eventId = UUID.randomUUID().toString();
    private Long userId;
}
