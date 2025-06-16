package com.dooray.bookstorecarts.service;

import com.dooray.bookstorecarts.exception.*;
import com.dooray.bookstorecarts.redisdto.GuestCart;
import com.dooray.bookstorecarts.redisdto.GuestCartItem;
import com.dooray.bookstorecarts.repository.GuestCartRepository;
import com.dooray.bookstorecarts.repository.UserCartItemRepository;
import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;
import com.dooray.bookstorecarts.repository.UserCartRepository;
import com.dooray.bookstorecarts.response.GuestCartResponse;
import com.dooray.bookstorecarts.response.UserCartResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final UserCartItemService userCartItemService;
    private final UserCartRepository userCartRepository;
    private final UserCartItemRepository userCartItemRepository;
    private final GuestCartRepository guestCartRepository;

    @Transactional
    public UserCartResponse mergeCarts(Long userId, String sessionId) {
        GuestCart guestCart = guestCartRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new CartNotFoundException(sessionId));
        Cart cart = userCartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));

        for (GuestCartItem guestCartItem : guestCart.getItems()) {
            CartItem cartItem = userCartItemRepository.findByCartAndBookId(cart, guestCartItem.getBookId());

            if(cartItem != null) {
                cartItem.setQuantity(cartItem.getQuantity() + guestCartItem.getQuantity());
                userCartItemRepository.save(cartItem);
            }else {
                CartItem newCartItem = new CartItem();
                newCartItem.setCart(cart);
                newCartItem.setBookId(guestCartItem.getBookId());
                newCartItem.setQuantity(guestCartItem.getQuantity());
                userCartItemRepository.save(newCartItem);
            }
        }
        guestCartRepository.delete(guestCart.getSessionId());

        List<CartItem> items = userCartItemService.getCartItemsByCartId(cart.getId());
        return new UserCartResponse(cart, items);
    }
}
