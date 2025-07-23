package com.dooray.bookstorecarts.servicetest;

import com.dooray.bookstorecarts.exception.CartNotFoundException;
import com.dooray.bookstorecarts.redisdto.RedisGuestCartDto;
import com.dooray.bookstorecarts.repository.GuestCartRedisRepository;
import com.dooray.bookstorecarts.response.CartResponse;
import com.dooray.bookstorecarts.service.CartResponseService;
import com.dooray.bookstorecarts.service.GuestCartService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class GuestCartServiceTest {

    @Mock
    private GuestCartRedisRepository guestCartRedisRepository;

    @Mock
    private CartResponseService cartResponseService;

    @InjectMocks
    private GuestCartService guestCartService;

    @Test
    @DisplayName("비회원 장바구니 조회 - 레디스에 비회원 장바구니가 있을경우")
    void getGuestCartByCartId_existingGuestCart() {
        // given
        String cartId = "guestCartId";
        RedisGuestCartDto guestCart = new RedisGuestCartDto(cartId, new ArrayList<>());
        CartResponse cartResponse = new CartResponse();

        when(guestCartRedisRepository.findByCartId(cartId)).thenReturn(guestCart);
        when(cartResponseService.createFromGuestCart(guestCart)).thenReturn(cartResponse);

        // when
        CartResponse result = guestCartService.getCartByCartId(cartId);

        // then
        assertEquals(cartResponse, result);
        verify(guestCartRedisRepository,never()).save(any());
    }

    @Test
    @DisplayName("비회원 장바구니 조회 - 레디스에 비회원 장바구니가 없을 경우 새로 생성")
    void getGuestCartByCartId_notExistingGuestCart() {
        // given
        String cartId = "guestCartId";
        CartResponse cartResponse = new CartResponse();

        when(guestCartRedisRepository.findByCartId(cartId)).thenReturn(null);
        when(cartResponseService.createFromGuestCart(any())).thenReturn(cartResponse);

        // when
        CartResponse result = guestCartService.getCartByCartId(cartId);

        // then
        assertEquals(cartResponse, result);
        verify(guestCartRedisRepository).save(any());

    }

    @Test
    @DisplayName("비회원 카트삭제 - 레디스에 비회원 장바구니가 있는 경우")
    void deleteGuestCartByCartId_existingGuestCart() {
        // given
        String cartId = "guestCartId";
        RedisGuestCartDto guestCart = new RedisGuestCartDto(cartId, new ArrayList<>());

        when(guestCartRedisRepository.findByCartId(cartId)).thenReturn(guestCart);

        // when
        guestCartService.deleteGuestCart(cartId);

        // then
        verify(guestCartRedisRepository).deleteByCartId(cartId);
    }

    @Test
    @DisplayName("비회원 카트삭제 - 레디스에 비회원 장바구니가 없는 경우(예외던짐)")
    void deleteGuestCartByCartId_notExistingGuestCart() {
        // given
        String cartId = "guestCartId";

        when(guestCartRedisRepository.findByCartId(cartId)).thenReturn(null);

        // when then
        assertThrows(CartNotFoundException.class, () -> guestCartService.deleteGuestCart(cartId));

    }
}
