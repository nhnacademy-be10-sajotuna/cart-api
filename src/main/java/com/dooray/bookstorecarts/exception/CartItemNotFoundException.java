package com.dooray.bookstorecarts.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CartItemNotFoundException extends ApiException {
    private static final String MESSAGE_CART_ITEM = "카트아이디가 존재하지 않습니다 : ";
    private static final String MESSAGE_BOOK = "존재하지 않는 책입니다 : ";

    // private 생성자 하나만 둠
    private CartItemNotFoundException(int status, String message) {
        super(status, message);
    }

    public static CartItemNotFoundException forCartItemId(Long cartItemId) {
        return new CartItemNotFoundException(HttpStatus.NOT_FOUND.value(), MESSAGE_CART_ITEM + cartItemId);
    }

    public static CartItemNotFoundException forBookId(Long bookId) {
        return new CartItemNotFoundException(HttpStatus.NOT_FOUND.value(), MESSAGE_BOOK + bookId);
    }
}
