package com.dooray.bookstorecarts.repository;

import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;

public interface QuerydslRepository {
    CartItem findByCartAndBookId(Cart cart, Long bookId);
}
