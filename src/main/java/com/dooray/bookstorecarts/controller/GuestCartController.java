package com.dooray.bookstorecarts.controller;

import com.dooray.bookstorecarts.response.GuestCartResponse;
import com.dooray.bookstorecarts.service.GuestCartService;
import com.dooray.bookstorecarts.service.GuestCartService1;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/guest-carts")
public class GuestCartController {
    private final GuestCartService1 guestCartService;

    // 장바구니 조회(비회원 장바구니 조회 - 모든 아이템 조회)
    @GetMapping
    public ResponseEntity<GuestCartResponse> getGuestCart(HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(guestCartService.getCartBySession(getSession(request)));
    }
    // 비회원 장바구니 수동삭제(레디스에서 자동삭제되게 하였지만 혹시나 필요할경우 사용)
    @DeleteMapping
    public ResponseEntity<Void> deleteGuestCart(HttpServletRequest request) {
        guestCartService.deleteGuestCart(getSession(request));
        return ResponseEntity.noContent().build();
    }

    private HttpSession getSession(HttpServletRequest request) {
        return request.getSession();
    }
}
