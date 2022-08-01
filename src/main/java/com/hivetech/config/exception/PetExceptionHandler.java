package com.hivetech.config.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDate;

@ControllerAdvice
public class PetExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handlerBadRequestException(BadRequestException e) {
        return ResponseEntity.badRequest().body(ExceptionResponse.builder()
                .message(e.getMessage())
                .date(LocalDate.now())
                .code(e.getCode())
                .build()
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handlerBadRequestException(NotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ExceptionResponse.builder()
                .message(e.getMessage())
                .date(LocalDate.now())
                .code(e.getCode())
                .build()
        );
    }


    @ExceptionHandler(BindException.class)
    public ResponseEntity<Object> validateFields(BindException e) {
        return ResponseEntity.badRequest().body(ExceptionResponse.builder()
                .message(e.getAllErrors().get(0).getDefaultMessage())
                .date(LocalDate.now())
                .code(HttpStatus.BAD_REQUEST.value())
                .build());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public String notFoundResource(Model model) {
        model.addAttribute("title", "404-NOT FOUND");
        return "error/error-404";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String accessDeniedResource(Model model) {
        model.addAttribute("title", "403-FORBIDDEN");
        return "error/error-403";
    }

}
