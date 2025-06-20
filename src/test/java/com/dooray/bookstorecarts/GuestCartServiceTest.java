package com.dooray.bookstorecarts;

import com.dooray.bookstorecarts.exception.CartNotFoundException;
import com.dooray.bookstorecarts.redisdto.GuestCart;
import com.dooray.bookstorecarts.redisdto.GuestCartItem;
import com.dooray.bookstorecarts.response.GuestCartItemResponse;
import com.dooray.bookstorecarts.response.GuestCartResponse;
import com.dooray.bookstorecarts.service.GuestCartService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GuestCartServiceTest {
    @Mock
    private HttpSession session;
    @InjectMocks
    private GuestCartService guestCartService;

    @Test
    void getCartBySession(){
        //given
        GuestCartItem item = new GuestCartItem(1L, 3L);
        GuestCart mockGuestCart = new GuestCart("test-session-id", List.of(item));
        given(session.getAttribute("guestCart")).willReturn(mockGuestCart);
        //when
        GuestCartResponse response = guestCartService.getCartBySession(session);
        //then
        assertNotNull(response);
        assertEquals("test-session-id", response.getSessionId());
        assertEquals(1, response.getItems().size());

        GuestCartItemResponse itemResponse = response.getItems().get(0);
        assertEquals(1L, itemResponse.getBookId());
        assertEquals(3L, itemResponse.getQuantity());
    }
    @Test
    void getCartBySession_CartNotFound() {
        //given
        given(session.getAttribute("guestCart")).willReturn(null);
        given(session.getId()).willReturn("mockSessionId");

        assertThrows(CartNotFoundException.class, () -> guestCartService.getCartBySession(session));
    }

    @Test
    void deleteGuestCart(){
        //when
        guestCartService.deleteGuestCart(session);
        //then
        verify(session).invalidate();
    }
}
