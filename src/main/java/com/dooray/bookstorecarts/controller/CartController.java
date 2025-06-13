package com.dooray.bookstorecarts.controller;

import com.dooray.bookstorecarts.request.CartRequest;
import com.dooray.bookstorecarts.response.MemberCartResponse;
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
    private final MemberCartService memberCartService;
    private final GuestCartService guestCartService;
    private final MemberCartItemService membercartItemService;
    private final CartService cartService;

    // postmapping validation 유효성검사 필수
    @PostMapping // 컨트롤러가 유저 아이디 보내면 회원카트 생성, 널을 보내면 비회원 카트 생성
    public ResponseEntity<?> createCart(@RequestHeader(value = "X-User-Id", required = false)Long userId,
                                                         HttpServletRequest httpServletRequest ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cartService.createCart(userId, getSessionId(httpServletRequest)));
    }

    @DeleteMapping("cart/{cartId}")  // 회원 장바구니 삭제
    public ResponseEntity<Void> deleteCart(@PathVariable Long cartId) {
        memberCartService.deleteMemberCart(cartId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("guest-cart/{sessionId}") // 비회원 장바구니 삭제
    public ResponseEntity<Void> deleteGuestCart(@PathVariable String sessionId) {
        guestCartService.deleteGuestCart(sessionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping // 이렇게 하면 껐다 켰을때 유지가 될까? 모르겟네
    public ResponseEntity<?> getCart(@RequestHeader(value = "X-User-Id", required = false)Long userId,
                                                      HttpServletRequest httpServletRequest ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cartService.getCart(userId, getSessionId(httpServletRequest)));
    }

    @PostMapping("/merge")  // 비회원에서 로그인한 순간 세션 장바구니와 회원 장바구니를 병합(프론트에서 로그인성공후 병합 요청)
    public ResponseEntity<MemberCartResponse> mergeCarts(@RequestBody CartRequest request,
                                                         HttpServletRequest httpServletRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cartService.mergeCarts(request.getUserId(), getSessionId(httpServletRequest)));
    }

    private String getSessionId(HttpServletRequest request) {
        return request.getSession().getId();
    }
}
