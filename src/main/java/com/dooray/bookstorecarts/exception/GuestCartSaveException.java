package com.dooray.bookstorecarts.exception;

import org.springframework.http.HttpStatus;

public class GuestCartSaveException extends ApiException {
    public GuestCartSaveException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
    }
}
