package com.dooray.bookstorecarts.redisdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisGuestCartItemDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String bookId;
    private Long quantity;
}
