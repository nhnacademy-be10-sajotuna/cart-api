package com.dooray.bookstorecarts.controller;

import com.dooray.bookstorecarts.response.GuestCartResponse;
import com.dooray.bookstorecarts.service.GuestCartService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/guest-carts")
public class GuestCartController {
    private final GuestCartService guestCartService;

    @PostMapping
    public ResponseEntity<GuestCartResponse> createGuestCart(HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(guestCartService.createGuestCart(getSessionId(request)));
    }

    @GetMapping
    public ResponseEntity<GuestCartResponse> getGuestCart(HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(guestCartService.getCartBySessionId(getSessionId(request)));
    }

    @DeleteMapping // 비회원 장바구니 삭제
    public ResponseEntity<Void> deleteGuestCart(HttpServletRequest request) {
        guestCartService.deleteGuestCart(getSessionId(request));
        return ResponseEntity.noContent().build();
    }

    private String getSessionId(HttpServletRequest request) {
        return request.getSession().getId();
    }
}
