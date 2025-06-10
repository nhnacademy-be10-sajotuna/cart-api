package com.dooray.bookstorecarts.repository;

import com.dooray.bookstorecarts.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    Cart findById(int id);
    Cart findByUserId(int userId);
    Cart findBySessionId(String sessionId);
    boolean existsByUserId(int userId);
    boolean existsBySessionId(String sessionId);
}
