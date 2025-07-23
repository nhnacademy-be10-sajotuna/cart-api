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
    public ResponseEntity<Void> addGuestCartItem(@RequestHeader(value = "X-Guest-Cart-Id") String cartId,
                                                 @Valid @RequestBody CartItemRequest request) {
        guestCartItemService.addGuestCartItem(cartId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    // 비회원 장바구니 책 단건조회 - 필요없음
    // 비회원 장바구니 책 수량 변경
    @PostMapping("/update")
    public ResponseEntity<Void> updateGuestCartItem(@RequestHeader(value = "X-Guest-Cart-Id") String cartId,
                                                    @Valid @RequestBody CartItemRequest request) {
        guestCartItemService.updateQuantity(cartId, request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 비회원 장바구니 책 삭제(단건 삭제)
    @DeleteMapping("/{isbn}")
    public ResponseEntity<Void> deleteGuestCartItem(@RequestHeader(value = "X-Guest-Cart-Id") String cartId,
                                                    @PathVariable String isbn) {
        guestCartItemService.deleteGuestCartItem(cartId, isbn);
        return ResponseEntity.noContent().build();
    }

    // 비회원 장바구니 비우기
    @DeleteMapping
    public ResponseEntity<Void>  clearGuestCartItems(@RequestHeader(value = "X-Guest-Cart-Id") String cartId) {
        guestCartItemService.deleteAllGuestCartItems(cartId);
        return ResponseEntity.noContent().build();
    }
}
