package com.dooray.bookstorecarts.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CartNotFoundException extends ApiException {
    private static final String MESSAGE = "존재하지 않는 카트입니다 : ";

    public CartNotFoundException(String sessionId) {
        super(HttpStatus.NOT_FOUND.value(), MESSAGE+ sessionId);
    }

    public CartNotFoundException(int userId) {
        super(HttpStatus.NOT_FOUND.value(), MESSAGE+ userId);
    }
}
