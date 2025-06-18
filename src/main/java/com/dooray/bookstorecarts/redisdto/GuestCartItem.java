package com.dooray.bookstorecarts.redisdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GuestCartItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long bookId;
    private Long quantity;
}
