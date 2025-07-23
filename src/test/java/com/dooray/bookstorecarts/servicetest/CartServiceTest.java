package com.dooray.bookstorecarts.servicetest;

import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;
import com.dooray.bookstorecarts.redisdto.RedisCartDto;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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
    @DisplayName("회원 비회원 카트 병합 - 비회원 카트가 비어있는경우(기존 유저 카트 반환)")
    public void mergeCarts_guestCartIsEmpty(){

        // given
        Long userId = 33L;
        String cartId = "cartId";

        RedisGuestCartDto guestCartDto = new RedisGuestCartDto(cartId, Collections.emptyList());

        Cart cart = new Cart(userId);
        CartItem item1 = new CartItem("isbn",5L, cart);
        CartItem item2 = new CartItem("isbn2",3L, cart);
        List<CartItem> items = List.of(item1, item2);
        RedisCartDto redisCartDto = RedisCartDto.from(cart, items);

        when(guestCartRedisRepository.findByCartId(cartId)).thenReturn(guestCartDto);
        when(userCartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(userCartItemRepository.findByCart(cart)).thenReturn(items);

        // when
        CartResponse response = cartService.mergeCarts(userId, cartId);

        // then
        verify(cartResponseService).createFromUserCart(redisCartDto, items);

    }

    @Test
    @DisplayName("회원 비회원 카트 병합 - 기존회원 카트에 이미 있는 책인경우(수량만 증가)")
    public void mergeCarts_cartItemIsNotEmpty(){

        // given
        Long userId = 33L;
        String cartId = "cartId";

        RedisGuestCartItemDto item1 = new RedisGuestCartItemDto("isbn1", 5L);
        RedisGuestCartItemDto item2 = new RedisGuestCartItemDto("isbn2", 3L);
        RedisGuestCartDto guestCart = new RedisGuestCartDto(cartId, List.of(item1, item2));

        Cart cart = new Cart(userId);
        CartItem cartItem = new CartItem("isbn1",5L, cart);


        when(guestCartRedisRepository.findByCartId(cartId)).thenReturn(guestCart);
        when(userCartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(userCartItemRepository.findByCartAndIsbn(cart, "isbn1")).thenReturn(cartItem);
        when(userCartItemRepository.findByCartAndIsbn(cart, "isbn2")).thenReturn(null);

        when(userCartItemRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(userCartRedisRepository).save(any());
        doNothing().when(guestCartService).deleteGuestCart(cartId);
        when(userCartItemRepository.findByCart(cart)).thenReturn(List.of(
                new CartItem("isbn1", 10L, cart),
                new CartItem("isbn2", 3L, cart)
        ));
        when(cartResponseService.createFromUserCart(any(), any())).thenReturn(mock(CartResponse.class));

        // when
        CartResponse response = cartService.mergeCarts(userId, cartId);

        // then
        assertEquals(10L, cartItem.getQuantity());
        verify(userCartItemRepository).save(cartItem);
        verify(userCartItemRepository).save(argThat(newItem ->
                newItem.getIsbn().equals("isbn2") && newItem.getQuantity() == 3L
        ));
        verify(guestCartService).deleteGuestCart(cartId);
    }

    @Test
    @DisplayName("회원 비회원 카트 병합 - 기존회원 카트에 없는 책인경우(카트에 새롭게 추가)")
    public void mergeCarts_cartItemIsEmpty(){

        // given
        Long userId = 33L;
        String cartId = "cartId";

        RedisGuestCartItemDto guestItem = new RedisGuestCartItemDto("isbn3", 2L);
        RedisGuestCartDto guestCart = new RedisGuestCartDto(cartId, List.of(guestItem));

        Cart cart = new Cart(userId);

        List<CartItem> existingItems = List.of(
                new CartItem("isbn1", 1L, cart),
                new CartItem("isbn2", 2L, cart)
        );

        when(guestCartRedisRepository.findByCartId(cartId)).thenReturn(guestCart);
        when(userCartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(userCartItemRepository.findByCartAndIsbn(cart, "isbn3")).thenReturn(null);

        when(userCartItemRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        doNothing().when(userCartRedisRepository).save(any());
        doNothing().when(guestCartService).deleteGuestCart(cartId);
        when(userCartItemRepository.findByCart(cart)).thenReturn(List.of(
                new CartItem("isbn1", 1L, cart),
                new CartItem("isbn2", 2L, cart),
                new CartItem("isbn3", 2L, cart)
        ));
        when(cartResponseService.createFromUserCart(any(), any())).thenReturn(mock(CartResponse.class));

        // when
        CartResponse response = cartService.mergeCarts(userId, cartId);

        // then
        verify(userCartItemRepository).save(argThat(newItem ->
                newItem.getIsbn().equals("isbn3") && newItem.getQuantity() == 2L));
        verify(guestCartService).deleteGuestCart(cartId);
    }
}
