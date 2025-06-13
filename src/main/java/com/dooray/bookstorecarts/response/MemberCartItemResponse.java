package com.dooray.bookstorecarts.response;

import com.dooray.bookstorecarts.entity.CartItem;
import lombok.Data;

@Data
public class MemberCartItemResponse {
    private Long cartItemId;
    private Long bookId;
    private Long quantity;

    public MemberCartItemResponse(CartItem cartItem) {
        this.cartItemId = cartItem.getId();
        this.bookId = cartItem.getBookId();
        this.quantity = cartItem.getQuantity();
    }
}
