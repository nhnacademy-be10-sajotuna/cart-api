package com.dooray.bookstorecarts.controller;

import com.dooray.bookstorecarts.request.CartItemRequest;
import com.dooray.bookstorecarts.response.CartItemResponse;
import com.dooray.bookstorecarts.service.GuestCartItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/guest-cart-items")
public class GuestCartItemController {
    private final GuestCartItemService guestCartItemService;
    // 비회원 장바구니에 책담기(해당세션의 장바구니가 없을경우 장바구니 생성)
    @PostMapping
    public ResponseEntity<CartItemResponse> addGuestCartItem(@Valid @RequestBody CartItemRequest request,
                                                             @RequestHeader(value = "X-Guest-Cart-Id") String cartId) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(guestCartItemService.addGuestCartItem(cartId, request));
    }
    // 비회원 장바구니 책 단건조회
    @GetMapping("/{bookId}")
    public ResponseEntity<CartItemResponse> getGuestCartItem(@PathVariable String bookId,
                                                             @RequestHeader(value = "X-Guest-Cart-Id") String cartId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(guestCartItemService.getGuestCartItemByIsbn(cartId, bookId));
    }
    // 비회원 장바구니 책 수량 변경
    @PatchMapping
    public ResponseEntity<CartItemResponse> updateGuestCartItem(@Valid @RequestBody CartItemRequest request,
                                                                @RequestHeader(value = "X-Guest-Cart-Id") String cartId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(guestCartItemService.updateQuantity(cartId, request));
    }
    // 비회원 장바구니 책 삭제(단건 삭제)
    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteGuestCartItem(@PathVariable String bookId,
                                                    @RequestHeader(value = "X-Guest-Cart-Id") String cartId) {
        guestCartItemService.deleteGuestCartItem(cartId, bookId);
        return ResponseEntity.noContent().build();
    }
    // 비회원 장바구니 비우기
    @DeleteMapping
    public ResponseEntity<Void>  clearGuestCartItems(@RequestHeader(value = "X-Guest-Cart-Id") String cartId) {
        guestCartItemService.deleteAllGuestCartItems(cartId);
        return ResponseEntity.noContent().build();
    }
}
