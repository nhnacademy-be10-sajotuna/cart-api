package com.dooray.bookstorecarts.controller;

import com.dooray.bookstorecarts.response.UserCartResponse;
import com.dooray.bookstorecarts.service.*;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<UserCartResponse> mergeCarts(@RequestHeader(value = "X-User-Id") Long userId,
                                                       HttpServletRequest httpServletRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cartService.mergeCarts(userId, getSessionId(httpServletRequest)));
    }

    private String getSessionId(HttpServletRequest request) {
        return request.getSession().getId();
    }
}
