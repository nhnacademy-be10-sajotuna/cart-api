package com.dooray.bookstorecarts.controller;

import com.dooray.bookstorecarts.entity.CartItem;
import com.dooray.bookstorecarts.redisdto.GuestCartItem;
import com.dooray.bookstorecarts.request.CartItemRequest;
import com.dooray.bookstorecarts.response.GuestCartItemResponse;
import com.dooray.bookstorecarts.response.GuestCartResponse;
import com.dooray.bookstorecarts.response.MemberCartItemResponse;
import com.dooray.bookstorecarts.service.GuestCartItemService;
import com.dooray.bookstorecarts.service.MemberCartItemService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart-items")
public class CartItemController {
//    private final CartService cartService;
    private final MemberCartItemService memberCartItemService;
    private final GuestCartItemService guestCartItemService;

    @PostMapping("/{cartId}")
    public ResponseEntity<MemberCartItemResponse> createMemberCartItem(@PathVariable Long cartId,
                                                                 @RequestBody CartItemRequest request) {
        CartItem savedCartItem = memberCartItemService.createMemberCartItem(cartId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MemberCartItemResponse(savedCartItem));
    }

    @PostMapping
    public ResponseEntity<GuestCartItemResponse> createGuestCartItem(HttpServletRequest httpServletRequest,
                                                            @RequestBody CartItemRequest request) {
        String sessionId = httpServletRequest.getSession().getId();
        GuestCartItem guestCartItem = guestCartItemService.createGuestCartItem(sessionId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new GuestCartItemResponse(guestCartItem));
    }

    @GetMapping("cart/{cartItemId}")
    public ResponseEntity<MemberCartItemResponse> getMemberCartItem(@PathVariable Long cartItemId) {
        CartItem cartItem = memberCartItemService.getCartItemByCartItemId(cartItemId);
        return ResponseEntity.status(HttpStatus.OK).body(new MemberCartItemResponse(cartItem));
    }

    @GetMapping("guest-cart/{bookId}")
    public ResponseEntity<GuestCartItemResponse> getGuestCartItem(@PathVariable Long bookId,
                                                                  HttpServletRequest httpServletRequest) {
        String sessionId = httpServletRequest.getSession().getId();
        GuestCartItem guestCartItem = guestCartItemService.getGuestCartItemByBookId(sessionId, bookId);
        return ResponseEntity.status(HttpStatus.OK).body(new GuestCartItemResponse(guestCartItem));
    }

    @PatchMapping("cart/{cartItemId}")
    public ResponseEntity<MemberCartItemResponse> updateMemberCartItem(@PathVariable Long cartItemId,
                                                                 @RequestBody CartItemRequest request) {
        CartItem updated = memberCartItemService.updateQuantity(cartItemId, request);
        return ResponseEntity.status(HttpStatus.OK).body(new MemberCartItemResponse(updated));
    }

    @PatchMapping("guest-cart/{bookId}")
    public ResponseEntity<GuestCartItemResponse> updateGuestCartItem(@RequestBody CartItemRequest request,
                                                                     HttpServletRequest httpServletRequest) {
        String sessionId = httpServletRequest.getSession().getId();
        GuestCartItem updated = guestCartItemService.updateQuantity(sessionId, request);
        return ResponseEntity.status(HttpStatus.OK).body(new GuestCartItemResponse(updated));
    }

    @DeleteMapping("cart/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long cartItemId) {
        memberCartItemService.deleteCartItem(cartItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("guest-cart/{bookId}")
    public ResponseEntity<Void> deleteGuestCartItem(@PathVariable Long bookId,
                                                    HttpServletRequest httpServletRequest) {
        String sessionId = httpServletRequest.getSession().getId();
        guestCartItemService.deleteGuestCartItem(sessionId, bookId);
        return ResponseEntity.noContent().build();
    }
}
