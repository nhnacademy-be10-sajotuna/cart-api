package com.dooray.bookstorecarts.repository;

import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;
import com.dooray.bookstorecarts.entity.QCartItem;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
public class UserCartItemRepositoryImpl extends QuerydslRepositorySupport implements QuerydslRepository {
    private final JPAQueryFactory queryFactory;

    public UserCartItemRepositoryImpl(EntityManager em) {
        super(CartItem.class);
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public CartItem findByCartAndBookId(Cart cart, Long bookId) {
        QCartItem cartItem = QCartItem.cartItem;

        return queryFactory
                .selectFrom(cartItem)
                .where(cartItem.cart.eq(cart)
                        .and(cartItem.bookId.eq(bookId)))
                .fetchOne();
    }
}
