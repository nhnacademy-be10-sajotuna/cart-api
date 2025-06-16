package com.dooray.bookstorecarts.controller;

import com.dooray.bookstorecarts.request.CartItemRequest;
import com.dooray.bookstorecarts.response.UserCartItemResponse;
import com.dooray.bookstorecarts.service.UserCartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-cart-items")
public class UserCartItemController {
    private final UserCartItemService userCartItemService;

    @PostMapping("/{cartId}")
    public ResponseEntity<UserCartItemResponse> createUserCartItem(@PathVariable Long cartId,
                                                                   @RequestBody CartItemRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userCartItemService.createUserCartItem(cartId, request));
    }

    @GetMapping("/{cartItemId}")
    public ResponseEntity<UserCartItemResponse> getUserCartItem(@PathVariable Long cartItemId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userCartItemService.getCartItemByCartItemId(cartItemId));
    }

    @PatchMapping("/{cartItemId}")
    public ResponseEntity<UserCartItemResponse> updateUserCartItem(@PathVariable Long cartItemId,
                                                                   @RequestBody CartItemRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userCartItemService.updateQuantity(cartItemId, request));
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long cartItemId) {
        userCartItemService.deleteCartItem(cartItemId);
        return ResponseEntity.noContent().build();
    }
}
