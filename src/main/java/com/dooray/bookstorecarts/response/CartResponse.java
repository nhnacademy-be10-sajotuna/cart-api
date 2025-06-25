package com.dooray.bookstorecarts.response;

import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;
import com.dooray.bookstorecarts.redisdto.RedisGuestCartDto;
import com.dooray.bookstorecarts.redisdto.RedisGuestCartItemDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class CartResponse {
    private String cartId;
    private List<CartItemResponse> items = new ArrayList<>();

    public CartResponse(Cart cart, List<CartItem> items) {
        this.cartId = String.valueOf(cart.getId());

        for (CartItem item : items) {
            this.items.add(new CartItemResponse(item));
        }
    }

    public CartResponse(RedisGuestCartDto cart) {
        this.cartId = cart.getCartId();
        for(RedisGuestCartItemDto item : cart.getItems()) {
            this.items.add(new CartItemResponse(item));
        }
    }
}
