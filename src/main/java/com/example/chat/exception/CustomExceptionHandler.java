package com.example.chat.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(ChatException.class)
    protected ResponseEntity<ErrorDto> handleCustom400Exception(ChatException ex) {
        return ErrorDto.toResponseEntity(ex);
    }

}