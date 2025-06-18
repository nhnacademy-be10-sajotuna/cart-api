package com.dooray.bookstorecarts.service;

import com.dooray.bookstorecarts.exception.CartNotFoundException;
import com.dooray.bookstorecarts.redisdto.GuestCart;
import com.dooray.bookstorecarts.response.GuestCartResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class GuestCartService1 {
    public GuestCartResponse getCartBySession(HttpSession session) {
        GuestCart guestCart = (GuestCart) session.getAttribute("guestCart");
        if (guestCart == null) {
            throw new CartNotFoundException(session.getId());
        }

        return new GuestCartResponse(guestCart);
    }

    public void deleteGuestCart(HttpSession session) {
        session.invalidate();
    }
}
