package com.company.exception;

import lombok.Getter;

@Getter
public class InputNotValidException extends RuntimeException{

    private final String errorMessage;
    private final String errorCode;

    public InputNotValidException(String errorMessage , String errorCode){
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

}
