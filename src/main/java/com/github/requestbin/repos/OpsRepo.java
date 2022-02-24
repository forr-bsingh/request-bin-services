package com.github.requestbin.repos;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.UnaryOperator;

@Repository
public class OpsRepo {

    private static final UnaryOperator<String> OPS_BIN = bin -> "OPS-" + bin;

    private final HashOperations<String, String, Map<String, Object>> opsHash;

    public OpsRepo(RedisTemplate<String, String> redisTemplate) {
        this.opsHash = redisTemplate.opsForHash();
    }

    @Value("${bin.default.redis.time-to-live}")
    private int binTTL;

    public boolean exists(String bin, String identifier) {
        return null != opsHash.hasKey(OPS_BIN.apply(bin), identifier)
                && opsHash.hasKey(OPS_BIN.apply(bin), identifier)
                && null != opsHash.get(OPS_BIN.apply(bin), identifier);
    }

    public Map<String, Object> create(String bin, Map<String, Object> payload) {
        String id = UUID.randomUUID().toString();
        if (!payload.containsKey("id")) {
            payload.put("id", id);
        } else {
            id = (String) payload.get("id");
        }
        opsHash.put(OPS_BIN.apply(bin), id, payload);
        expire(bin, Duration.ofMillis(binTTL));
        return payload;
    }

    public Map<String, Object> get(String bin, String identifier) {
        return opsHash.get(OPS_BIN.apply(bin), identifier);
    }

    public long count(String bin) {
        return opsHash.size(OPS_BIN.apply(bin));
    }

    public List<Map<String, Object>> values(String bin) {
        return opsHash.values(OPS_BIN.apply(bin));
    }

    public void delete(String bin, String identifier) {
        opsHash.delete(OPS_BIN.apply(bin), identifier);
    }

    public Map<String, Object> update(String bin, String identifier, Map<String, Object> payload) {
        opsHash.put(OPS_BIN.apply(bin), identifier, payload);
        return get(bin, identifier);
    }

    public void drop(String bin) {
        opsHash.getOperations().delete(OPS_BIN.apply(bin));
    }

    public void expire(String bin, Duration ttl) {
        opsHash.getOperations().expire(OPS_BIN.apply(bin), ttl);
    }
}
