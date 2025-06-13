package com.dooray.bookstorecarts.response;

import com.dooray.bookstorecarts.redisdto.GuestCart;
import com.dooray.bookstorecarts.redisdto.GuestCartItem;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GuestCartResponse {
    private String sessionId;
    private List<GuestCartItemResponse> items;

    public GuestCartResponse(GuestCart guestCart) {
        this.sessionId = guestCart.getSessionId();
        this.items = new ArrayList<>();

        for(GuestCartItem item : guestCart.getItems()) {
            this.items.add(new GuestCartItemResponse(item));
        }
    }
}
