package com.dooray.bookstorecarts.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CartAlreadyExistsException extends ApiException {
    private static final String MESSAGE = "이미 존재하는 카트입니다 : ";

    public CartAlreadyExistsException(String sessionId) {
        super(HttpStatus.CONFLICT.value(), MESSAGE+ sessionId);
    }

    public CartAlreadyExistsException(Long userId) {
        super(HttpStatus.CONFLICT.value(), MESSAGE+ userId);
    }
}
