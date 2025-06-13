package com.dooray.bookstorecarts.controller;

import com.dooray.bookstorecarts.entity.CartItem;
import com.dooray.bookstorecarts.redisdto.GuestCart;
import com.dooray.bookstorecarts.redisdto.GuestCartItem;
import com.dooray.bookstorecarts.request.CartRequest;
import com.dooray.bookstorecarts.response.GuestCartResponse;
import com.dooray.bookstorecarts.response.MemberCartResponse;
import com.dooray.bookstorecarts.service.*;
import com.dooray.bookstorecarts.entity.Cart;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {
    private final MemberCartService memberCartService;
    private final GuestCartService guestCartService;
    private final MemberCartItemService membercartItemService;
    private final GuestCartItemService guestCartItemService;
//    private final CartService cartService;

    @PostMapping // 컨트롤러가 유저 아이디 보내면 회원카트 생성, 널을 보내면 비회원 카트 생성
    public ResponseEntity<?> createCart(@RequestHeader(value = "X-User-Id", required = false)Long userId,
                                                         HttpServletRequest httpServletRequest ) {

        List<CartItem> emptyItems = List.of();
        String sessionId = httpServletRequest.getSession().getId();

        if (userId != null) {
            Cart savedCart = memberCartService.createMemberCart(userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(new MemberCartResponse(savedCart, emptyItems));
        }
        else{
            GuestCart guestSavedCart = guestCartService.createGuestCart(sessionId);
            return ResponseEntity.status(HttpStatus.CREATED).body(new GuestCartResponse(guestSavedCart));
        }
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

        if (userId != null) {
            Cart cart = memberCartService.getCartByUserId(userId);
            List<CartItem> items = membercartItemService.getCartItemsByCartId(cart.getId());
            return ResponseEntity.ok(new MemberCartResponse(cart, items));
        }
        else{
            String sessionId = httpServletRequest.getSession().getId();
            GuestCart guestCart = guestCartService.getCartBySessionId(sessionId);
            return ResponseEntity.ok(new GuestCartResponse(guestCart));
        }
    }

//    @PostMapping("/merge")  // 비회원에서 로그인한 순간 세션 장바구니와 회원 장바구니를 병합(프론트에서 로그인성공후 병합 요청)
//    public ResponseEntity<MemberCartResponse> mergeCarts(@RequestBody CartRequest request){
//        Cart sessionCart = cartService.getCartBySessionId(request.getSessionId());
//        Cart userCart = cartService.getCartByUserId(request.getUserId());
//
//        Cart mergedCart = cartService.mergeCarts(sessionCart, userCart);
//        List<CartItem> items = cartItemService.getCartItemsByCartId(mergedCart.getId());
//
//        return ResponseEntity.ok(new MemberCartResponse(mergedCart, items));
//    }
}
