package com.dooray.bookstorecarts.servicetest;

import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;
import com.dooray.bookstorecarts.exception.CartItemNotFoundException;
import com.dooray.bookstorecarts.exception.CartNotFoundException;
import com.dooray.bookstorecarts.exception.InvalidException;
import com.dooray.bookstorecarts.redisdto.RedisCartDto;
import com.dooray.bookstorecarts.repository.UserCartItemRepository;
import com.dooray.bookstorecarts.repository.UserCartRedisRepository;
import com.dooray.bookstorecarts.repository.UserCartRepository;
import com.dooray.bookstorecarts.request.CartItemRequest;
import com.dooray.bookstorecarts.service.UserCartItemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserCartItemServiceTest {

    @Mock
    private UserCartRepository userCartRepository;

    @Mock
    private UserCartItemRepository userCartItemRepository;

    @Mock
    private UserCartRedisRepository userCartRedisRepository;

    @InjectMocks
    private UserCartItemService userCartItemService;

    @Test
    @DisplayName("회원 장바구니 조회- db에 장바구니랑 isbn 둘다 없는경우")
    public void addUserCartItem_notExistingUserCartAnd_isbnNotFound() {

        // given
        Long userId = 33L;
        CartItemRequest request = new CartItemRequest(1L, "isbn", 5L);
        Cart newCart = new Cart(userId);

        when(userCartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(userCartRepository.save(any(Cart.class))).thenReturn(newCart);
        when(userCartItemRepository.findByCartAndIsbn(newCart, request.getIsbn())).thenReturn(null);
        when(userCartItemRepository.findByCart(newCart)).thenReturn(Collections.singletonList(new CartItem()));

        // when
        userCartItemService.addUserCartItem(userId, request);

        // then
        verify(userCartRepository).save(any(Cart.class));
        verify(userCartItemRepository).save(any(CartItem.class));
        verify(userCartRedisRepository, times(2)).save(any(RedisCartDto.class));

    }

    @Test
    @DisplayName("회원 장바구니 조회 - db에 장바구니가 있고 isbn 은 없는경우")
    public void addUserCartItem_existingUserCartAnd_isbnNotFound() {

        // given
        Long userId = 33L;
        CartItemRequest request = new CartItemRequest(1L, "isbn", 5L);
        Cart Cart = new Cart(userId);

        when(userCartRepository.findByUserId(userId)).thenReturn(Optional.of(Cart));
        when(userCartItemRepository.findByCartAndIsbn(Cart, request.getIsbn())).thenReturn(null);
        when(userCartItemRepository.findByCart(Cart)).thenReturn(Collections.singletonList(new CartItem()));

        // when
        userCartItemService.addUserCartItem(userId, request);

        // then
        verify(userCartItemRepository).save(any(CartItem.class));
        verify(userCartRedisRepository).save(any(RedisCartDto.class));
    }

    @Test
    @DisplayName("회원 장바구니 조회 - db에 장바구니가 있고 isbn도 있는경우")
    public void addUserCartItem_existingUserCartAnd_isbnFound() {

        // given
        Long userId = 33L;
        CartItemRequest request = new CartItemRequest(1L, "isbn", 5L);
        Cart cart = new Cart(userId);
        CartItem existingItem = new CartItem("isbn", 5L, cart);

        when(userCartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));
        when(userCartItemRepository.findByCartAndIsbn(cart, request.getIsbn())).thenReturn(existingItem);

        // when
        userCartItemService.addUserCartItem(userId, request);

        // then
        verify(userCartItemRepository).save(existingItem);
        verify(userCartRedisRepository).save(any(RedisCartDto.class));
    }

    @Test
    @DisplayName("책 수량 업데이트 - 장바구니에 도서가 없을경우")
    public void updateQuantity_isbnNotFound() {

        // given
        Long CartItemId = 1L;
        CartItemRequest request = new CartItemRequest(1L, "isbn", 5L);

        when(userCartItemRepository.findById(CartItemId)).thenReturn(Optional.empty());

        // when then
        assertThrows(CartItemNotFoundException.class, () ->
                userCartItemService.updateQuantity(CartItemId, request));
    }

    @Test
    @DisplayName("책 수량 업데이트 - 요청한 isbn과 해당 cartItem isbn이 일치하지 않을때")
    public void updateQuantity_InvalidException(){

        // given
        Long CartItemId = 2L;
        CartItemRequest request = new CartItemRequest(1L, "wrong-isbn", 5L);
        Cart cart = new Cart(33L);
        CartItem existingItem = new CartItem("real-isbn", 3L, cart);

        when(userCartItemRepository.findById(CartItemId)).thenReturn(Optional.of(existingItem));

        // when then
        assertThrows(InvalidException.class, () ->
                userCartItemService.updateQuantity(CartItemId, request));
    }

    @Test
    @DisplayName("책 수량 업데이트 - 정상적으로 동작할때")
    public void updateQuantity(){

        // given
        Long CartItemId = 1L;
        CartItemRequest request = new CartItemRequest(1L, "isbn", 5L);
        Cart cart = new Cart(33L);
        CartItem existingItem = new CartItem("isbn", 3L, cart);

        when(userCartItemRepository.findById(CartItemId)).thenReturn(Optional.of(existingItem));
        when(userCartItemRepository.findByCart(cart)).thenReturn(List.of(existingItem));

        // when
        userCartItemService.updateQuantity(CartItemId, request);

        // then
        assertThat(existingItem.getQuantity()).isEqualTo(5L);
        verify(userCartItemRepository).save(existingItem);
        verify(userCartRedisRepository).save(any(RedisCartDto.class));
    }

    @Test
    @DisplayName("도서 삭제 - db에 요청한 도서가 없을때")
    public void deleteCartItem_isbnNotFound() {

        // given
        Long CartItemId = 1L;

        when(userCartItemRepository.findById(CartItemId)).thenReturn(Optional.empty());

        // when then
        assertThrows(CartItemNotFoundException.class, () ->
                userCartItemService.deleteCartItem(CartItemId));
    }

    @Test
    @DisplayName("도서 삭제  - 정상삭제 될때")
    public void deleteCartItem(){

        // given
        Long cartItemId = 1L;
        Cart cart = new Cart(33L);
        CartItem existingItem = new CartItem("isbn", 3L, cart);

        when(userCartItemRepository.findById(cartItemId)).thenReturn(Optional.of(existingItem));
        when(userCartItemRepository.findByCart(cart)).thenReturn(Collections.emptyList());

        // when
        userCartItemService.deleteCartItem(cartItemId);

        // then
        verify(userCartItemRepository).delete(existingItem);
        verify(userCartRedisRepository).save(any(RedisCartDto.class));
    }

    @Test
    @DisplayName("장바구니 비우기 - 장바구니 없음 예외(db 기준)")
    public void deleteAllCartItemsFromUserId_cartNotFound(){

        // given
        Long userId = 33L;

        when(userCartRedisRepository.findByUserId(userId)).thenReturn(null);
        when(userCartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when then
        assertThrows(CartNotFoundException.class, () ->
                userCartItemService.deleteAllCartItemsFromUserId(userId));
    }

    @Test
    @DisplayName("장바구니 비우기 - 정상 동작")
    public void deleteAllCartItemsFromUserId(){

        // given
        Long userId = 33L;
        Cart cart = new Cart(userId);
        CartItem item1 = new CartItem("isbn", 3L, cart);
        CartItem item2 = new CartItem("isbn2", 4L, cart);

        RedisCartDto redisCartDto = RedisCartDto.from(cart, List.of(item1, item2));

        when(userCartRedisRepository.findByUserId(userId)).thenReturn(redisCartDto);
        when(userCartRepository.findByUserId(userId)).thenReturn(Optional.of(cart));

        // when
        userCartItemService.deleteAllCartItemsFromUserId(userId);

        // then
        ArgumentCaptor<List<CartItem>> captor = ArgumentCaptor.forClass(List.class);
        verify(userCartItemRepository).deleteAll(captor.capture());
        List<CartItem> deletedItems = captor.getValue();

        assertThat(deletedItems).hasSize(2);
        assertThat(deletedItems).extracting("isbn").containsExactlyInAnyOrder("isbn", "isbn2");

        verify(userCartRedisRepository).save(RedisCartDto.from(cart, Collections.emptyList()));
    }

}
