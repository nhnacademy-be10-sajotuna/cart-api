package com.dooray.bookstorecarts;

import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;
import com.dooray.bookstorecarts.exception.CartItemNotFoundException;
import com.dooray.bookstorecarts.exception.CartNotFoundException;
import com.dooray.bookstorecarts.exception.InvalidException;
import com.dooray.bookstorecarts.redisdto.RedisCartDto;
import com.dooray.bookstorecarts.redisdto.RedisCartItemDto;
import com.dooray.bookstorecarts.repository.UserCartItemRepository;
import com.dooray.bookstorecarts.repository.UserCartRedisRepository;
import com.dooray.bookstorecarts.repository.UserCartRepository;
import com.dooray.bookstorecarts.request.CartItemRequest;
import com.dooray.bookstorecarts.response.UserCartItemResponse;
import com.dooray.bookstorecarts.service.UserCartItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserCartItemServiceTest {
    @InjectMocks
    UserCartItemService userCartItemService;

    @Mock
    UserCartRepository userCartRepository;

    @Mock
    UserCartItemRepository userCartItemRepository;

    @Mock
    UserCartRedisRepository userCartRedisRepository;

    @Test
    void addUserCartItem_WhenCartDoesNotExist_CreatesCartAndAddsItem() {
        Long userId = 1L;
        CartItemRequest request = new CartItemRequest(10L, 3L);

        // 1) 카트가 없어서 새로 만듦
        given(userCartRepository.findByUserId(userId)).willReturn(Optional.empty());

        Cart newCart = new Cart();
        newCart.setUserId(userId);
        Cart savedCart = new Cart();
        savedCart.setId(100L);
        savedCart.setUserId(userId);

        given(userCartRepository.save(any(Cart.class))).willReturn(savedCart);

        // 2) 아이템 저장 시
        CartItem savedItem = new CartItem();
        savedItem.setBookId(request.getBookId());
        savedItem.setQuantity(request.getQuantity());
        savedItem.setCart(savedCart);
        given(userCartItemRepository.save(any(CartItem.class))).willReturn(savedItem);

        // 3) Redis 저장은 그냥 무시하고 성공했다고 가정
        doNothing().when(userCartRedisRepository).save(any());

        // 4) findByCart 호출 시 아이템 리스트 리턴 (빈 리스트 or 새 아이템 리스트)
        given(userCartItemRepository.findByCart(savedCart)).willReturn(List.of(savedItem));

        // 실제 호출
        UserCartItemResponse response = userCartItemService.addUserCartItem(userId, request);

        // 검증
        assertEquals(request.getBookId(), response.getBookId());
        assertEquals(request.getQuantity(), response.getQuantity());

        verify(userCartRepository).findByUserId(userId);
        verify(userCartRepository).save(any(Cart.class));
        verify(userCartItemRepository).save(any(CartItem.class));
        verify(userCartRedisRepository, times(2)).save(any());
    }

    @Test
    void addUserCartItem_WhenCartExistsAndItemExists_UpdatesQuantity() {
        Long userId = 1L;
        Cart existingCart = new Cart();
        existingCart.setId(100L);
        existingCart.setUserId(userId);

        CartItem existingItem = new CartItem();
        existingItem.setBookId(10L);
        existingItem.setQuantity(1L);
        existingItem.setCart(existingCart);

        CartItemRequest request = new CartItemRequest(10L, 5L);

        given(userCartRepository.findByUserId(userId)).willReturn(Optional.of(existingCart));
        given(userCartItemRepository.findByCartAndBookId(existingCart, 10L)).willReturn(existingItem);

        CartItem savedItem = new CartItem();
        savedItem.setBookId(10L);
        savedItem.setQuantity(5L);
        savedItem.setCart(existingCart);

        given(userCartItemRepository.save(existingItem)).willReturn(savedItem);
        given(userCartItemRepository.findByCart(existingCart)).willReturn(List.of(savedItem));
        doNothing().when(userCartRedisRepository).save(any());

        UserCartItemResponse response = userCartItemService.addUserCartItem(userId, request);

        assertEquals(10L, response.getBookId());
        assertEquals(5L, response.getQuantity());

        verify(userCartRepository).findByUserId(userId);
        verify(userCartItemRepository).findByCartAndBookId(existingCart, 10L);
        verify(userCartItemRepository).save(existingItem);
        verify(userCartRedisRepository, times(1)).save(any());
    }

    @Test
    void addUserCartItem_WhenCartExistsAndItemDoesNotExist_AddsNewItem() {
        Long userId = 1L;
        Cart existingCart = new Cart();
        existingCart.setId(100L);
        existingCart.setUserId(userId);

        CartItemRequest request = new CartItemRequest(10L, 3L);

        given(userCartRepository.findByUserId(userId)).willReturn(Optional.of(existingCart));
        given(userCartItemRepository.findByCartAndBookId(existingCart, 10L)).willReturn(null);

        CartItem newItem = new CartItem();
        newItem.setBookId(10L);
        newItem.setQuantity(3L);
        newItem.setCart(existingCart);

        given(userCartItemRepository.save(any(CartItem.class))).willReturn(newItem);
        given(userCartItemRepository.findByCart(existingCart)).willReturn(List.of(newItem));
        doNothing().when(userCartRedisRepository).save(any());

        UserCartItemResponse response = userCartItemService.addUserCartItem(userId, request);

        assertEquals(10L, response.getBookId());
        assertEquals(3L, response.getQuantity());

        verify(userCartRepository).findByUserId(userId);
        verify(userCartItemRepository).findByCartAndBookId(existingCart, 10L);
        verify(userCartItemRepository).save(any(CartItem.class));
        verify(userCartRedisRepository, times(1)).save(any());
    }

    @Test
    void getCartItemByCartItemId_WhenItemFoundInRedis() {
        Long userId = 1L;
        Long cartItemId = 100L;

        RedisCartItemDto redisItem = new RedisCartItemDto();
        redisItem.setCartItemId(cartItemId);
        redisItem.setBookId(10L);
        redisItem.setQuantity(5L);

        RedisCartDto redisCart = new RedisCartDto();
        redisCart.setItems(List.of(redisItem));

        given(userCartRedisRepository.findByUserId(userId)).willReturn(redisCart);

        UserCartItemResponse response = userCartItemService.getCartItemByCartItemId(userId, cartItemId);

        assertEquals(cartItemId, response.getCartItemId());
        assertEquals(10L, response.getBookId());
        assertEquals(5L, response.getQuantity());

        // DB와 Redis 재저장은 호출 안 돼야 함
        verify(userCartItemRepository, never()).findById(any());
        verify(userCartRedisRepository, never()).save(any());
    }

    @Test
    void getCartItemByCartItemId_WhenItemNotInRedis_FetchesFromDBAndUpdatesRedis() {
        Long userId = 1L;
        Long cartItemId = 100L;

        given(userCartRedisRepository.findByUserId(userId)).willReturn(null); // Redis 캐시 없음

        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);
        cartItem.setBookId(10L);
        cartItem.setQuantity(5L);

        Cart cart = new Cart();
        cart.setId(50L);
        cartItem.setCart(cart);

        given(userCartItemRepository.findById(cartItemId)).willReturn(Optional.of(cartItem));

        List<CartItem> itemList = List.of(cartItem);
        given(userCartItemRepository.findByCart(cart)).willReturn(itemList);

        doNothing().when(userCartRedisRepository).save(any());

        UserCartItemResponse response = userCartItemService.getCartItemByCartItemId(userId, cartItemId);

        assertEquals(cartItemId, response.getCartItemId());
        assertEquals(10L, response.getBookId());
        assertEquals(5L, response.getQuantity());

        verify(userCartItemRepository).findById(cartItemId);
        verify(userCartItemRepository).findByCart(cart);
        verify(userCartRedisRepository).save(any());
    }

    @Test
    void getCartItemByCartItemId_WhenItemNotFound_ThrowsException() {
        Long userId = 1L;
        Long cartItemId = 100L;

        given(userCartRedisRepository.findByUserId(userId)).willReturn(null);
        given(userCartItemRepository.findById(cartItemId)).willReturn(Optional.empty());

        assertThrows(CartItemNotFoundException.class, () -> {
            userCartItemService.getCartItemByCartItemId(userId, cartItemId);
        });

        verify(userCartItemRepository).findById(cartItemId);
        verify(userCartRedisRepository, never()).save(any());
    }

    @Test
    void getCartItemsByUserId_WhenRedisHasData() {
        // given
        Long userId = 1L;
        RedisCartItemDto redisItem = new RedisCartItemDto();
        redisItem.setCartItemId(101L);
        redisItem.setBookId(20L);
        redisItem.setQuantity(2L);

        RedisCartDto redisCart = new RedisCartDto();
        redisCart.setItems(List.of(redisItem));

        given(userCartRedisRepository.findByUserId(userId)).willReturn(redisCart);

        // when
        List<CartItem> result = userCartItemService.getCartItemsByUserId(userId);

        // then
        assertEquals(1, result.size());
        assertEquals(101L, result.get(0).getId());
        assertEquals(20L, result.get(0).getBookId());
        assertEquals(2L, result.get(0).getQuantity());

        verify(userCartRedisRepository).findByUserId(userId);
        verifyNoInteractions(userCartRepository, userCartItemRepository);
    }

    @Test
    void getCartItemsByUserId_WhenRedisIsEmptyAndDBHasData() {
        // given
        Long userId = 1L;
        given(userCartRedisRepository.findByUserId(userId)).willReturn(null);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUserId(userId);

        CartItem item = new CartItem();
        item.setId(101L);
        item.setBookId(20L);
        item.setQuantity(2L);
        item.setCart(cart);

        given(userCartRepository.findByUserId(userId)).willReturn(Optional.of(cart));
        given(userCartItemRepository.findByCart(cart)).willReturn(List.of(item));
        doNothing().when(userCartRedisRepository).save(any());

        // when
        List<CartItem> result = userCartItemService.getCartItemsByUserId(userId);

        // then
        assertEquals(1, result.size());
        assertEquals(101L, result.get(0).getId());
        verify(userCartRepository).findByUserId(userId);
        verify(userCartItemRepository).findByCart(cart);
        verify(userCartRedisRepository).save(any());
    }

    @Test
    void getCartItemsByUserId_WhenCartNotExists_ThrowsException() {
        // given
        Long userId = 1L;
        given(userCartRedisRepository.findByUserId(userId)).willReturn(null);
        given(userCartRepository.findByUserId(userId)).willReturn(Optional.empty());

        // then
        assertThrows(CartNotFoundException.class, () ->
                userCartItemService.getCartItemsByUserId(userId));
    }

    @Test
    void updateQuantity_WhenBookIdMatches_UpdatesSuccessfully() {
        // given
        Long cartItemId = 1L;
        Long bookId = 10L;
        Long newQuantity = 5L;

        Cart cart = new Cart();
        cart.setId(100L);

        CartItem existingItem = new CartItem();
        existingItem.setId(cartItemId);
        existingItem.setBookId(bookId);
        existingItem.setQuantity(2L);
        existingItem.setCart(cart);

        CartItemRequest request = new CartItemRequest(bookId, newQuantity);

        given(userCartItemRepository.findById(cartItemId)).willReturn(Optional.of(existingItem));
        given(userCartItemRepository.save(existingItem)).willReturn(existingItem);
        given(userCartItemRepository.findByCart(cart)).willReturn(List.of(existingItem));
        doNothing().when(userCartRedisRepository).save(any());

        // when
        UserCartItemResponse response = userCartItemService.updateQuantity(cartItemId, request);

        // then
        assertEquals(bookId, response.getBookId());
        assertEquals(newQuantity, response.getQuantity());
        verify(userCartItemRepository).save(existingItem);
        verify(userCartRedisRepository).save(any());
    }

    @Test
    void updateQuantity_WhenCartItemNotFound_ThrowsException() {
        // given
        Long cartItemId = 1L;
        CartItemRequest request = new CartItemRequest(10L, 5L);
        given(userCartItemRepository.findById(cartItemId)).willReturn(Optional.empty());

        // when & then
        assertThrows(CartItemNotFoundException.class, () ->
                userCartItemService.updateQuantity(cartItemId, request));
    }

    @Test
    void updateQuantity_WhenBookIdMismatch_ThrowsInvalidException() {
        // given
        Long cartItemId = 1L;
        CartItemRequest request = new CartItemRequest(999L, 5L); // 틀린 bookId

        CartItem existingItem = new CartItem();
        existingItem.setId(cartItemId);
        existingItem.setBookId(10L);
        existingItem.setQuantity(3L);
        existingItem.setCart(new Cart());

        given(userCartItemRepository.findById(cartItemId)).willReturn(Optional.of(existingItem));

        // when & then
        assertThrows(InvalidException.class, () ->
                userCartItemService.updateQuantity(cartItemId, request));
    }

    @Test
    void deleteCartItem_WhenItemExists_DeletesSuccessfully() {
        // given
        Long cartItemId = 1L;

        Cart cart = new Cart();
        cart.setId(100L);

        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);
        cartItem.setCart(cart);

        List<CartItem> updatedItems = List.of(); // 삭제 후 빈 리스트

        given(userCartItemRepository.findById(cartItemId)).willReturn(Optional.of(cartItem));
        given(userCartItemRepository.findByCart(cart)).willReturn(updatedItems);
        doNothing().when(userCartItemRepository).delete(cartItem);
        doNothing().when(userCartRedisRepository).save(any());

        // when
        userCartItemService.deleteCartItem(cartItemId);

        // then
        verify(userCartItemRepository).findById(cartItemId);
        verify(userCartItemRepository).delete(cartItem);
        verify(userCartItemRepository).findByCart(cart);
        verify(userCartRedisRepository).save(any());
    }

    @Test
    void deleteCartItem_WhenItemNotFound_ThrowsException() {
        // given
        Long cartItemId = 1L;
        given(userCartItemRepository.findById(cartItemId)).willReturn(Optional.empty());

        // when & then
        assertThrows(CartItemNotFoundException.class, () -> {
            userCartItemService.deleteCartItem(cartItemId);
        });

        verify(userCartItemRepository).findById(cartItemId);
        verify(userCartItemRepository, never()).delete(any());
        verify(userCartRedisRepository, never()).save(any());
    }
    @Test
    void deleteAllCartItemsFromUserId_WhenCartExists_ShouldDeleteItemsAndClearRedis() {
        // given
        Long userId = 1L;

        Cart cart = new Cart();
        cart.setId(100L);
        cart.setUserId(userId);

        CartItem item1 = new CartItem();
        item1.setId(1L);
        item1.setBookId(10L);
        item1.setQuantity(2L);
        item1.setCart(cart);

        CartItem item2 = new CartItem();
        item2.setId(2L);
        item2.setBookId(20L);
        item2.setQuantity(1L);
        item2.setCart(cart);

        // service 를 spy 처리해서 내부 메서드 mocking
        UserCartItemService spyService = Mockito.spy(userCartItemService);
        doReturn(List.of(item1, item2)).when(spyService).getCartItemsByUserId(userId);
        given(userCartRepository.findByUserId(userId)).willReturn(Optional.of(cart));
        doNothing().when(userCartItemRepository).deleteAll(anyList());
        doNothing().when(userCartRedisRepository).save(any());

        // when
        spyService.deleteAllCartItemsFromUserId(userId);

        // then
        verify(spyService).getCartItemsByUserId(userId);
        verify(userCartItemRepository).deleteAll(List.of(item1, item2));
        verify(userCartRepository).findByUserId(userId);
        verify(userCartRedisRepository).save(any());
    }

    @Test
    void deleteAllCartItemsFromUserId_WhenCartNotFound_ShouldThrowException() {
        // given
        Long userId = 999L;

        UserCartItemService spyService = Mockito.spy(userCartItemService);
        doReturn(List.of()).when(spyService).getCartItemsByUserId(userId);
        given(userCartRepository.findByUserId(userId)).willReturn(Optional.empty());

        // when & then
        assertThrows(CartNotFoundException.class, () -> spyService.deleteAllCartItemsFromUserId(userId));

        verify(spyService).getCartItemsByUserId(userId);
        verify(userCartRepository).findByUserId(userId);
        verify(userCartRedisRepository, never()).save(any());
    }

}


