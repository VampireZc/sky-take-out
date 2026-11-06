package com.sky;

import com.sky.anno.AutoFill;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;

// @SpringBootTest
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void test() { // 在java中操作redis
        ValueOperations value = redisTemplate.opsForValue();
        value.set("city", "ckj");
        System.out.println(value.get("city"));
    }
}
