package com.dooray.bookstorecarts.controller;


import com.dooray.bookstorecarts.request.CartItemRequest;
import com.dooray.bookstorecarts.response.GuestCartItemResponse;
import com.dooray.bookstorecarts.response.MemberCartItemResponse;
import com.dooray.bookstorecarts.service.GuestCartItemService;
import com.dooray.bookstorecarts.service.MemberCartItemService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart-items")
public class CartItemController {
    private final MemberCartItemService memberCartItemService;
    private final GuestCartItemService guestCartItemService;

    @PostMapping("/{cartId}")
    public ResponseEntity<MemberCartItemResponse> createMemberCartItem(@PathVariable Long cartId,
                                                                 @RequestBody CartItemRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(memberCartItemService.createMemberCartItem(cartId, request));
    }

    @PostMapping
    public ResponseEntity<GuestCartItemResponse> createGuestCartItem(HttpServletRequest httpServletRequest,
                                                            @RequestBody CartItemRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(guestCartItemService.createGuestCartItem(getSessionId(httpServletRequest), request));
    }

    @GetMapping("cart/{cartItemId}")
    public ResponseEntity<MemberCartItemResponse> getMemberCartItem(@PathVariable Long cartItemId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(memberCartItemService.getCartItemByCartItemId(cartItemId));
    }

    @GetMapping("guest-cart/{bookId}")
    public ResponseEntity<GuestCartItemResponse> getGuestCartItem(@PathVariable Long bookId,
                                                                  HttpServletRequest httpServletRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(guestCartItemService.getGuestCartItemByBookId(getSessionId(httpServletRequest), bookId));
    }

    @PatchMapping("cart/{cartItemId}")
    public ResponseEntity<MemberCartItemResponse> updateMemberCartItem(@PathVariable Long cartItemId,
                                                                 @RequestBody CartItemRequest request) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(memberCartItemService.updateQuantity(cartItemId, request));
    }

    @PatchMapping("guest-cart/{bookId}")
    public ResponseEntity<GuestCartItemResponse> updateGuestCartItem(@RequestBody CartItemRequest request,
                                                                     HttpServletRequest httpServletRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(guestCartItemService.updateQuantity(getSessionId(httpServletRequest), request));
    }

    @DeleteMapping("cart/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long cartItemId) {
        memberCartItemService.deleteCartItem(cartItemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("guest-cart/{bookId}")
    public ResponseEntity<Void> deleteGuestCartItem(@PathVariable Long bookId,
                                                    HttpServletRequest httpServletRequest) {
        guestCartItemService.deleteGuestCartItem(getSessionId(httpServletRequest), bookId);
        return ResponseEntity.noContent().build();
    }

    private String getSessionId(HttpServletRequest request) {
        return request.getSession().getId();
    }
}
