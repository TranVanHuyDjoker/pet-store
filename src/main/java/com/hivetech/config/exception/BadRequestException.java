package com.hivetech.config.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class BadRequestException extends RuntimeException {
    @Getter
    private int code;
    private String message;

    public BadRequestException(String message) {
        super(message);
        this.code = HttpStatus.BAD_REQUEST.value();
    }

    public BadRequestException(String message, Throwable throwable) {
        super(message, throwable);
    }
}