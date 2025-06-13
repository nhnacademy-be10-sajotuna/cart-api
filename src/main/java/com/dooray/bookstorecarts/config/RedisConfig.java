package com.dooray.bookstorecarts.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public ObjectMapper redisMapper() {
        return new ObjectMapper();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory, ObjectMapper redisMapper) {
        RedisTemplate<String, Object> sessionRedisTemplate = new RedisTemplate<>();
        sessionRedisTemplate.setConnectionFactory(redisConnectionFactory);
        sessionRedisTemplate.setKeySerializer(new StringRedisSerializer());
        sessionRedisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(redisMapper));
        sessionRedisTemplate.setHashKeySerializer(new StringRedisSerializer());
        sessionRedisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(redisMapper));

        return sessionRedisTemplate;
    }
}
