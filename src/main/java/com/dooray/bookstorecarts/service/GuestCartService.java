package com.dooray.bookstorecarts.service;

import com.dooray.bookstorecarts.exception.CartNotFoundException;
import com.dooray.bookstorecarts.redisdto.RedisGuestCartDto;
import com.dooray.bookstorecarts.repository.GuestCartRedisRepository;
import com.dooray.bookstorecarts.response.CartResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class GuestCartService {

    private final GuestCartRedisRepository guestCartRedisRepository;
    private final CartResponseService cartResponseService;

    public CartResponse getCartByCartId(String cartId) {
        RedisGuestCartDto guestCart = guestCartRedisRepository.findByCartId(cartId);

        if (guestCart == null) {
            guestCart = new RedisGuestCartDto(cartId, new ArrayList<>());
        }

        return cartResponseService.createFromGuestCart(guestCart);
    }

    public void deleteGuestCart(String cartId) {
        RedisGuestCartDto guestCart = guestCartRedisRepository.findByCartId(cartId);

        if (guestCart == null) {
            throw new CartNotFoundException(cartId);
        }
        guestCartRedisRepository.deleteByCartId(cartId);
    }
}
