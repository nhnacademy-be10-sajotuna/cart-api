package com.dooray.bookstorecarts;

import com.dooray.bookstorecarts.exception.CartItemNotFoundException;
import com.dooray.bookstorecarts.exception.CartNotFoundException;
import com.dooray.bookstorecarts.redisdto.GuestCart;
import com.dooray.bookstorecarts.redisdto.GuestCartItem;
import com.dooray.bookstorecarts.request.CartItemRequest;
import com.dooray.bookstorecarts.response.GuestCartItemResponse;
import com.dooray.bookstorecarts.service.GuestCartItemService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GuestCartItemServiceTest {
    @Mock
    private HttpSession session;
    @InjectMocks
    private GuestCartItemService guestCartItemService;

    @Test // 게스트 카트가 비어있는 경우(게스트 카트 새로 생성)
    void addGuestCartItem_WhenGuestCartIsNull() {
        // given
        CartItemRequest request = new CartItemRequest(1L, 2L);
        given(session.getAttribute("guestCart")).willReturn(null);
        given(session.getId()).willReturn("test-session-id");
        // when
        GuestCartItemResponse response = guestCartItemService.addGuestCartItem(session, request);
        // then
        assertEquals(1L, response.getBookId());
        assertEquals(2L, response.getQuantity());

        // session.setAttribute 호출 검증
        ArgumentCaptor<GuestCart> captor = ArgumentCaptor.forClass(GuestCart.class);
        verify(session).setAttribute(eq("guestCart"), captor.capture());

        GuestCart storedCart = captor.getValue();
        assertEquals("test-session-id", storedCart.getSessionId());
        assertEquals(1, storedCart.getItems().size());
        assertEquals(1L, storedCart.getItems().get(0).getBookId());
    }

    @Test
    void addGuestCartItem_WhenItemAlreadyExists() {
        // given
        GuestCartItem existingItem = new GuestCartItem(1L, 1L);
        GuestCart guestCart = new GuestCart("session-id", new ArrayList<>(List.of(existingItem)));

        CartItemRequest request = new CartItemRequest(1L, 5L);
        given(session.getAttribute("guestCart")).willReturn(guestCart);

        // when
        GuestCartItemResponse response = guestCartItemService.addGuestCartItem(session, request);

        // then
        assertEquals(1L, response.getBookId());
        assertEquals(5L, response.getQuantity());

        ArgumentCaptor<GuestCart> captor = ArgumentCaptor.forClass(GuestCart.class);
        verify(session).setAttribute(eq("guestCart"), captor.capture());

        GuestCart storedCart = captor.getValue();
        assertEquals(1, storedCart.getItems().size());
        assertEquals(5L, storedCart.getItems().get(0).getQuantity());
    }

    @Test
    void getGuestCartItemByBookId_WhenGuestCartIsNull() {
        // given
        given(session.getAttribute("guestCart")).willReturn(null);
        given(session.getId()).willReturn("test-session-id");

        // when
        assertThrows(CartNotFoundException.class, () ->
                guestCartItemService.getGuestCartItemByBookId(session, 1L));
    }

    @Test
    void getGuestCartItemByBookId_WhenBookNotFound() {
        // given
        GuestCart guestCart = new GuestCart("session-id", List.of(new GuestCartItem(2L, 3L)));
        given(session.getAttribute("guestCart")).willReturn(guestCart);
        // when
        assertThrows(CartItemNotFoundException.class, () ->
                guestCartItemService.getGuestCartItemByBookId(session, 1L));
    }

    @Test
    void getGuestCartItemByBookId_WhenBookExists() {
        // given
        GuestCart guestCart = new GuestCart("session-id", List.of(new GuestCartItem(1L, 3L)));
        given(session.getAttribute("guestCart")).willReturn(guestCart);
        // when
        GuestCartItemResponse response = guestCartItemService.getGuestCartItemByBookId(session, 1L);
        // then
        assertEquals(1L, response.getBookId());
        assertEquals(3L, response.getQuantity());
    }

    @Test
    void updateQuantity_WhenGuestCartIsNull() {
        // given
        CartItemRequest request = new CartItemRequest(1L, 2L);
        given(session.getAttribute("guestCart")).willReturn(null);
        given(session.getId()).willReturn("test-session-id");

        // when
        assertThrows(CartNotFoundException.class, () ->
                guestCartItemService.updateQuantity(session, request));
    }

    @Test
    void updateQuantity_WhenBookNotFound() {
        // given
        CartItemRequest request = new CartItemRequest(1L, 2L);
        GuestCart guestCart = new GuestCart("session-id", List.of(new GuestCartItem(2L, 3L)));
        given(session.getAttribute("guestCart")).willReturn(guestCart);
        // when
        assertThrows(CartItemNotFoundException.class, () ->
                guestCartItemService.updateQuantity(session, request));
    }

    @Test
    void updateQuantity_WhenBookExists() {
        // given
        Long bookId = 1L;
        Long newQuantity = 10L;

        GuestCartItem cartItem = new GuestCartItem(bookId, 3L);
        GuestCart guestCart = new GuestCart("session-id", new ArrayList<>(List.of(cartItem)));

        given(session.getAttribute("guestCart")).willReturn(guestCart);
        CartItemRequest request = new CartItemRequest(bookId, newQuantity);

        // when
        GuestCartItemResponse response = guestCartItemService.updateQuantity(session, request);

        // then
        assertEquals(bookId, response.getBookId());
        assertEquals(newQuantity, response.getQuantity());

        ArgumentCaptor<GuestCart> captor = ArgumentCaptor.forClass(GuestCart.class);
        verify(session).setAttribute(eq("guestCart"), captor.capture());

        GuestCart updatedCart = captor.getValue();
        assertEquals(1, updatedCart.getItems().size());
        assertEquals(newQuantity, updatedCart.getItems().get(0).getQuantity());
    }


    @Test
    void deleteGuestCartItem_WhenGuestCartIsNull() {
        // given
        given(session.getAttribute("guestCart")).willReturn(null);
        given(session.getId()).willReturn("test-session-id");
        // when
        assertThrows(CartNotFoundException.class, () ->
                guestCartItemService.deleteGuestCartItem(session, 1L));
    }

    @Test
    void deleteGuestCartItem_WhenBookNotFound() {
        // given
        GuestCart guestCart = new GuestCart("session-id", new ArrayList<>(List.of(new GuestCartItem(2L, 3L))));
        given(session.getAttribute("guestCart")).willReturn(guestCart);
        // when
        assertThrows(CartItemNotFoundException.class, () ->
                guestCartItemService.deleteGuestCartItem(session, 1L));
    }

    @Test
    void deleteGuestCartItem_WhenBookExists() {
        // given
        GuestCart guestCart = new GuestCart("session-id", new ArrayList<>(List.of(new GuestCartItem(2L, 3L))));
        given(session.getAttribute("guestCart")).willReturn(guestCart);
        // when
        guestCartItemService.deleteGuestCartItem(session, 2L);
        // then
        ArgumentCaptor<GuestCart> captor = ArgumentCaptor.forClass(GuestCart.class);
        verify(session).setAttribute(eq("guestCart"), captor.capture());

        GuestCart updatedCart = captor.getValue();
        assertTrue(updatedCart.getItems().isEmpty(), "장바구니 아이템이 삭제되어야 함");
    }

    @Test
    void deleteAllGuestCartItems(){
        // given
        GuestCart guestCart = new GuestCart("session-id", new ArrayList<>(List.of(new GuestCartItem(2L, 3L))));
        given(session.getAttribute("guestCart")).willReturn(guestCart);
        // when
        guestCartItemService.deleteAllGuestCartItems(session);
        // then
        ArgumentCaptor<GuestCart> captor = ArgumentCaptor.forClass(GuestCart.class);
        verify(session).setAttribute(eq("guestCart"), captor.capture());

        GuestCart updatedGuestCart = captor.getValue();
        assertNotNull(updatedGuestCart);
        assertTrue(updatedGuestCart.getItems().isEmpty());
    }

}
