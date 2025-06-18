package com.dooray.bookstorecarts;

import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;
import com.dooray.bookstorecarts.repository.UserCartItemRepository;
import com.dooray.bookstorecarts.repository.UserCartRepository;
import com.dooray.bookstorecarts.request.CartItemRequest;
import com.dooray.bookstorecarts.service.UserCartItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
public class UserCartItemServiceTest {
    @Mock
    private UserCartRepository userCartRepository;
    @Mock
    private UserCartItemRepository userCartItemRepository;
    @InjectMocks
    private UserCartItemService userCartItemService;

    @Test // 기존 카트가 있고, 같은 책이 있을 때 → 수량 증가
    void createUserCartItem_whenCartExistsAndSameBookExists_thenQuantityUpdated(){
        // given
        Long userId = 22L;
        Long bookId = 1L;
        Long quantityToAdd = 2L;

        Cart mockCart = new Cart();
        mockCart.setId(1L);
        mockCart.setUserId(userId);

        CartItem existingItem = new CartItem();
        existingItem.setId(1L);
        existingItem.setBookId(bookId);
        existingItem.setQuantity(3L);

        CartItemRequest request = new CartItemRequest();
        request.setQuantity(bookId);
        request.setQuantity(quantityToAdd);

        given(userCartRepository.findByUserId(userId)).willReturn(Optional.of(mockCart));
    }

    @Test // 카트가 없거나, 같은 책이 없을 때 → 새 아이템 추가
    void createUserCartItem_whenCartNotExistOrNewBook_thenCreateNewItem() {

    }
}
