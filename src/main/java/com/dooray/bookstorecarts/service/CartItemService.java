package com.dooray.bookstorecarts.service;

import com.dooray.bookstorecarts.repository.CartItemRepository;
import com.dooray.bookstorecarts.repository.CartRepository;
import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;
import com.dooray.bookstorecarts.exception.CartItemNotFoundException;
import com.dooray.bookstorecarts.exception.CartNotFoundException;
import com.dooray.bookstorecarts.exception.InvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartItemService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public CartItem addCartItem(int cartId, CartItem cartItem) {
        if (!cartRepository.existsById(cartId)) {
            throw new CartNotFoundException(cartId);
        }

        Cart cart = cartRepository.findById(cartId);

        CartItem existingItem = cartItemRepository.findByCartAndBookId(cart, cartItem.getBookId());
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + cartItem.getQuantity());
            return cartItemRepository.save(existingItem);
        }

        cartItem.setCart(cart);
        return cartItemRepository.save(cartItem);
    }

    public CartItem getCartItem(int cartItemId) {
        return cartItemRepository.findById(cartItemId);
    }

    public List<CartItem> getCartItemsByCartId(int cartId){
        if (!cartRepository.existsById(cartId)) {
            throw new CartNotFoundException(cartId);
        }
        Cart cart = cartRepository.findById(cartId);
        return cartItemRepository.findByCart(cart);
    }

    public CartItem updateQuantity(int cartItemId, int quantity, int bookId) {
        if (!cartItemRepository.existsById(cartItemId)) {
            throw new CartItemNotFoundException(cartItemId);
        }
        CartItem cartItem = getCartItem(cartItemId);

        if (cartItem.getBookId() != bookId) {
            throw new InvalidException("요청한 bookId와 해당 cartItem 의 bookId가 일치하지 않습니다.");
        }

        if (quantity <= 0) throw new InvalidException("수량은 0보다 커야합니다. ");
        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }

    public void deleteCartItem(int cartItemId) {
        if (!cartItemRepository.existsById(cartItemId)) {
            throw new CartItemNotFoundException(cartItemId);
        }
        cartItemRepository.deleteById(cartItemId);
    }

    public void deleteAllCartItemsByCartId(int cartId) {
        List<CartItem> items = getCartItemsByCartId(cartId);
        if (items.isEmpty()) {
            throw new CartItemNotFoundException();
        }
        cartItemRepository.deleteAll(items);
    }
}
