package com.dooray.bookstorecarts.repository;

import com.dooray.bookstorecarts.redisdto.GuestCart;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GuestCartRepository {
    private final RedisTemplate<String,GuestCart> redisTemplate;
    private static final String GUEST_CART_KEY = "guest-cart:";

    public GuestCart save(GuestCart guestCart) {
        String key = GUEST_CART_KEY + guestCart.getSessionId();
        redisTemplate.opsForValue().set(key, guestCart, Duration.ofDays(1));
        return guestCart;
    }

    public Optional<GuestCart> findBySessionId(String sessionId) {
        String key = GUEST_CART_KEY + sessionId;
        GuestCart guestCart = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(guestCart);
    }

    public boolean existsBySessionId(String sessionId) {
        String key = GUEST_CART_KEY + sessionId;
        return redisTemplate.hasKey(key);
    }

    public void delete(String sessionId) {
        redisTemplate.delete(GUEST_CART_KEY + sessionId);
    }
}
