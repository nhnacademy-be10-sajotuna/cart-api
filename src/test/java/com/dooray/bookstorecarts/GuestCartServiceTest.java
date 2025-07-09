package com.dooray.bookstorecarts;

import com.dooray.bookstorecarts.exception.CartNotFoundException;
import com.dooray.bookstorecarts.redisdto.RedisGuestCartDto;
import com.dooray.bookstorecarts.redisdto.RedisGuestCartItemDto;
import com.dooray.bookstorecarts.repository.GuestCartRedisRepository;
import com.dooray.bookstorecarts.response.CartResponse;
import com.dooray.bookstorecarts.service.CartResponseService;
import com.dooray.bookstorecarts.service.GuestCartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GuestCartServiceTest {
    @Mock
    private GuestCartRedisRepository guestCartRedisRepository;
    @Mock
    private CartResponseService cartResponseService;
    @InjectMocks
    private GuestCartService guestCartService;

    @Test
    void getCartByCartId(){
        //given
        String cartId = "test-cart-id";
        RedisGuestCartItemDto item = new RedisGuestCartItemDto("1", 3L);
        RedisGuestCartDto mockGuestCart = new RedisGuestCartDto(cartId, List.of(item));
        given(guestCartRedisRepository.findByCartId(cartId)).willReturn(mockGuestCart);
        
        CartResponse mockResponse = new CartResponse();
        mockResponse.setCartId(cartId);
        given(cartResponseService.createFromGuestCart(mockGuestCart)).willReturn(mockResponse);
        
        //when
        CartResponse response = guestCartService.getCartByCartId(cartId);
        
        //then
        assertNotNull(response);
        assertEquals(cartId, response.getCartId());
        verify(cartResponseService).createFromGuestCart(mockGuestCart);
    }

    @Test
    void getCartByCartId_CartNotFound() {
        //given
        String cartId = "test-cart-id";
        given(guestCartRedisRepository.findByCartId(cartId)).willReturn(null);
        
        CartResponse mockResponse = new CartResponse();
        mockResponse.setCartId(cartId);
        mockResponse.setItems(new ArrayList<>());
        given(cartResponseService.createFromGuestCart(any(RedisGuestCartDto.class))).willReturn(mockResponse);
        
        //when
        CartResponse response = guestCartService.getCartByCartId(cartId);
        
        //then
        assertNotNull(response);
        assertEquals(cartId, response.getCartId());
        assertEquals(0, response.getItems().size());
    }

    @Test
    void deleteGuestCart(){
        //given
        String cartId = "test-cart-id";
        RedisGuestCartDto mockGuestCart = new RedisGuestCartDto(cartId, new ArrayList<>());
        given(guestCartRedisRepository.findByCartId(cartId)).willReturn(mockGuestCart);
        
        //when
        guestCartService.deleteGuestCart(cartId);
        
        //then
        verify(guestCartRedisRepository).deleteByCartId(cartId);
    }
    
    @Test
    void deleteGuestCart_CartNotFound() {
        //given
        String cartId = "test-cart-id";
        given(guestCartRedisRepository.findByCartId(cartId)).willReturn(null);
        
        //when & then
        assertThrows(CartNotFoundException.class, () -> guestCartService.deleteGuestCart(cartId));
    }
}