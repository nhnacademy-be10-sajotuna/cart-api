package com.dooray.bookstorecarts.repository;

import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;

public interface QuerydslRepository {
    CartItem findByCartAndIsbn(Cart cart, String isbn);
}
