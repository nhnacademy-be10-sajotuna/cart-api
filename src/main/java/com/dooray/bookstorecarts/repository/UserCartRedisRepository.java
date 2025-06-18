package com.dooray.bookstorecarts.repository;

import com.dooray.bookstorecarts.entity.Cart;
import com.dooray.bookstorecarts.redisdto.RedisCartDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserCartRedisRepository {
    private final RedisTemplate<String, RedisCartDto> redisTemplate;
    private static final String USER_CART_KEY = "user_cart:";

    public void save(RedisCartDto cart) {
        String key = USER_CART_KEY + cart.getUserId();
        redisTemplate.opsForValue().set(key, cart);
    }

    public RedisCartDto findByUserId(Long userId) {
        String key = USER_CART_KEY + userId;
        return redisTemplate.opsForValue().get(key);
    }

    public boolean existsByUserId(Long userId) {
        String key = USER_CART_KEY + userId;
        return redisTemplate.hasKey(key);
    }

    public void deleteByUserId(Long userId) {
        redisTemplate.delete(USER_CART_KEY + userId);
    }
}
