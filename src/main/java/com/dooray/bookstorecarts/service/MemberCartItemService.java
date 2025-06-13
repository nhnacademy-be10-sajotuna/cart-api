package com.dooray.bookstorecarts.service;

import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;
import com.dooray.bookstorecarts.exception.CartItemNotFoundException;
import com.dooray.bookstorecarts.exception.CartNotFoundException;
import com.dooray.bookstorecarts.exception.InvalidException;
import com.dooray.bookstorecarts.repository.MemberCartItemRepository;
import com.dooray.bookstorecarts.repository.MemberCartRepository;
import com.dooray.bookstorecarts.request.CartItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberCartItemService {
    private final MemberCartRepository memberCartRepository;
    private final MemberCartItemRepository memberCartItemRepository;

    public CartItem createMemberCartItem(Long cartId, CartItemRequest request) {
        Cart cart = memberCartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException(cartId));
// 예를 들어 유저 임동혁의 장바구니에 같은종류의 책이있으면 수량증가만 시키고 아니면 새로운 카트아이템 객체 생성
        CartItem existingItem = memberCartItemRepository.findByCartAndBookId(cart, request.getBookId());
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            return memberCartItemRepository.save(existingItem);
        }

        CartItem newCartItem = new CartItem();
        newCartItem.setBookId(request.getBookId());
        newCartItem.setQuantity(request.getQuantity());
        newCartItem.setCart(cart);
        return memberCartItemRepository.save(newCartItem);
    }

    public CartItem getCartItemByCartItemId(Long cartItemId) {
        return memberCartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException(cartItemId));
    }

    public List<CartItem> getCartItemsByCartId(Long cartId){
        Cart cart = memberCartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException(cartId));
        return memberCartItemRepository.findByCart(cart);
    }

    public CartItem updateQuantity(Long cartItemId, CartItemRequest request) {
        CartItem cartItem = memberCartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException(cartItemId));

        if (!cartItem.getBookId().equals(request.getBookId())) {
            throw new InvalidException("요청한 bookId와 해당 cartItem 의 bookId가 일치하지 않습니다.");
        }

        if (request.getQuantity() <= 0) throw new InvalidException("수량은 0보다 커야합니다.");
        cartItem.setQuantity(request.getQuantity());
        return memberCartItemRepository.save(cartItem);
    }

    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = memberCartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException(cartItemId));
        memberCartItemRepository.delete(cartItem);
    }

    // 카트에 있는 모든 카트 아이템 삭제(장바구니 비우기)
    public void deleteAllCartItemsFromCart(Long cartId) {
        List<CartItem> items = getCartItemsByCartId(cartId);
        memberCartItemRepository.deleteAll(items);
    }
}
