package com.dooray.bookstorecarts.servicetest;

import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;
import com.dooray.bookstorecarts.feign.BookBatchRequest;
import com.dooray.bookstorecarts.feign.BookFeignClient;
import com.dooray.bookstorecarts.feign.BookSummaryResponse;
import com.dooray.bookstorecarts.redisdto.RedisCartDto;
import com.dooray.bookstorecarts.redisdto.RedisGuestCartDto;
import com.dooray.bookstorecarts.redisdto.RedisGuestCartItemDto;
import com.dooray.bookstorecarts.response.CartResponse;
import com.dooray.bookstorecarts.service.CartResponseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CartResponseServiceTest {

    @Mock
    private BookFeignClient bookFeignClient;

    @InjectMocks
    private CartResponseService cartResponseService;

    @Test
    @DisplayName("회원 장바구니 비어있을때")
    public void createFromUserCart_userCartIsEmpty() {

        // given
        RedisCartDto cartDto = new RedisCartDto(1L, 33L, Collections.emptyList());

        // when
        CartResponse response = cartResponseService.createFromUserCart(cartDto, Collections.emptyList());

        // then
        assertThat(response.getItems()).isEmpty();
    }

    @Test
    @DisplayName("회원 장바구니 정상 동작")
    public void createFromUserCart(){

        // given
        RedisCartDto cartDto = new RedisCartDto(1L, 33L, Collections.emptyList());
        Cart cart = new Cart(33L);
        CartItem item1 = new CartItem("isbn1", 5L, cart);
        CartItem item2 = new CartItem("isbn2", 6L, cart);
        List<CartItem> items = List.of(item1, item2);

        BookSummaryResponse book1 = new BookSummaryResponse("isbn1", "책1", "image1",
                10000.0,8000.0,true, Collections.emptyList());
        BookSummaryResponse book2 = new BookSummaryResponse("isbn2", "책2", "image2",
                11000.0,9000.0,true, Collections.emptyList());

        when(bookFeignClient.getBooksByIsbns(any(BookBatchRequest.class))).thenReturn(List.of(book1, book2));

        // when
        CartResponse response = cartResponseService.createFromUserCart(cartDto, items);

        // then
        assertThat(response.getCartId()).isEqualTo("1");
        assertThat(response.getItems()).hasSize(2);
        assertThat(response.getItems()).extracting("title")
                .containsExactlyInAnyOrder("책1", "책2");
    }

    @Test
    @DisplayName("비회원 장바구니 비어있을때")
    public void createFromGuestCart_guestCartIsEmpty() {

        // given
        RedisGuestCartDto guestCartDto = new RedisGuestCartDto("1", Collections.emptyList());

        // when
        CartResponse response = cartResponseService.createFromGuestCart(guestCartDto);

        // then
        assertThat(response.getItems()).isEmpty();
    }

    @Test
    @DisplayName("비회원 장바구니 정상동작")
    public void createFromGuestCart(){

        // given
        RedisGuestCartItemDto item1 = new RedisGuestCartItemDto("isbn1", 5L);
        RedisGuestCartItemDto item2 = new RedisGuestCartItemDto("isbn2", 6L);
        List<RedisGuestCartItemDto> items = List.of(item1, item2);
        RedisGuestCartDto guestCartDto = new RedisGuestCartDto("1", items);

        BookSummaryResponse book1 = new BookSummaryResponse("isbn1", "책1", "image1",
                10000.0,8000.0,true, Collections.emptyList());
        BookSummaryResponse book2 = new BookSummaryResponse("isbn2", "책2", "image2",
                11000.0,9000.0,true, Collections.emptyList());

        when(bookFeignClient.getBooksByIsbns(any(BookBatchRequest.class))).thenReturn(List.of(book1, book2));

        // when
        CartResponse response = cartResponseService.createFromGuestCart(guestCartDto);

        // then
        assertThat(response.getCartId()).isEqualTo("1");
        assertThat(response.getItems()).hasSize(2);
        assertThat(response.getItems()).extracting("title")
                .containsExactlyInAnyOrder("책1", "책2");
    }
}
