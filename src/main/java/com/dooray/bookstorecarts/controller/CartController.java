package com.dooray.bookstorecarts.controller;

import com.dooray.bookstorecarts.entity.CartItem;
import com.dooray.bookstorecarts.request.CartRequest;
import com.dooray.bookstorecarts.response.CartResponse;
import com.dooray.bookstorecarts.service.CartItemService;
import com.dooray.bookstorecarts.service.CartService;
import com.dooray.bookstorecarts.entity.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {
    private final CartItemService cartItemService;
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartResponse> createCart(@RequestBody CartRequest request) {
        Cart cart = new Cart();
        cart.setUserId(request.getUserId() != null ? request.getUserId() : 0);
        cart.setSessionId(request.getSessionId());

        Cart savedCart = cartService.saveCart(cart);

        List<CartItem> emptyItems = List.of();

        return ResponseEntity.status(HttpStatus.CREATED).body(new CartResponse(savedCart, emptyItems));
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> deleteCart(@PathVariable int cartId) {
        Cart cart = cartService.getCartById(cartId);
        cartService.deleteCart(cart);
        return ResponseEntity.noContent().build();
    }

    @GetMapping // 회원인경우 GET /carts?userId=10, 비회원인경우 GET /carts?sessionId=abc123
    public ResponseEntity<CartResponse> getCart(@RequestParam(required = false) Integer userId,
                                                @RequestParam(required = false) String sessionId) {
        Cart cart;
        if (userId != null) {
            cart = cartService.getCartByUserId(userId);
        }else if (sessionId != null) {
            cart = cartService.getCartBySessionId(sessionId);
        }else {
            return ResponseEntity.badRequest().build();
        }
        List<CartItem> items = cartItemService.getCartItemsByCartId(cart.getId());

        return ResponseEntity.ok(new CartResponse(cart, items));
    }

    @PostMapping("/merge")  // 비회원에서 로그인한 순간 세션 장바구니와 회원 장바구니를 병합(프론트에서 로그인성공후 병합 요청)
    public ResponseEntity<CartResponse> mergeCarts(@RequestBody CartRequest request){
        Cart sessionCart = cartService.getCartBySessionId(request.getSessionId());
        Cart userCart = cartService.getCartByUserId(request.getUserId());

        Cart mergedCart = cartService.mergeCarts(sessionCart, userCart);
        List<CartItem> items = cartItemService.getCartItemsByCartId(mergedCart.getId());

        return ResponseEntity.ok(new CartResponse(mergedCart, items));
    }

}
