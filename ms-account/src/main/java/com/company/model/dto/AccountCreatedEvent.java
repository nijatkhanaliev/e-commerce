package com.company.model.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AccountCreatedEvent {
    private String eventId;
    private Long userId;
}
