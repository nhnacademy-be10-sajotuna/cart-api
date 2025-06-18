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
    // 장바구니 조회(유저의 장바구니 조회 - 모든 아이템 조회)
    @GetMapping
    public ResponseEntity<UserCartResponse> getUserCart(@RequestHeader(value = "X-User-Id") Long userId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userCartService.getCartByUserId(userId));
    }
    // 장바구니 완전삭제(유저가 회원탈퇴할때 카트가 db에 남지 않도록)
    @DeleteMapping
    public ResponseEntity<Void> deleteUserCart(@RequestHeader(value = "X-User-Id") Long userId) {
        userCartService.deleteUserCart(userId);
        return ResponseEntity.noContent().build();
    }
}
