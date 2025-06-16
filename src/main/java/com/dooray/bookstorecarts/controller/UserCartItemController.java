package com.dooray.bookstorecarts.controller;

import com.dooray.bookstorecarts.request.CartItemRequest;
import com.dooray.bookstorecarts.response.UserCartItemResponse;
import com.dooray.bookstorecarts.service.UserCartItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-cart-items")
public class UserCartItemController {
    private final UserCartItemService userCartItemService;
    // 장바구니에 책담기(해당 유저의 장바구니가 없을경우 장바구니 생성)
    @PostMapping
    public ResponseEntity<UserCartItemResponse> createUserCartItem(@RequestHeader(value = "X-User-Id") Long userId,
                                                                   @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userCartItemService.createUserCartItem(userId, request));
    }
    // 장바구니 책 단건조회
    @GetMapping("/{cartItemId}")
    public ResponseEntity<UserCartItemResponse> getUserCartItem(@PathVariable Long cartItemId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userCartItemService.getCartItemByCartItemId(cartItemId));
    }
    // 장바구니 책 수량 변경
    @PatchMapping("/{cartItemId}")
    public ResponseEntity<UserCartItemResponse> updateUserCartItem(@PathVariable Long cartItemId,
                                                                   @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userCartItemService.updateQuantity(cartItemId, request));
    }
    // 장바구니 책 삭제(단건 삭제)
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> deleteUserCartItem(@PathVariable Long cartItemId) {
        userCartItemService.deleteCartItem(cartItemId);
        return ResponseEntity.noContent().build();
    }
    // 장바구니 비우기
    @DeleteMapping
    public ResponseEntity<Void> clearUserCartItems(@RequestHeader(value = "X-User-Id") Long userId) {
        userCartItemService.deleteAllCartItemsFromUserId(userId);
        return ResponseEntity.noContent().build();
    }
}
