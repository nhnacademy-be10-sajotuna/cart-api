package com.dooray.bookstorecarts.config;

import com.dooray.bookstorecarts.redisdto.GuestCart;
import com.dooray.bookstorecarts.redisdto.RedisCartDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public ObjectMapper redisMapper() {
        return new ObjectMapper();
    }

    @Bean
    public RedisTemplate<String, RedisCartDto> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, RedisCartDto> sessionRedisTemplate = new RedisTemplate<>();
        sessionRedisTemplate.setConnectionFactory(redisConnectionFactory);

        Jackson2JsonRedisSerializer<RedisCartDto> serializer = new Jackson2JsonRedisSerializer<>(RedisCartDto.class);
        sessionRedisTemplate.setKeySerializer(new StringRedisSerializer());
        sessionRedisTemplate.setValueSerializer(serializer);
        sessionRedisTemplate.setHashKeySerializer(new StringRedisSerializer());
        sessionRedisTemplate.setHashValueSerializer(serializer);

        sessionRedisTemplate.afterPropertiesSet();

        return sessionRedisTemplate;
    }
}
