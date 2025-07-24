package com.dooray.bookstorecarts.servicetest;

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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserCartServiceTest {
    @Mock
    private UserCartRepository userCartRepository;
    @Mock
    private UserCartItemService userCartItemService;
    @Mock
    private UserCartItemRepository userCartItemRepository;
    @Mock
    private UserCartRedisRepository userCartRedisRepository;
    @Mock
    private CartResponseService cartResponseService;

    @InjectMocks
    private UserCartService userCartService;

    @Test
    @DisplayName("회원 장바구니 조회 - 레디스에 회원 장바구니가 있을경우")
    void getCartByCartId_existingRedisCart() throws Exception {
        // given
        Long userId = 33L;
        CartResponse cartResponse = new CartResponse();
        RedisCartDto redisCartDto = new RedisCartDto(5L,userId, new ArrayList<>());

        when(userCartRedisRepository.findByUserId(userId)).thenReturn(redisCartDto);
        when(cartResponseService.createFromUserCart(eq(redisCartDto), anyList())).thenReturn(cartResponse);

        // when
        CartResponse result = userCartService.getCartByUserId(userId);

        // then
        Assertions.assertEquals(cartResponse, result);
        verify(userCartRedisRepository).findByUserId(userId);
        verify(cartResponseService).createFromUserCart(eq(redisCartDto), anyList());
    }


    @Test
    @DisplayName("회원 장바구니 조회 - 레디스에 회원 장바구니가 없을경우")
    void getCartByCartId_notExistingRedisCart() throws Exception {
        // given
        Long userId = 33L;
        CartResponse cartResponse = new CartResponse();
        Cart cart = new Cart(userId);
        List<CartItem> cartItems = new ArrayList<>();

        when(userCartRedisRepository.findByUserId(userId)).thenReturn(null);
        when(userCartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(userCartItemRepository.findByCart(cart)).thenReturn(cartItems);
        when(cartResponseService.createFromUserCart(any(RedisCartDto.class),eq(cartItems))).thenReturn(cartResponse);

        // when
        CartResponse result = userCartService.getCartByUserId(userId);

        // then
        Assertions.assertEquals(cartResponse, result);
        verify(userCartRedisRepository).findByUserId(userId);
        verify(userCartRepository).findByUserId(userId);
        verify(userCartItemRepository).findByCart(cart);
        verify(userCartRedisRepository).save(any(RedisCartDto.class));
        verify(cartResponseService).createFromUserCart(any(RedisCartDto.class),eq(cartItems));
    }

    @Test
    @DisplayName("회원 장바구니 삭제 - 장바구니가 있을때")
    void deleteUserCart_existingCart() throws Exception {
        // given
        Long userId = 33L;
        Cart cart = new Cart(userId);

        given(userCartRepository.findByUserId(userId)).willReturn(Optional.of(cart));

        // when
        userCartService.deleteUserCart(userId);

        // then
        verify(userCartItemService).deleteAllCartItemsFromUserId(userId);
        verify(userCartRepository).delete(cart);
        verify(userCartRedisRepository).deleteByUserId(userId);
    }

    @Test
    @DisplayName("회원 장바구니 삭제 - 장바구니가 없을때")
    void deleteUserCart_notExistingCart() throws Exception {
        // given
        Long userId = 33L;
        given(userCartRepository.findByUserId(userId)).willReturn(Optional.empty());

        // when then
        assertThrows(CartNotFoundException.class, () -> {
            userCartService.deleteUserCart(userId);
        });
    }
}
