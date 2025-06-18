package com.dooray.bookstorecarts.redisdto;

import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedisCartDto implements Serializable{
    private static final long serialVersionUID = 1L;

    private Long cartId;
    private Long userId;
    private List<RedisCartItemDto> items;

    public static RedisCartDto from(Cart cart, List<CartItem> cartItems) {
        List<RedisCartItemDto> item = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            item.add(RedisCartItemDto.from(cartItem));
        }
        return new RedisCartDto(cart.getId(), cart.getUserId(), item);
    }
}
