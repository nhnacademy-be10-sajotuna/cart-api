package com.dooray.bookstorecarts.response;

import com.dooray.bookstorecarts.entity.CartItem;
import com.dooray.bookstorecarts.redisdto.RedisGuestCartItemDto;
import lombok.Data;

@Data
public class CartItemResponse {
    private Long cartItemId;
    private Long bookId;
    private Long quantity;

    public CartItemResponse(CartItem cartItem) {
        this.cartItemId = cartItem.getId();
        this.bookId = cartItem.getBookId();
        this.quantity = cartItem.getQuantity();
    }

    public CartItemResponse(RedisGuestCartItemDto cartItem) {
        this.cartItemId = null;
        this.bookId = cartItem.getBookId();
        this.quantity = cartItem.getQuantity();
    }
}
