package com.dooray.bookstorecarts.service;

import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;
import com.dooray.bookstorecarts.exception.CartItemNotFoundException;
import com.dooray.bookstorecarts.exception.CartNotFoundException;
import com.dooray.bookstorecarts.exception.InvalidException;
import com.dooray.bookstorecarts.redisdto.RedisCartDto;
import com.dooray.bookstorecarts.repository.UserCartItemRepository;
import com.dooray.bookstorecarts.repository.UserCartRedisRepository;
import com.dooray.bookstorecarts.repository.UserCartRepository;
import com.dooray.bookstorecarts.request.CartItemRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserCartItemService {
    private final UserCartRepository userCartRepository;
    private final UserCartItemRepository userCartItemRepository;
    private final UserCartRedisRepository userCartRedisRepository;

    @Transactional
    public void addUserCartItem(Long userId, CartItemRequest request) {
        Cart cart = userCartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    Cart savedCart = userCartRepository.save(newCart);
                    userCartRedisRepository.save(RedisCartDto.from(savedCart, Collections.emptyList()));
                    return savedCart;
                });

        // 같은 책이 이미 있으면 요청한 수량으로 새로 설정(예스 24가 그럼)
        CartItem existingItem = userCartItemRepository.findByCartAndIsbn(cart, request.getIsbn());
        if (existingItem != null) {
            // 이미 있으면 수량만 변경
            existingItem.setQuantity(request.getQuantity());
            userCartItemRepository.save(existingItem);
        } else {
            // 없으면 새로 추가
            CartItem newCartItem = new CartItem();
            newCartItem.setIsbn(request.getIsbn());
            newCartItem.setQuantity(request.getQuantity());
            newCartItem.setCart(cart);
            userCartItemRepository.save(newCartItem);
        }

        // Redis 업데이트는 무조건 한 번만
        List<CartItem> updatedItems = userCartItemRepository.findByCart(cart);
        userCartRedisRepository.save(RedisCartDto.from(cart, updatedItems));
    }

    @Transactional
    public void updateQuantity(Long cartItemId, CartItemRequest request) {
        CartItem cartItem = userCartItemRepository.findById(cartItemId)
                .orElseThrow(() -> CartItemNotFoundException.forCartItemId(cartItemId));

        if (!cartItem.getIsbn().equals(request.getIsbn())) {
            throw new InvalidException("요청한 isbn과 해당 cartItem 의 isbn이 일치하지 않습니다.");
        }

        cartItem.setQuantity(request.getQuantity());
        userCartItemRepository.save(cartItem);

        Cart cart = cartItem.getCart();
        List<CartItem> updatedItems = userCartItemRepository.findByCart(cart);

        userCartRedisRepository.save(RedisCartDto.from(cart, updatedItems));
    }

    @Transactional
    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = userCartItemRepository.findById(cartItemId)
                .orElseThrow(() -> CartItemNotFoundException.forCartItemId(cartItemId));
        Cart cart = cartItem.getCart();
        userCartItemRepository.delete(cartItem);

        List<CartItem> updatedItems = userCartItemRepository.findByCart(cart);
        userCartRedisRepository.save(RedisCartDto.from(cart, updatedItems));
    }

    // 카트에 있는 모든 카트 아이템 삭제(장바구니 비우기)
    @Transactional
    public void deleteAllCartItemsFromUserId(Long userId) {
        List<CartItem> items = getCartItemsByUserId(userId);
        userCartItemRepository.deleteAll(items);

        Cart cart = userCartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));

        userCartRedisRepository.save(RedisCartDto.from(cart, Collections.emptyList()));
    }

    // 장바구니 아이템 조회 ( 장바구니 비우기 기능 쓸때 필요 )
    public List<CartItem> getCartItemsByUserId(Long userId){
        RedisCartDto redisCart = userCartRedisRepository.findByUserId(userId);
        if (redisCart != null) {
            return redisCart.getItems().stream()
                    .map(dto -> {
                        CartItem item = new CartItem();
                        item.setId(dto.getCartItemId());
                        item.setIsbn(dto.getIsbn());
                        item.setQuantity(dto.getQuantity());
                        return item;
                    })
                    .collect(Collectors.toList());
        }
        Cart cart = userCartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));
        List<CartItem> items = userCartItemRepository.findByCart(cart);
        userCartRedisRepository.save(RedisCartDto.from(cart, items));
        return items;
    }
}
