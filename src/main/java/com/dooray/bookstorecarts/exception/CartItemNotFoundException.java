package com.dooray.bookstorecarts.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CartItemNotFoundException extends ApiException {
    private static final String MESSAGE = "카트아이디가 존재하지 않습니다 : ";

    public CartItemNotFoundException(int cartItemId) {
        super(HttpStatus.NOT_FOUND.value(), MESSAGE+ cartItemId);
    }

    public CartItemNotFoundException() {
        super(HttpStatus.NOT_FOUND.value(), MESSAGE+ "아이템 없음");
    }
}
