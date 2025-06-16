package com.dooray.bookstorecarts.repository;

import com.dooray.bookstorecarts.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
// quarydsl -> 복잡한 쿼리
