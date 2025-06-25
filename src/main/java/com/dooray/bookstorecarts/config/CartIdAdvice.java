package com.dooray.bookstorecarts.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.UUID;

@ControllerAdvice
public class CartIdAdvice {
    @ModelAttribute("guestCartId")
    public String guestCartId(@CookieValue(value = "guestCartId", required = false) String cartId,
                              HttpServletResponse response) {
        if (cartId == null) {
            cartId = UUID.randomUUID().toString();
            Cookie cookie = new Cookie("guestCartId", cartId);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24);
            response.addCookie(cookie);
        }
        return cartId;
    }
}
