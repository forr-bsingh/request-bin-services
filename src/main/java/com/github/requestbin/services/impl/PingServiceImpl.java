package com.github.requestbin.services.impl;

import com.github.requestbin.services.PingService;
import com.github.requestbin.utils.LogThis;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class PingServiceImpl implements PingService {

    private final ValueOperations<String, String> valueOps;

    public PingServiceImpl(RedisTemplate<String, String> redisTemplate) {
        valueOps = redisTemplate.opsForValue();
    }

    @LogThis
    @Override
    public String ping() {
        if (StringUtils.isBlank(valueOps.get("PING"))) {
            valueOps.set("PING", "PONG");
            valueOps.getOperations().expire("PING", Duration.ofMinutes(30));
        }
        return valueOps.get("PING");
    }
}
