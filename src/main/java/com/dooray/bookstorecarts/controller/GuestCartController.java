package com.dooray.bookstorecarts.controller;

import com.dooray.bookstorecarts.response.CartResponse;
import com.dooray.bookstorecarts.service.GuestCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/guest-carts")
public class GuestCartController {
    private final GuestCartService guestCartService;

    // 장바구니 조회(비회원 장바구니 조회 - 모든 아이템 조회)
    @GetMapping
    public ResponseEntity<CartResponse> getGuestCart(@RequestHeader(value = "X-Guest-Cart-Id") String cartId) {
        System.out.println(cartId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(guestCartService.getCartByCartId(cartId));
    }
    // 비회원 장바구니 수동삭제(레디스에서 자동삭제되게 하였지만 혹시나 필요할경우 사용)
    @DeleteMapping
    public ResponseEntity<Void> deleteGuestCart(@RequestHeader(value = "X-Guest-Cart-Id") String cartId) {
        guestCartService.deleteGuestCart(cartId);
        return ResponseEntity.noContent().build();
    }
}
