package com.dooray.bookstorecarts.controller;

import com.dooray.bookstorecarts.request.CartItemRequest;
import com.dooray.bookstorecarts.response.CartItemResponse;
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
    public ResponseEntity<Void> addUserCartItem(@RequestHeader(value = "X-User-Id") Long userId,
                                                @Valid @RequestBody CartItemRequest request) {
        userCartItemService.addUserCartItem(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    // 장바구니 책 단건 조회 ( 필요없어서 삭제 )
    // 장바구니 책 수량 변경
    @PostMapping("/update/{cartItemId}")
    public ResponseEntity<Void> updateUserCartItem(@PathVariable Long cartItemId,
                                                                   @Valid @RequestBody CartItemRequest request) {
        userCartItemService.updateQuantity(cartItemId, request);
        return ResponseEntity.status(HttpStatus.OK).build();
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
