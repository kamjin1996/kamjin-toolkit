package com.kamjin.toolkit.cache.redis.config;

import com.kamjin.toolkit.cache.redis.client.RedisClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;


@Configuration
@Order(0)
public class RedisConfig {


    @Resource
    private RedisConnectionFactory factory;


    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        //redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        //redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericToStringSerializer(String.class));
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(factory);
        return redisTemplate;
    }

    @Bean
    public HashOperations<String, String, String> hashOperations(RedisTemplate<String, String> redisTemplate) {
        return redisTemplate.opsForHash();
    }

    @Bean
    public ValueOperations<String, Object> valueOperatons(RedisTemplate<String, Object> redisTemplate) {
        return redisTemplate.opsForValue();
    }

    @Bean
    public ListOperations<String, String> listOperations(RedisTemplate<String, String> redisTemplate) {
        return redisTemplate.opsForList();
    }

    @Bean
    public SetOperations<String, String> setOperations(RedisTemplate<String, String> redisTemplate) {
        return redisTemplate.opsForSet();
    }

    @Bean
    public ZSetOperations<String, String> zSetOperations(RedisTemplate<String, String> redisTemplate) {
        return redisTemplate.opsForZSet();
    }

    @Bean
    public RedisClient redisClient(ValueOperations<String, Object> valueOperations, HashOperations<String, String, String> hashOperations, SetOperations<String, String> setOperations, RedisTemplate redisTemplate, ListOperations<String, String> listOperations, ZSetOperations<String, String> zSetOperations) {
        return new RedisClient(valueOperations, hashOperations, setOperations, redisTemplate, listOperations,zSetOperations);
    }
}


