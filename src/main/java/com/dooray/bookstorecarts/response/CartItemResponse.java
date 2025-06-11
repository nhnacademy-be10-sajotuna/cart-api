package com.dooray.bookstorecarts.response;

import com.dooray.bookstorecarts.entity.CartItem;
import lombok.Data;

@Data
public class CartItemResponse {
    private int cartItemId;
    private int bookId;
    private int quantity;

    public CartItemResponse(CartItem cartItem) {
        this.cartItemId = cartItem.getId();
        this.bookId = cartItem.getBookId();
        this.quantity = cartItem.getQuantity();
    }
}
