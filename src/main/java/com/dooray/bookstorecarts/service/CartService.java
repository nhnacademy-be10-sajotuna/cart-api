package com.dooray.bookstorecarts.service;

import com.dooray.bookstorecarts.repository.CartItemRepository;
import com.dooray.bookstorecarts.repository.CartRepository;
import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;
import com.dooray.bookstorecarts.exception.CartAlreadyExistsException;
import com.dooray.bookstorecarts.exception.CartMergeException;
import com.dooray.bookstorecarts.exception.CartNotFoundException;
import com.dooray.bookstorecarts.exception.InvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartItemService cartItemService;

    public Cart saveCart(Cart cart){
        if (cart.getUserId() !=0 && cartRepository.existsByUserId(cart.getUserId())){
            throw new CartAlreadyExistsException(cart.getUserId());
        }

        if(cart.getSessionId() != null && cartRepository.existsBySessionId(cart.getSessionId())){
            throw new CartAlreadyExistsException(cart.getSessionId());
        }
        return cartRepository.save(cart);
    }

    public Cart getCartByUserId(int userId) {
        if (!cartRepository.existsByUserId(userId)) {
            throw new CartNotFoundException(userId);
        }
        return cartRepository.findByUserId(userId);
    }

    public Cart getCartBySessionId(String sessionId) {
        if (!cartRepository.existsBySessionId(sessionId)) {
            throw new CartNotFoundException(sessionId);
        }
        return cartRepository.findBySessionId(sessionId);
    }

    public Cart getCartById(int cartId) {
        if (!cartRepository.existsById(cartId)) {
            throw new CartNotFoundException(cartId);
        }
        return cartRepository.findById(cartId);
    }

    public void deleteCart(Cart cart){
        if (cart == null) {
            throw new InvalidException("삭제할 카트가 null 입니다.");
        }
        cartItemService.deleteAllCartItemsByCartId(cart.getId());
        cartRepository.delete(cart);
    }

    public Cart mergeCarts(Cart sessionCart, Cart userCart) {
        if (sessionCart == null || userCart == null) {
            throw new CartMergeException("sessionCart 또는 userCart 가 null 입니다.");
        }
        List<CartItem> sessionItems = cartItemRepository.findByCart(sessionCart);

        for (CartItem sessionItem: sessionItems) {
            CartItem existingItem = cartItemRepository.findByCartAndBookId(userCart, sessionItem.getBookId());

            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + sessionItem.getQuantity());
                cartItemRepository.save(existingItem);
                cartItemService.deleteCartItem(sessionItem.getId());
            }else{
                sessionItem.setCart(userCart);
                cartItemRepository.save(sessionItem);
            }
        }
        deleteCart(sessionCart);
        return userCart;
    }
}
