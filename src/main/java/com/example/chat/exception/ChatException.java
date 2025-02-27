
package com.example.chat.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ChatException extends RuntimeException{

    private final HttpStatus status;
    private final ErrorCode errorCode;
    private final String detail;

    public ChatException(HttpStatus status, ErrorCode errorCode, String detail) {
        this.status = status;
        this.errorCode = errorCode;
        this.detail = detail;
    }

    public ChatException(HttpStatus status, ErrorCode errorCode) {
        this.status = status;
        this.errorCode = errorCode;
        this.detail = "";
    }
}
