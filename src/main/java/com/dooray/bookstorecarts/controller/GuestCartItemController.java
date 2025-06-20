package com.dooray.bookstorecarts.controller;

import com.dooray.bookstorecarts.request.CartItemRequest;
import com.dooray.bookstorecarts.response.GuestCartItemResponse;
import com.dooray.bookstorecarts.service.GuestCartItemService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
    public ResponseEntity<GuestCartItemResponse> createGuestCartItem(@Valid @RequestBody CartItemRequest request,
                                                                     HttpServletRequest httpServletRequest){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(guestCartItemService.addGuestCartItem(getSession(httpServletRequest), request));
    }
    // 비회원 장바구니 책 단건조회
    @GetMapping("/{bookId}")
    public ResponseEntity<GuestCartItemResponse> getGuestCartItem(@PathVariable Long bookId,
                                                                  HttpServletRequest httpServletRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(guestCartItemService.getGuestCartItemByBookId(getSession(httpServletRequest), bookId));
    }
    // 비회원 장바구니 책 수량 변경
    @PatchMapping
    public ResponseEntity<GuestCartItemResponse> updateGuestCartItem(@Valid @RequestBody CartItemRequest request,
                                                                     HttpServletRequest httpServletRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(guestCartItemService.updateQuantity(getSession(httpServletRequest), request));
    }
    // 비회원 장바구니 책 삭제(단건 삭제)
    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteGuestCartItem(@PathVariable Long bookId,
                                                    HttpServletRequest httpServletRequest) {
        guestCartItemService.deleteGuestCartItem(getSession(httpServletRequest), bookId);
        return ResponseEntity.noContent().build();
    }
    // 비회원 장바구니 비우기
    @DeleteMapping
    public ResponseEntity<Void>  clearGuestCartItems(HttpServletRequest httpServletRequest) {
        guestCartItemService.deleteAllGuestCartItems(getSession(httpServletRequest));
        return ResponseEntity.noContent().build();

    }
    private HttpSession getSession(HttpServletRequest request) {
        return request.getSession();
    }
}
