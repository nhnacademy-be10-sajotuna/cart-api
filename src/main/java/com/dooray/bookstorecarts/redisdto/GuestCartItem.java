package com.dooray.bookstorecarts.redisdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestCartItem {
    private Long bookId;
    private Long quantity;
}
