package com.dooray.bookstorecarts.config;

import com.dooray.bookstorecarts.redisdto.RedisCartDto;
import com.dooray.bookstorecarts.redisdto.RedisGuestCartDto;
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
    public RedisTemplate<String, RedisCartDto> userCartRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, RedisCartDto> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        Jackson2JsonRedisSerializer<RedisCartDto> serializer = new Jackson2JsonRedisSerializer<>(RedisCartDto.class);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisTemplate<String, RedisGuestCartDto> guestCartRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, RedisGuestCartDto> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        Jackson2JsonRedisSerializer<RedisGuestCartDto> serializer = new Jackson2JsonRedisSerializer<>(RedisGuestCartDto.class);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}

