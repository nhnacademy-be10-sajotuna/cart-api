package com.dooray.bookstorecarts.service;

import com.dooray.bookstorecarts.exception.CartItemNotFoundException;
import com.dooray.bookstorecarts.exception.CartNotFoundException;
import com.dooray.bookstorecarts.redisdto.RedisGuestCartDto;
import com.dooray.bookstorecarts.redisdto.RedisGuestCartItemDto;
import com.dooray.bookstorecarts.repository.GuestCartRedisRepository;
import com.dooray.bookstorecarts.request.CartItemRequest;
import com.dooray.bookstorecarts.response.CartItemResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class GuestCartItemService {
    private final GuestCartRedisRepository guestCartRedisRepository;

    @Transactional
    public CartItemResponse addGuestCartItem(String cartId, CartItemRequest request) {
        RedisGuestCartDto guestCart = guestCartRedisRepository.findByCartId(cartId);
        if (guestCart == null) {
            guestCart = new RedisGuestCartDto(cartId, new ArrayList<>());
        }

        for (RedisGuestCartItemDto existingItem : guestCart.getItems()) {
            if (existingItem.getBookId().equals(request.getBookId())) {
                existingItem.setQuantity(request.getQuantity());
                guestCartRedisRepository.save(guestCart);
                return new CartItemResponse(existingItem);
            }
        }

        RedisGuestCartItemDto newItem = new RedisGuestCartItemDto();
        newItem.setBookId(request.getBookId());
        newItem.setQuantity(request.getQuantity());
        guestCart.getItems().add(newItem);

        guestCartRedisRepository.save(guestCart);
        return new CartItemResponse(newItem);
    }

    // 회원은 카트아이템(기본키, 오토인크리즈먼트키)로 식별되는데 비회원은 기본키없어서 cart Id와 book id가 둘다 있어야 식별가능
    public CartItemResponse getGuestCartItemByBookId(String CartId, Long BookId){
        RedisGuestCartDto guestCart = guestCartRedisRepository.findByCartId(CartId);
        if (guestCart == null) {
            throw new CartNotFoundException(CartId);
        }
        for(RedisGuestCartItemDto item : guestCart.getItems()){
            if(item.getBookId().equals(BookId)){
                return new CartItemResponse(item);
            }
        }
        throw CartItemNotFoundException.forBookId(BookId);
    }

    @Transactional
    public CartItemResponse updateQuantity(String cartId, CartItemRequest request) {
        RedisGuestCartDto guestCart = guestCartRedisRepository.findByCartId(cartId);
        if (guestCart == null) {
            throw new CartNotFoundException(cartId);
        }

        RedisGuestCartItemDto guestCartItem = null;
        for (RedisGuestCartItemDto item : guestCart.getItems()) {
            if (item.getBookId().equals(request.getBookId())) {
                guestCartItem = item;
                break;
            }
        }

        if (guestCartItem == null) throw  CartItemNotFoundException.forBookId(request.getBookId());
        guestCartItem.setQuantity(request.getQuantity());
        guestCartRedisRepository.save(guestCart);

        return new CartItemResponse(guestCartItem);
    }

    @Transactional
    public void deleteGuestCartItem(String cartId, Long bookId) {
        RedisGuestCartDto guestCart = guestCartRedisRepository.findByCartId(cartId);
        if (guestCart == null) {
            throw new CartNotFoundException(cartId);
        }

        boolean removed = guestCart.getItems().removeIf(item -> item.getBookId().equals(bookId));

        if (!removed) {
            throw CartItemNotFoundException.forBookId(bookId);
        }

        guestCartRedisRepository.save(guestCart);
    }

    @Transactional
    public void deleteAllGuestCartItems(String cartId) {
        RedisGuestCartDto guestCart = guestCartRedisRepository.findByCartId(cartId);
        guestCart.getItems().clear();
        guestCartRedisRepository.save(guestCart);
    }
}
