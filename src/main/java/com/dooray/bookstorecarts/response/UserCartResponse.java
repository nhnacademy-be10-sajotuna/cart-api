package com.dooray.bookstorecarts.response;

import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserCartResponse {
    private Long cartId;
    private Long userId;
    private List<UserCartItemResponse> items = new ArrayList<>();

    public UserCartResponse(Cart cart, List<CartItem> items) {
        this.cartId = cart.getId();
        this.userId = cart.getUserId();

        for(CartItem cartItem : items) {
            this.items.add(new UserCartItemResponse(cartItem));
        }
    }
}
