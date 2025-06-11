package com.dooray.bookstorecarts.exception;

import org.springframework.http.HttpStatus;

public class InvalidException extends ApiException {
    public InvalidException(String message) {
        super(HttpStatus.BAD_REQUEST.value(), message);
    }
}
