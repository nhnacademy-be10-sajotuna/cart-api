package com.dooray.bookstorecarts.response;

import com.dooray.bookstorecarts.redisdto.GuestCartItem;
import lombok.Data;

@Data
public class GuestCartItemResponse {
    private Long bookId;
    private Long quantity;

    public GuestCartItemResponse(GuestCartItem item) {
        this.bookId = item.getBookId();
        this.quantity = item.getQuantity();
    }
}