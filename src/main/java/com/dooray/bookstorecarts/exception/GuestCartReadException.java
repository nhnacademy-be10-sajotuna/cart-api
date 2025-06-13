package com.dooray.bookstorecarts.exception;

import org.springframework.http.HttpStatus;

public class GuestCartReadException extends ApiException {
    public GuestCartReadException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
    }
}
