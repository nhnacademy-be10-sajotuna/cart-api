package com.dooray.bookstorecarts.exception;

import org.springframework.http.HttpStatus;

public class CartMergeException extends ApiException {

    public CartMergeException(String message) {
        super(HttpStatus.CONFLICT.value(), message);
    }
}
