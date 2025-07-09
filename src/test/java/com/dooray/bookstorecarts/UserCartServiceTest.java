package com.dooray.bookstorecarts;

import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;
import com.dooray.bookstorecarts.exception.CartNotFoundException;
import com.dooray.bookstorecarts.redisdto.RedisCartDto;
import com.dooray.bookstorecarts.repository.UserCartItemRepository;
import com.dooray.bookstorecarts.repository.UserCartRedisRepository;
import com.dooray.bookstorecarts.repository.UserCartRepository;
import com.dooray.bookstorecarts.response.CartResponse;
import com.dooray.bookstorecarts.service.CartResponseService;
import com.dooray.bookstorecarts.service.UserCartItemService;
import com.dooray.bookstorecarts.service.UserCartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserCartServiceTest {
    @Mock
    private UserCartRepository userCartRepository;
    @Mock
    private UserCartItemRepository userCartItemRepository;
    @Mock
    private UserCartItemService userCartItemService;
    @Mock
    private UserCartRedisRepository userCartRedisRepository;
    @Mock
    private CartResponseService cartResponseService;
    @InjectMocks
    private UserCartService userCartService;

    @Test
    void getCartByUserId() {
        // Given - 테스트할 상황준비(입력, 가짜 리턴값 등)
        Long userId = 22L;
        Cart mockCart = new Cart();
        mockCart.setId(1L);
        mockCart.setUserId(userId);

        CartItem mockCartItem = new CartItem();
        mockCartItem.setId(1L);
        mockCartItem.setIsbn("1");
        mockCartItem.setQuantity(5L);

        List<CartItem> items = List.of(mockCartItem);
        given(userCartRedisRepository.findByUserId(userId)).willReturn(null);
        given(userCartRepository.findByUserId(userId)).willReturn(Optional.of(mockCart));
        given(userCartItemRepository.findByCart(mockCart)).willReturn(items);

        CartResponse mockResponse = new CartResponse();
        mockResponse.setCartId(String.valueOf(mockCart.getId()));
        given(cartResponseService.createFromUserCart(mockCart, items)).willReturn(mockResponse);

        // When - 실제 대상 메서드 호출
        CartResponse response = userCartService.getCartByUserId(userId);

        // Then - 예상 결과 검증
        assertNotNull(response);
        assertEquals(String.valueOf(mockCart.getId()), response.getCartId());

        verify(userCartRedisRepository).save(any(RedisCartDto.class));
        verify(cartResponseService).createFromUserCart(mockCart, items);
    }

    @Test
    void deleteUserCart() {
        // Given
        Long userId = 22L;
        Cart mockCart = new Cart();
        mockCart.setId(100L);
        mockCart.setUserId(userId);

        given(userCartRepository.findByUserId(userId)).willReturn(Optional.of(mockCart));

        // When
        userCartService.deleteUserCart(userId);

        // Then
        verify(userCartRepository).findByUserId(userId);
        verify(userCartItemService).deleteAllCartItemsFromUserId(userId);
        verify(userCartRepository).delete(mockCart);
        verify(userCartRedisRepository).deleteByUserId(userId);
    }

    @Test
    void getCartByUserId_CartNotFound() {
        Long userId = 22L;
        given(userCartRepository.findByUserId(userId)).willReturn(Optional.empty());
        assertThrows(CartNotFoundException.class, () -> userCartService.getCartByUserId(userId));
    }

    @Test
    void deleteUserCart_CartNotFound() {
        Long userId = 22L;
        given(userCartRepository.findByUserId(userId)).willReturn(Optional.empty());
        assertThrows(CartNotFoundException.class, () -> userCartService.deleteUserCart(userId));
    }

}



