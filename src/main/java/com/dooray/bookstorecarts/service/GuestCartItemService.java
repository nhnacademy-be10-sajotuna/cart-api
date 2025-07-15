package com.dooray.bookstorecarts.service;

import com.dooray.bookstorecarts.exception.CartItemNotFoundException;
import com.dooray.bookstorecarts.exception.CartNotFoundException;
import com.dooray.bookstorecarts.feign.BookFeignClient;
import com.dooray.bookstorecarts.feign.BookResponse;
import com.dooray.bookstorecarts.redisdto.RedisGuestCartDto;
import com.dooray.bookstorecarts.redisdto.RedisGuestCartItemDto;
import com.dooray.bookstorecarts.repository.GuestCartRedisRepository;
import com.dooray.bookstorecarts.request.CartItemRequest;
import com.dooray.bookstorecarts.response.CartItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class GuestCartItemService {
    private final GuestCartRedisRepository guestCartRedisRepository;
    private final BookFeignClient bookFeignClient;

    public void addGuestCartItem(String cartId, CartItemRequest request) {
        RedisGuestCartDto guestCart = guestCartRedisRepository.findByCartId(cartId);
        if (guestCart == null) {
            guestCart = new RedisGuestCartDto(cartId, new ArrayList<>());
            guestCartRedisRepository.save(guestCart);
        }

        for (RedisGuestCartItemDto existingItem : guestCart.getItems()) {
            if (existingItem.getIsbn().equals(request.getIsbn())) {
                existingItem.setQuantity(request.getQuantity());
                guestCartRedisRepository.save(guestCart);
                return;
            }
        }

        RedisGuestCartItemDto newItem = new RedisGuestCartItemDto();
        newItem.setIsbn(request.getIsbn());
        newItem.setQuantity(request.getQuantity());
        guestCart.getItems().add(newItem);

        guestCartRedisRepository.save(guestCart);
    }

    // 회원은 카트아이템(기본키, 오토인크리즈먼트키)로 식별되는데 비회원은 기본키없어서 cart Id와 book id가 둘다 있어야 식별가능
    public CartItemResponse getGuestCartItemByIsbn(String CartId, String isbn){
        RedisGuestCartDto guestCart = guestCartRedisRepository.findByCartId(CartId);
        if (guestCart == null) {
            throw new CartNotFoundException(CartId);
        }
        for(RedisGuestCartItemDto item : guestCart.getItems()){
            if(item.getIsbn().equals(isbn)){
                BookResponse book = bookFeignClient.getBook(item.getIsbn());
                return new CartItemResponse(item,book);
            }
        }
        throw CartItemNotFoundException.forIsbn(isbn);
    }


    public void updateQuantity(String cartId, CartItemRequest request) {
        RedisGuestCartDto guestCart = guestCartRedisRepository.findByCartId(cartId);
        if (guestCart == null) {
            throw new CartNotFoundException(cartId);
        }

        RedisGuestCartItemDto guestCartItem = null;
        for (RedisGuestCartItemDto item : guestCart.getItems()) {
            if (item.getIsbn().equals(request.getIsbn())) {
                guestCartItem = item;
                break;
            }
        }

        if (guestCartItem == null) throw  CartItemNotFoundException.forIsbn(request.getIsbn());
        guestCartItem.setQuantity(request.getQuantity());
        guestCartRedisRepository.save(guestCart);
    }

    public void deleteGuestCartItem(String cartId, String isbn) {
        RedisGuestCartDto guestCart = guestCartRedisRepository.findByCartId(cartId);
        if (guestCart == null) {
            throw new CartNotFoundException(cartId);
        }

        boolean removed = guestCart.getItems().removeIf(item -> item.getIsbn().equals(isbn));

        if (!removed) {
            throw CartItemNotFoundException.forIsbn(isbn);
        }

        guestCartRedisRepository.save(guestCart);
    }

    public void deleteAllGuestCartItems(String cartId) {
        RedisGuestCartDto guestCart = guestCartRedisRepository.findByCartId(cartId);
        guestCart.getItems().clear();
        guestCartRedisRepository.save(guestCart);
    }
}
