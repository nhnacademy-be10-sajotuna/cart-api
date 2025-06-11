package com.dooray.bookstorecarts.response;

import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CartResponse {
    private int cartId;
    private int userId;
    private String sessionId;
    private List<CartItemResponse> items = new ArrayList<>();

    public CartResponse(Cart cart, List<CartItem> items) {
        this.cartId = cart.getId();
        this.userId = cart.getUserId();
        this.sessionId = cart.getSessionId();

        for(CartItem cartItem : items) {
            this.items.add(new CartItemResponse(cartItem));
        }
    }
}
