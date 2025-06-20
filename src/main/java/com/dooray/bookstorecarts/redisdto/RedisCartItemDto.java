package com.dooray.bookstorecarts.redisdto;

import com.dooray.bookstorecarts.entity.CartItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedisCartItemDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long cartItemId;
    private Long bookId;
    private Long quantity;

    public static RedisCartItemDto from(CartItem item) {
        return new RedisCartItemDto(
                item.getId(),
                item.getBookId(),
                item.getQuantity()
        );
    }
}
