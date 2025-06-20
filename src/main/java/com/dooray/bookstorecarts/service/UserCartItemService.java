package com.dooray.bookstorecarts.service;

import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.entity.CartItem;
import com.dooray.bookstorecarts.exception.CartItemNotFoundException;
import com.dooray.bookstorecarts.exception.CartNotFoundException;
import com.dooray.bookstorecarts.exception.InvalidException;
import com.dooray.bookstorecarts.redisdto.RedisCartDto;
import com.dooray.bookstorecarts.redisdto.RedisCartItemDto;
import com.dooray.bookstorecarts.repository.UserCartItemRepository;
import com.dooray.bookstorecarts.repository.UserCartRedisRepository;
import com.dooray.bookstorecarts.repository.UserCartRepository;
import com.dooray.bookstorecarts.request.CartItemRequest;
import com.dooray.bookstorecarts.response.UserCartItemResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public UserCartItemResponse addUserCartItem(Long userId, CartItemRequest request) {
        Cart cart = userCartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    Cart savedCart = userCartRepository.save(newCart);
                    userCartRedisRepository.save(RedisCartDto.from(savedCart, Collections.emptyList()));
                    return savedCart;
                });

        // 같은 책이 이미 있으면 요청한 수량으로 새로 설정(예스 24가 그럼)
        CartItem existingItem = userCartItemRepository.findByCartAndBookId(cart, request.getBookId());
        if (existingItem != null) {
            existingItem.setQuantity(request.getQuantity());
            CartItem savedItem = userCartItemRepository.save(existingItem);

            List<CartItem> updatedItems = userCartItemRepository.findByCart(cart);
            userCartRedisRepository.save(RedisCartDto.from(cart, updatedItems));

            return new UserCartItemResponse(savedItem);
        }

        // 새로운 책이면 카트 아이템 추가
        CartItem newCartItem = new CartItem();
        newCartItem.setBookId(request.getBookId());
        newCartItem.setQuantity(request.getQuantity());
        newCartItem.setCart(cart);
        CartItem savedItem = userCartItemRepository.save(newCartItem);

        List<CartItem> updatedItems = userCartItemRepository.findByCart(cart);
        userCartRedisRepository.save(RedisCartDto.from(cart, updatedItems));

        return new UserCartItemResponse(savedItem);
    }


    public UserCartItemResponse getCartItemByCartItemId(Long userId, Long cartItemId) {
        RedisCartDto redisCart = userCartRedisRepository.findByUserId(userId);

        if (redisCart != null) {
            for (RedisCartItemDto itemDto : redisCart.getItems()) {
                if (itemDto.getCartItemId().equals(cartItemId)) {
                    CartItem cartItem = new CartItem();
                    cartItem.setId(itemDto.getCartItemId());
                    cartItem.setBookId(itemDto.getBookId());
                    cartItem.setQuantity(itemDto.getQuantity());
                    return new UserCartItemResponse(cartItem);
                }
            }
        }

        CartItem cartItem = userCartItemRepository.findById(cartItemId)
                .orElseThrow(() -> CartItemNotFoundException.forCartItemId(cartItemId));

        Cart cart = cartItem.getCart();
        List<CartItem> updatedItems = userCartItemRepository.findByCart(cart);
        userCartRedisRepository.save(RedisCartDto.from(cart, updatedItems));

        return new UserCartItemResponse(cartItem);
    }

    public List<CartItem> getCartItemsByUserId(Long userId){
        RedisCartDto redisCart = userCartRedisRepository.findByUserId(userId);
        if (redisCart != null) {
            return redisCart.getItems().stream()
                    .map(dto -> {
                        CartItem item = new CartItem();
                        item.setId(dto.getCartItemId());
                        item.setBookId(dto.getBookId());
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

    @Transactional
    public UserCartItemResponse updateQuantity(Long cartItemId, CartItemRequest request) {
        CartItem cartItem = userCartItemRepository.findById(cartItemId)
                .orElseThrow(() -> CartItemNotFoundException.forCartItemId(cartItemId));

        if (!cartItem.getBookId().equals(request.getBookId())) {
            throw new InvalidException("요청한 bookId와 해당 cartItem 의 bookId가 일치하지 않습니다.");
        }

        cartItem.setQuantity(request.getQuantity());
        CartItem updatedCartItem = userCartItemRepository.save(cartItem);

        Cart cart = cartItem.getCart();
        List<CartItem> updatedItems = userCartItemRepository.findByCart(cart);

        userCartRedisRepository.save(RedisCartDto.from(cart, updatedItems));
        return new UserCartItemResponse(updatedCartItem);
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
}
