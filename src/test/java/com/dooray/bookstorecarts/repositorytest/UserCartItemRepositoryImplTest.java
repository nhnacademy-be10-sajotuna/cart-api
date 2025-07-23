package com.dooray.bookstorecarts.repositorytest;

import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;
import com.dooray.bookstorecarts.repository.UserCartItemRepositoryImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(UserCartItemRepositoryImpl.class)
@ActiveProfiles("test")
public class UserCartItemRepositoryImplTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    @Qualifier("userCartItemRepositoryImpl")
    UserCartItemRepositoryImpl repository;

    @Test
    void findByCartAndIsbn(){
        // given
        Cart cart = new Cart();
        entityManager.persist(cart);

        CartItem cartItem = new CartItem("isbn", 5L, cart);
        entityManager.persist(cartItem);

        entityManager.flush();
        entityManager.clear();

        // when
        CartItem item = repository.findByCartAndIsbn(cart, "isbn");

        // then
        assertThat(item).isNotNull();
        assertThat(item.getIsbn()).isEqualTo("isbn");

    }
}
