package com.dooray.bookstorecarts.service;

import com.dooray.bookstorecarts.exception.*;
import com.dooray.bookstorecarts.redisdto.GuestCart;
import com.dooray.bookstorecarts.repository.MemberCartItemRepository;
import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

//@Service
//@RequiredArgsConstructor
//public class CartService {
//
//    private final MemberCartItemRepository memberCartItemRepository;
//    private final MemberCartItemService membercartItemService;
//
//
//    public Cart mergeCarts(GuestCart sessionCart, Cart userCart) {
//        if (sessionCart == null || userCart == null) {
//            throw new CartMergeException("sessionCart 또는 userCart 가 null 입니다.");
//        }
//        List<CartItem> sessionItems = memberCartItemRepository.findByCart(sessionCart);
//
//        for (CartItem sessionItem: sessionItems) {
//            CartItem existingItem = memberCartItemRepository.findByCartAndBookId(userCart, sessionItem.getBookId());
//
//            if (existingItem != null) {
//                existingItem.setQuantity(existingItem.getQuantity() + sessionItem.getQuantity());
//                memberCartItemRepository.save(existingItem);
//                membercartItemService.deleteCartItem(sessionItem.getId());
//            }else{
//                sessionItem.setCart(userCart);
//                memberCartItemRepository.save(sessionItem);
//            }
//        }
//        deleteCart(sessionCart);
//        return userCart;
//    }
//}
