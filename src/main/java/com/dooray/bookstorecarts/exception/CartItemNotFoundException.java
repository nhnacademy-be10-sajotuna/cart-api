package com.dooray.bookstorecarts.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CartItemNotFoundException extends ApiException {
    private static final String MESSAGE = "존재하지 않는 카트입니다 : ";

    public CartItemNotFoundException(int cartItemId) {
        super(HttpStatus.NOT_FOUND.value(), MESSAGE+ cartItemId);
    }
}
