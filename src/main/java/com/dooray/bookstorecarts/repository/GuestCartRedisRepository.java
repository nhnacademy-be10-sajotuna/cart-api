package com.dooray.bookstorecarts.repository;

import com.dooray.bookstorecarts.redisdto.RedisGuestCartDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository

public class GuestCartRedisRepository {
    private final RedisTemplate<String, RedisGuestCartDto> redisTemplate;
    private static final String GUEST_CART_KEY = "guest_cart:";

    public GuestCartRedisRepository(@Qualifier("guestCartRedisTemplate") RedisTemplate<String, RedisGuestCartDto> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(RedisGuestCartDto cart) {
        String key = GUEST_CART_KEY+ cart.getCartId();
        redisTemplate.opsForValue().set(key, cart);
        redisTemplate.expire(key, 1, TimeUnit.DAYS);
    }

    public RedisGuestCartDto findByCartId(String cartId) {
        String key = GUEST_CART_KEY + cartId;
        return redisTemplate.opsForValue().get(key);
    }

    public boolean existsByCartId(String cartId) {
        String key = GUEST_CART_KEY + cartId;
        return redisTemplate.hasKey(key);
    }

    public void deleteByCartId(String cartId) {
        redisTemplate.delete(GUEST_CART_KEY + cartId);
    }
}
