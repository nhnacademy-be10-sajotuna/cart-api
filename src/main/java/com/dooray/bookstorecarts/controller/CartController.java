package com.dooray.bookstorecarts.controller;

import com.dooray.bookstorecarts.response.CartResponse;
import com.dooray.bookstorecarts.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {
    private final CartService cartService;

    @PostMapping("/merge")  // 비회원에서 로그인한 순간 세션 장바구니와 회원 장바구니를 병합(프론트에서 로그인성공후 병합 요청)
    public ResponseEntity<CartResponse> mergeCarts(@RequestHeader(value = "X-User-Id") Long userId,
                                                   @RequestHeader(value = "X-Guest-Cart-Id") String cartId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cartService.mergeCarts(userId, cartId));
    }
}
