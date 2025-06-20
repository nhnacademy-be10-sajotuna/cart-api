package com.dooray.bookstorecarts.repository;

import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public interface UserCartItemRepository extends JpaRepository<CartItem, Long>, QuerydslRepository {
    List<CartItem> findByCart(Cart cart);

}
