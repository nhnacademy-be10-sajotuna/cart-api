package com.dooray.bookstorecarts.repository;

import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByCart(Cart cart);
    CartItem findByCartAndBookId(Cart cart, int bookId);
    CartItem findById(int cartItemId);
}
