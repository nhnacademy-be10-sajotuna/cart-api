package com.dooray.bookstorecarts.repository;

import com.dooray.bookstorecarts.redisdto.GuestCart;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class GuestCartRepository {
    private final RedisTemplate<String,Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String GUEST_CART_KEY = "guest-cart:";

    public GuestCart save(GuestCart guestCart) throws JsonProcessingException {
        String key = GUEST_CART_KEY + guestCart.getSessionId();
        String value = objectMapper.writeValueAsString(guestCart);
        redisTemplate.opsForValue().set(key, value, Duration.ofDays(1));
        return guestCart;
    }

    public GuestCart findBySessionId(String sessionId) throws JsonProcessingException {
        String key = GUEST_CART_KEY + sessionId;
        String json = (String) redisTemplate.opsForValue().get(key);
        if (json == null) return null;
        return objectMapper.readValue(json, GuestCart.class);
    }

    public boolean existsBySessionId(String sessionId) {
        String key = GUEST_CART_KEY + sessionId;
        return redisTemplate.hasKey(key);
    }

    public void delete(String sessionId) {
        redisTemplate.delete(GUEST_CART_KEY + sessionId);
    }
}
