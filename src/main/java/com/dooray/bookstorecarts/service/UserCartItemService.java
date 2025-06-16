package com.dooray.bookstorecarts.service;

import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;
import com.dooray.bookstorecarts.exception.CartItemNotFoundException;
import com.dooray.bookstorecarts.exception.CartNotFoundException;
import com.dooray.bookstorecarts.exception.InvalidException;
import com.dooray.bookstorecarts.repository.UserCartItemRepository;
import com.dooray.bookstorecarts.repository.UserCartRepository;
import com.dooray.bookstorecarts.request.CartItemRequest;
import com.dooray.bookstorecarts.response.UserCartItemResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserCartItemService {
    private final UserCartRepository userCartRepository;
    private final UserCartItemRepository userCartItemRepository;
    @Transactional
    public UserCartItemResponse createUserCartItem(Long userId, CartItemRequest request) {
        Cart cart = userCartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return userCartRepository.save(newCart);
                });
// 예를 들어 유저 임동혁의 장바구니에 같은종류의 책이있으면 수량증가만 시키고 아니면 새로운 카트아이템 객체 생성
        CartItem existingItem = userCartItemRepository.findByCartAndBookId(cart, request.getBookId());
        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            return new UserCartItemResponse(userCartItemRepository.save(existingItem));
        }

        CartItem newCartItem = new CartItem();
        newCartItem.setBookId(request.getBookId());
        newCartItem.setQuantity(request.getQuantity());
        newCartItem.setCart(cart);
        return new UserCartItemResponse(userCartItemRepository.save(newCartItem));
    }

    public UserCartItemResponse getCartItemByCartItemId(Long cartItemId) {
        CartItem cartItem = userCartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException(cartItemId));
        return new UserCartItemResponse(cartItem);
    }

    public List<CartItem> getCartItemsByCartId(Long cartId){
        Cart cart = userCartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException(cartId));
        return userCartItemRepository.findByCart(cart);
    }

    public List<CartItem> getCartItemsByUserId(Long userId){
        Cart cart = userCartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));
        return userCartItemRepository.findByCart(cart);
    }
    @Transactional
    public UserCartItemResponse updateQuantity(Long cartItemId, CartItemRequest request) {
        CartItem cartItem = userCartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException(cartItemId));

        if (!cartItem.getBookId().equals(request.getBookId())) {
            throw new InvalidException("요청한 bookId와 해당 cartItem 의 bookId가 일치하지 않습니다.");
        }

        if (request.getQuantity() <= 0) throw new InvalidException("수량은 0보다 커야합니다.");
        cartItem.setQuantity(request.getQuantity());
        CartItem updatedCartItem = userCartItemRepository.save(cartItem);
        return new UserCartItemResponse(updatedCartItem);
    }
    @Transactional
    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = userCartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new CartItemNotFoundException(cartItemId));
        userCartItemRepository.delete(cartItem);
    }

    // 카트에 있는 모든 카트 아이템 삭제(장바구니 비우기)
    @Transactional
    public void deleteAllCartItemsFromCartId(Long cartId) {
        List<CartItem> items = getCartItemsByCartId(cartId);
        userCartItemRepository.deleteAll(items);
    }

    @Transactional
    public void deleteAllCartItemsFromUserId(Long userId) {
        List<CartItem> items = getCartItemsByUserId(userId);
        userCartItemRepository.deleteAll(items);
    }
}
