package com.dooray.bookstorecarts.response;

import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MemberCartResponse {
    private Long cartId;
    private Long userId;
    private List<MemberCartItemResponse> items = new ArrayList<>();

    public MemberCartResponse(Cart cart, List<CartItem> items) {
        this.cartId = cart.getId();
        this.userId = cart.getUserId();

        for(CartItem cartItem : items) {
            this.items.add(new MemberCartItemResponse(cartItem));
        }
    }
}
