package com.dooray.bookstorecarts.response;

import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;
import com.dooray.bookstorecarts.feign.BookFeignClient;
import com.dooray.bookstorecarts.feign.BookResponse;
import com.dooray.bookstorecarts.redisdto.RedisGuestCartDto;
import com.dooray.bookstorecarts.redisdto.RedisGuestCartItemDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class CartResponse {
    private String cartId;
    private List<CartItemResponse> items = new ArrayList<>();

    public CartResponse(Cart cart, List<CartItem> items, BookFeignClient bookFeignClient) {
        this.cartId = String.valueOf(cart.getId());

        for (CartItem item : items) {
            BookResponse book = bookFeignClient.getBook(item.getBookId());
            this.items.add(new CartItemResponse(item, book));
        }
    }

    public CartResponse(RedisGuestCartDto cart, BookFeignClient bookFeignClient) {
        this.cartId = cart.getCartId();
        for(RedisGuestCartItemDto item : cart.getItems()) {
            BookResponse book = bookFeignClient.getBook(item.getBookId());
            this.items.add(new CartItemResponse(item, book));
        }
    }
}
