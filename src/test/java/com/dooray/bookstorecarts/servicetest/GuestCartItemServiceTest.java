package com.dooray.bookstorecarts.servicetest;

import com.dooray.bookstorecarts.exception.CartItemNotFoundException;
import com.dooray.bookstorecarts.exception.CartNotFoundException;
import com.dooray.bookstorecarts.redisdto.RedisGuestCartDto;
import com.dooray.bookstorecarts.redisdto.RedisGuestCartItemDto;
import com.dooray.bookstorecarts.repository.GuestCartRedisRepository;
import com.dooray.bookstorecarts.request.CartItemRequest;
import com.dooray.bookstorecarts.service.GuestCartItemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GuestCartItemServiceTest {

    @Mock
    private GuestCartRedisRepository guestCartRedisRepository;

    @InjectMocks
    private GuestCartItemService guestCartItemService;

    @Test
    @DisplayName("비회원 장바구니에 책 추가 - 레디스에 비회원 장바구니가 없을경우")
    public void addGuestCartItem_notExistingGuestCart() {
        // given
        String cartId = UUID.randomUUID().toString();
        CartItemRequest request = new CartItemRequest(1L,"isbn", 5L);

        when(guestCartRedisRepository.findByCartId(cartId)).thenReturn(null);

        // when
        guestCartItemService.addGuestCartItem(cartId, request);

        // then
        verify(guestCartRedisRepository, times(2)).save(any(RedisGuestCartDto.class));

    }

    @Test
    @DisplayName("비회원 장바구니에 책 추가 - 같은 책이 존재하는 경우(수량 업데이트)")
    public void addGuestCartItem_WhenSameIsbnExists_ShouldUpdateQuantity() {
        // given
        String cartId = UUID.randomUUID().toString();
        CartItemRequest request = new CartItemRequest(1L,"isbn", 10L);
        RedisGuestCartDto guestCart = new RedisGuestCartDto(cartId, List.of(
                new RedisGuestCartItemDto("isbn", 5L)));

        given(guestCartRedisRepository.findByCartId(cartId)).willReturn(guestCart);

        // when
        guestCartItemService.addGuestCartItem(cartId, request);

        // then
        assertThat(guestCart.getItems().getFirst().getQuantity()).isEqualTo(10L);

    }

    @Test
    @DisplayName("비회원 장바구니에 책 추가 - 새로운 책 추가")
    public void addGuestCartItem_WhenSameIsbnNotExists_ShouldAddNewBook() {
        // given
        String cartId = UUID.randomUUID().toString();
        CartItemRequest request = new CartItemRequest(1L,"isbn2", 10L);
        RedisGuestCartDto guestCart = new RedisGuestCartDto(cartId, new ArrayList<>());
        guestCart.getItems().add(new RedisGuestCartItemDto("isbn", 5L));

        given(guestCartRedisRepository.findByCartId(cartId)).willReturn(guestCart);

        // when
        guestCartItemService.addGuestCartItem(cartId, request);
        // then
        assertThat(guestCart.getItems()).hasSize(2);

        RedisGuestCartItemDto addedItem = guestCart.getItems().stream()
                .filter(item -> item.getIsbn().equals("isbn2"))
                .findFirst()
                .orElseThrow();

        assertThat(addedItem.getQuantity()).isEqualTo(10L);
        verify(guestCartRedisRepository).save(guestCart);
    }

    @Test
    @DisplayName("비회원 책 수량 업데이트 - 비회원 장바구니가 없는 경우")
    public void updateQuantity_whenNotExistingGuestCart(){
        // given
        String cartId = UUID.randomUUID().toString();
        CartItemRequest request = new CartItemRequest(1L,"isbn", 5L);

        when(guestCartRedisRepository.findByCartId(cartId)).thenReturn(null);

        // when then
        assertThrows(CartNotFoundException.class, () ->
                guestCartItemService.updateQuantity(cartId, request));
    }

    @Test
    @DisplayName("비회원 책 수량 업데이트 - 비회원 장바구니에 책이 없는 경우")
    public void updateQuantity_whenNotExistingBookInGuestCart(){
        // given
        String cartId = UUID.randomUUID().toString();
        CartItemRequest request = new CartItemRequest(1L,"isbn", 10L);
        RedisGuestCartDto guestCart = new RedisGuestCartDto(cartId, List.of(
                new RedisGuestCartItemDto("isbn2", 2L)
        ));

        when(guestCartRedisRepository.findByCartId(cartId)).thenReturn(guestCart);

        // when then
        assertThrows(CartItemNotFoundException.class, () ->
                guestCartItemService.updateQuantity(cartId, request));
    }

    @Test
    @DisplayName("비회원 책 수량 업데이트 - 정상적으로 업데이트 되는 경우")
    public void updateQuantity(){
        // given
        String cartId = UUID.randomUUID().toString();
        CartItemRequest request = new CartItemRequest(1L,"isbn", 10L);
        RedisGuestCartDto guestCart = new RedisGuestCartDto(cartId, List.of(
                new RedisGuestCartItemDto("isbn", 2L)
        ));

        when(guestCartRedisRepository.findByCartId(cartId)).thenReturn(guestCart);

        // when
        guestCartItemService.updateQuantity(cartId, request);

        // then
        verify(guestCartRedisRepository).save(guestCart);
        assertThat(guestCart.getItems().getFirst().getQuantity()).isEqualTo(10L);
    }

    @Test
    @DisplayName("비회원 카트에서 도서 삭제 - 비회원 카트가 없는 경우")
    public void deleteGuestCartItem_WhenNotExistingGuestCart(){
        // given
        String cartId = UUID.randomUUID().toString();

        when(guestCartRedisRepository.findByCartId(cartId)).thenReturn(null);

        // when then
        assertThrows(CartNotFoundException.class, () ->
                guestCartItemService.deleteGuestCartItem(cartId, "isbn"));
    }

    @Test
    @DisplayName("비회원 카트에서 도서 삭제 - 비회원 카트에 삭제하려는 도서가 없는 경우")
    public void deleteGuestCartItem_whenNotExistingBookInGuestCart(){
        // given
        String cartId = UUID.randomUUID().toString();
        RedisGuestCartDto guestCart = new RedisGuestCartDto(cartId, new ArrayList<>(
                List.of(new RedisGuestCartItemDto("isbn2", 2L))
        ));

        when(guestCartRedisRepository.findByCartId(cartId)).thenReturn(guestCart);

        // when then
        assertThrows(CartItemNotFoundException.class, () ->
                guestCartItemService.deleteGuestCartItem(cartId, "isbn"));
    }

    @Test
    @DisplayName("비회원 카트에서 도서 삭제 - 정상적인 경우")
    public void deleteGuestCartItem(){
        // given
        String cartId = UUID.randomUUID().toString();
        RedisGuestCartDto guestCart = new RedisGuestCartDto(cartId, new ArrayList<>(
                List.of(new RedisGuestCartItemDto("isbn", 2L))
        ));

        when(guestCartRedisRepository.findByCartId(cartId)).thenReturn(guestCart);

        // when
        guestCartItemService.deleteGuestCartItem(cartId, "isbn");

        // then
        verify(guestCartRedisRepository).save(guestCart);
    }

    @Test
    @DisplayName("비회원 장바구니 비우기")
    public void deleteAllGuestCartItems(){
        // given
        String cartId = UUID.randomUUID().toString();
        RedisGuestCartDto guestCart = new RedisGuestCartDto(cartId, new ArrayList<>(
                List.of(new RedisGuestCartItemDto("isbn", 2L))
        ));

        when(guestCartRedisRepository.findByCartId(cartId)).thenReturn(guestCart);

        // when
        guestCartItemService.deleteAllGuestCartItems(cartId);

        // then
        verify(guestCartRedisRepository).save(guestCart);
        assertThat(guestCart.getItems().size()).isEqualTo(0);
    }
}
