package com.dooray.bookstorecarts.controller;

import com.dooray.bookstorecarts.entity.CartItem;
import com.dooray.bookstorecarts.request.CartItemRequest;
import com.dooray.bookstorecarts.response.CartItemResponse;
import com.dooray.bookstorecarts.service.CartItemService;
import com.dooray.bookstorecarts.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart-items")
public class CartItemController {
    private final CartItemService cartItemService;
    private final CartService cartService;

    @PostMapping("/{cartId}")
    public ResponseEntity<CartItemResponse> createCartItem(@PathVariable int cartId,
                                                           @RequestBody CartItemRequest request) {
        CartItem cartItem = new CartItem();
        cartItem.setBookId(request.getBookId());
        cartItem.setQuantity(request.getQuantity());

        CartItem savedCartItem = cartItemService.addCartItem(cartId, cartItem);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CartItemResponse(savedCartItem));
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<List<CartItemResponse>> getCartItems(@PathVariable int cartId) {
        List<CartItem> cartItems = cartItemService.getCartItemsByCartId(cartId);
        List<CartItemResponse> responses = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            responses.add(new CartItemResponse(cartItem));
        }

        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{cartItemId}")
    public ResponseEntity<CartItemResponse> updateCartItem(@PathVariable int cartItemId,
                                                           @RequestBody CartItemRequest request) {
        int quantity = request.getQuantity();
        int bookId = request.getBookId();
        CartItem updated = cartItemService.updateQuantity(cartItemId, quantity, bookId);
        return ResponseEntity.ok(new CartItemResponse(updated));
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable int cartItemId) {
        cartItemService.deleteCartItem(cartItemId);
        return ResponseEntity.noContent().build();
    }
}
