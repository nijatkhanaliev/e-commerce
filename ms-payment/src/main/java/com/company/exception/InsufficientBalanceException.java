package com.company.exception;

import lombok.Getter;

@Getter
public class InsufficientBalanceException extends RuntimeException{

    private final String errorMessage;
    private final String errorCode;

    public InsufficientBalanceException(String errorMessage, String errorCode){
        super(errorMessage);

        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }

}
