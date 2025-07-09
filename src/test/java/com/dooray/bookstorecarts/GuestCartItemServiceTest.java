package com.dooray.bookstorecarts;

import com.dooray.bookstorecarts.exception.CartItemNotFoundException;
import com.dooray.bookstorecarts.exception.CartNotFoundException;
import com.dooray.bookstorecarts.redisdto.RedisGuestCartDto;
import com.dooray.bookstorecarts.redisdto.RedisGuestCartItemDto;
import com.dooray.bookstorecarts.repository.GuestCartRedisRepository;
import com.dooray.bookstorecarts.request.CartItemRequest;
import com.dooray.bookstorecarts.response.CartItemResponse;
import com.dooray.bookstorecarts.service.GuestCartItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GuestCartItemServiceTest {
    @Mock
    private GuestCartRedisRepository guestCartRedisRepository;
    @InjectMocks
    private GuestCartItemService guestCartItemService;

    @Test
    void addGuestCartItem_WhenGuestCartIsNull() {
        // given
        String cartId = "test-cart-id";
        CartItemRequest request = new CartItemRequest("1", 2L);
        given(guestCartRedisRepository.findByCartId(cartId)).willReturn(null);
        
        // when
        CartItemResponse response = guestCartItemService.addGuestCartItem(cartId, request);
        
        // then
        assertEquals("1", response.getIsbn());
        assertEquals(2L, response.getQuantity());

        // Redis 저장 검증
        ArgumentCaptor<RedisGuestCartDto> captor = ArgumentCaptor.forClass(RedisGuestCartDto.class);
        verify(guestCartRedisRepository).save(captor.capture());

        RedisGuestCartDto storedCart = captor.getValue();
        assertEquals(cartId, storedCart.getCartId());
        assertEquals(1, storedCart.getItems().size());
        assertEquals("1", storedCart.getItems().get(0).getIsbn());
    }

    @Test
    void addGuestCartItem_WhenItemAlreadyExists() {
        // given
        String cartId = "test-cart-id";
        RedisGuestCartItemDto existingItem = new RedisGuestCartItemDto("1", 1L);
        RedisGuestCartDto guestCart = new RedisGuestCartDto(cartId, new ArrayList<>(List.of(existingItem)));

        CartItemRequest request = new CartItemRequest("1", 5L);
        given(guestCartRedisRepository.findByCartId(cartId)).willReturn(guestCart);

        // when
        CartItemResponse response = guestCartItemService.addGuestCartItem(cartId, request);

        // then
        assertEquals("1", response.getIsbn());
        assertEquals(5L, response.getQuantity());

        ArgumentCaptor<RedisGuestCartDto> captor = ArgumentCaptor.forClass(RedisGuestCartDto.class);
        verify(guestCartRedisRepository).save(captor.capture());

        RedisGuestCartDto storedCart = captor.getValue();
        assertEquals(1, storedCart.getItems().size());
        assertEquals(5L, storedCart.getItems().get(0).getQuantity());
    }

    @Test
    void getGuestCartItemByIsbn_WhenGuestCartIsNull() {
        // given
        String cartId = "test-cart-id";
        given(guestCartRedisRepository.findByCartId(cartId)).willReturn(null);

        // when & then
        assertThrows(CartNotFoundException.class, () ->
                guestCartItemService.getGuestCartItemByIsbn(cartId, "1"));
    }

    @Test
    void getGuestCartItemByIsbn_WhenBookNotFound() {
        // given
        String cartId = "test-cart-id";
        RedisGuestCartDto guestCart = new RedisGuestCartDto(cartId, List.of(new RedisGuestCartItemDto("2", 3L)));
        given(guestCartRedisRepository.findByCartId(cartId)).willReturn(guestCart);
        
        // when & then
        assertThrows(CartItemNotFoundException.class, () ->
                guestCartItemService.getGuestCartItemByIsbn(cartId, "1"));
    }

    @Test
    void getGuestCartItemByIsbn_WhenBookExists() {
        // given
        String cartId = "test-cart-id";
        RedisGuestCartDto guestCart = new RedisGuestCartDto(cartId, List.of(new RedisGuestCartItemDto("1", 3L)));
        given(guestCartRedisRepository.findByCartId(cartId)).willReturn(guestCart);
        
        // when
        CartItemResponse response = guestCartItemService.getGuestCartItemByIsbn(cartId, "1");
        
        // then
        assertEquals("1", response.getIsbn());
        assertEquals(3L, response.getQuantity());
    }

    @Test
    void updateQuantity_WhenGuestCartIsNull() {
        // given
        String cartId = "test-cart-id";
        CartItemRequest request = new CartItemRequest("1", 2L);
        given(guestCartRedisRepository.findByCartId(cartId)).willReturn(null);

        // when & then
        assertThrows(CartNotFoundException.class, () ->
                guestCartItemService.updateQuantity(cartId, request));
    }

    @Test
    void updateQuantity_WhenBookNotFound() {
        // given
        String cartId = "test-cart-id";
        CartItemRequest request = new CartItemRequest("1", 2L);
        RedisGuestCartDto guestCart = new RedisGuestCartDto(cartId, List.of(new RedisGuestCartItemDto("2", 3L)));
        given(guestCartRedisRepository.findByCartId(cartId)).willReturn(guestCart);
        
        // when & then
        assertThrows(CartItemNotFoundException.class, () ->
                guestCartItemService.updateQuantity(cartId, request));
    }

    @Test
    void updateQuantity_WhenBookExists() {
        // given
        String cartId = "test-cart-id";
        String isbn = "1";
        Long newQuantity = 10L;

        RedisGuestCartItemDto cartItem = new RedisGuestCartItemDto(isbn, 3L);
        RedisGuestCartDto guestCart = new RedisGuestCartDto(cartId, new ArrayList<>(List.of(cartItem)));

        given(guestCartRedisRepository.findByCartId(cartId)).willReturn(guestCart);
        CartItemRequest request = new CartItemRequest(isbn, newQuantity);

        // when
        CartItemResponse response = guestCartItemService.updateQuantity(cartId, request);

        // then
        assertEquals(isbn, response.getIsbn());
        assertEquals(newQuantity, response.getQuantity());

        ArgumentCaptor<RedisGuestCartDto> captor = ArgumentCaptor.forClass(RedisGuestCartDto.class);
        verify(guestCartRedisRepository).save(captor.capture());

        RedisGuestCartDto updatedCart = captor.getValue();
        assertEquals(1, updatedCart.getItems().size());
        assertEquals(newQuantity, updatedCart.getItems().get(0).getQuantity());
    }

    @Test
    void deleteGuestCartItem_WhenGuestCartIsNull() {
        // given
        String cartId = "test-cart-id";
        given(guestCartRedisRepository.findByCartId(cartId)).willReturn(null);
        
        // when & then
        assertThrows(CartNotFoundException.class, () ->
                guestCartItemService.deleteGuestCartItem(cartId, "1"));
    }

    @Test
    void deleteGuestCartItem_WhenBookNotFound() {
        // given
        String cartId = "test-cart-id";
        RedisGuestCartDto guestCart = new RedisGuestCartDto(cartId, new ArrayList<>(List.of(new RedisGuestCartItemDto("2", 3L))));
        given(guestCartRedisRepository.findByCartId(cartId)).willReturn(guestCart);
        
        // when & then
        assertThrows(CartItemNotFoundException.class, () ->
                guestCartItemService.deleteGuestCartItem(cartId, "1"));
    }

    @Test
    void deleteGuestCartItem_WhenBookExists() {
        // given
        String cartId = "test-cart-id";
        RedisGuestCartDto guestCart = new RedisGuestCartDto(cartId, new ArrayList<>(List.of(new RedisGuestCartItemDto("2", 3L))));
        given(guestCartRedisRepository.findByCartId(cartId)).willReturn(guestCart);
        
        // when
        guestCartItemService.deleteGuestCartItem(cartId, "2");
        
        // then
        ArgumentCaptor<RedisGuestCartDto> captor = ArgumentCaptor.forClass(RedisGuestCartDto.class);
        verify(guestCartRedisRepository).save(captor.capture());

        RedisGuestCartDto updatedCart = captor.getValue();
        assertTrue(updatedCart.getItems().isEmpty(), "장바구니 아이템이 삭제되어야 함");
    }

    @Test
    void deleteAllGuestCartItems(){
        // given
        String cartId = "test-cart-id";
        RedisGuestCartDto guestCart = new RedisGuestCartDto(cartId, new ArrayList<>(List.of(new RedisGuestCartItemDto("2", 3L))));
        given(guestCartRedisRepository.findByCartId(cartId)).willReturn(guestCart);
        
        // when
        guestCartItemService.deleteAllGuestCartItems(cartId);
        
        // then
        ArgumentCaptor<RedisGuestCartDto> captor = ArgumentCaptor.forClass(RedisGuestCartDto.class);
        verify(guestCartRedisRepository).save(captor.capture());

        RedisGuestCartDto updatedGuestCart = captor.getValue();
        assertNotNull(updatedGuestCart);
        assertTrue(updatedGuestCart.getItems().isEmpty());
    }
}