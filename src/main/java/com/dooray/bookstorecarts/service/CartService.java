package com.dooray.bookstorecarts.service;

import com.dooray.bookstorecarts.exception.*;
import com.dooray.bookstorecarts.redisdto.GuestCart;
import com.dooray.bookstorecarts.redisdto.GuestCartItem;
import com.dooray.bookstorecarts.repository.GuestCartRepository;
import com.dooray.bookstorecarts.repository.MemberCartItemRepository;
import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;
import com.dooray.bookstorecarts.repository.MemberCartRepository;
import com.dooray.bookstorecarts.response.GuestCartResponse;
import com.dooray.bookstorecarts.response.MemberCartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final MemberCartService memberCartService;
    private final GuestCartService guestCartService;
    private final MemberCartItemService memberCartItemService;
    private final GuestCartItemService guestCartItemService;
    private final MemberCartRepository memberCartRepository;
    private final MemberCartItemRepository memberCartItemRepository;
    private final GuestCartRepository guestCartRepository;

    public Object createCart(Long userId, String sessionId){
        if(userId != null){
            Cart savedCart = memberCartService.createMemberCart(userId);
            List<CartItem> emptyItems = List.of();
            return new MemberCartResponse(savedCart, emptyItems);
        }else {
            GuestCart guestSavedCart = guestCartService.createGuestCart(sessionId);
            return new GuestCartResponse(guestSavedCart);
        }
    }

    public Object getCart(Long userId, String sessionId){
        if(userId != null){
            Cart cart = memberCartService.getCartByUserId(userId);
            List<CartItem> items = memberCartItemService.getCartItemsByCartId(cart.getId());
            return new MemberCartResponse(cart, items);
        }
        else{
            GuestCart guestCart = guestCartService.getCartBySessionId(sessionId);
            return new GuestCartResponse(guestCart);
        }
    }

    public MemberCartResponse mergeCarts(Long userId, String sessionId) {
        GuestCart guestCart = guestCartRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new CartNotFoundException(sessionId));
        Cart cart = memberCartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));

        for (GuestCartItem guestCartItem : guestCart.getItems()) {
            CartItem cartItem = memberCartItemRepository.findByCartAndBookId(cart, guestCartItem.getBookId());

            if(cartItem != null) {
                cartItem.setQuantity(cartItem.getQuantity() + guestCartItem.getQuantity());
                memberCartItemRepository.save(cartItem);
            }else {
                CartItem newCartItem = new CartItem();
                newCartItem.setCart(cart);
                newCartItem.setBookId(guestCartItem.getBookId());
                newCartItem.setQuantity(guestCartItem.getQuantity());
                memberCartItemRepository.save(newCartItem);
            }
        }
        guestCartRepository.delete(guestCart.getSessionId());

        List<CartItem> items = memberCartItemService.getCartItemsByCartId(cart.getId());
        return new MemberCartResponse(cart, items);
    }
}
