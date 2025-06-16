package com.dooray.bookstorecarts.controller;

import com.dooray.bookstorecarts.response.UserCartResponse;
import com.dooray.bookstorecarts.service.UserCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-carts")
public class UserCartController {
    private final UserCartService userCartService;

    @PostMapping
    public ResponseEntity<UserCartResponse> createUserCart(@RequestHeader(value = "X-User-Id", required = false)Long userId){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userCartService.createUserCart(userId));
    }

    @GetMapping
    public ResponseEntity<UserCartResponse> getUserCart(@RequestHeader(value = "X-User-Id", required = false) Long userId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userCartService.getCartByUserId(userId));
    }

    @DeleteMapping("/{cartId}")  // 회원 장바구니 삭제
    public ResponseEntity<Void> deleteUserCart(@PathVariable Long cartId) {
        userCartService.deleteUserCart(cartId);
        return ResponseEntity.noContent().build();
    }
}
