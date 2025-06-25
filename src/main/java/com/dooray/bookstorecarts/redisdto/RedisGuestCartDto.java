package com.dooray.bookstorecarts.redisdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedisGuestCartDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String cartId;
    private List<RedisGuestCartItemDto> items;

}
