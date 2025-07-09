package com.dooray.bookstorecarts;

import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;
import com.dooray.bookstorecarts.redisdto.RedisGuestCartDto;
import com.dooray.bookstorecarts.redisdto.RedisGuestCartItemDto;
import com.dooray.bookstorecarts.repository.GuestCartRedisRepository;
import com.dooray.bookstorecarts.repository.UserCartItemRepository;
import com.dooray.bookstorecarts.repository.UserCartRedisRepository;
import com.dooray.bookstorecarts.repository.UserCartRepository;
import com.dooray.bookstorecarts.response.CartResponse;
import com.dooray.bookstorecarts.service.CartResponseService;
import com.dooray.bookstorecarts.service.CartService;
import com.dooray.bookstorecarts.service.GuestCartService;
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
public class CartServiceTest {
    @Mock
    private GuestCartService guestCartService;
    @Mock
    private UserCartRepository userCartRepository;
    @Mock
    private UserCartItemRepository userCartItemRepository;
    @Mock
    private UserCartRedisRepository userCartRedisRepository;
    @Mock
    private GuestCartRedisRepository guestCartRedisRepository;
    @Mock
    private CartResponseService cartResponseService;
    @InjectMocks
    private CartService cartService;

    @Test
    void mergeCarts(){
        // given
        Long userId = 22L;
        String cartId = "test-cart-id";

        RedisGuestCartItemDto item = new RedisGuestCartItemDto("1", 3L);
        RedisGuestCartDto mockGuestCart = new RedisGuestCartDto(cartId, List.of(item));
        given(guestCartRedisRepository.findByCartId(cartId)).willReturn(mockGuestCart);

        Cart mockCart = new Cart();
        mockCart.setId(100L);
        mockCart.setUserId(userId);
        given(userCartRepository.findByUserId(userId)).willReturn(Optional.of(mockCart));

        given(userCartItemRepository.findByCartAndIsbn(mockCart,"1")).willReturn(null);

        CartItem cartItem = new CartItem();
        cartItem.setId(200L);
        cartItem.setIsbn("1");
        cartItem.setQuantity(3L);
        cartItem.setCart(mockCart);
        given(userCartItemRepository.save(any())).willReturn(cartItem);

        given(userCartItemRepository.findByCart(mockCart)).willReturn(List.of(cartItem));

        CartResponse mockResponse = new CartResponse();
        mockResponse.setCartId(String.valueOf(mockCart.getId()));
        given(cartResponseService.createFromUserCart(mockCart, List.of(cartItem))).willReturn(mockResponse);

        // when
        CartResponse result = cartService.mergeCarts(userId, cartId);
        
        // then
        assertNotNull(result);
        assertEquals(String.valueOf(mockCart.getId()), result.getCartId());
        verify(guestCartService).deleteGuestCart(cartId);
        verify(cartResponseService).createFromUserCart(mockCart, List.of(cartItem));
    }
}