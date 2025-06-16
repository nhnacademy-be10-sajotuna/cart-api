package com.dooray.bookstorecarts.controller;

import com.dooray.bookstorecarts.request.CartItemRequest;
import com.dooray.bookstorecarts.response.GuestCartItemResponse;
import com.dooray.bookstorecarts.service.GuestCartItemService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/guest-cart-items")
public class GuestCartItemController {
    private final GuestCartItemService guestCartItemService;

    @PostMapping
    public ResponseEntity<GuestCartItemResponse> createGuestCartItem(HttpServletRequest httpServletRequest,
                                                                     @RequestBody CartItemRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(guestCartItemService.createGuestCartItem(getSessionId(httpServletRequest), request));
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<GuestCartItemResponse> getGuestCartItem(@PathVariable Long bookId,
                                                                  HttpServletRequest httpServletRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(guestCartItemService.getGuestCartItemByBookId(getSessionId(httpServletRequest), bookId));
    }

    @PatchMapping("/{bookId}")
    public ResponseEntity<GuestCartItemResponse> updateGuestCartItem(@RequestBody CartItemRequest request,
                                                                     HttpServletRequest httpServletRequest) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(guestCartItemService.updateQuantity(getSessionId(httpServletRequest), request));
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteGuestCartItem(@PathVariable Long bookId,
                                                    HttpServletRequest httpServletRequest) {
        guestCartItemService.deleteGuestCartItem(getSessionId(httpServletRequest), bookId);
        return ResponseEntity.noContent().build();
    }

    private String getSessionId(HttpServletRequest request) {
        return request.getSession().getId();
    }
}
