package com.hivetech.config.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class NotFoundException extends RuntimeException {
    @Getter
    private int code;
    private String message;

    public NotFoundException(String message) {
        super(message);
        this.code = HttpStatus.NOT_FOUND.value();
    }

}
