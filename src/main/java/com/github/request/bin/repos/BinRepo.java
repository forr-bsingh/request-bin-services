package com.github.request.bin.repos;

import com.github.request.bin.data.Bin;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

@Repository
public class BinRepo {

    private static final UnaryOperator<String> GET_BIN_URL = name -> "/bins/" + name;
    private static final String ID_FIELD = "id";

    private final BoundHashOperations<String, String, Map<String, Object>> hashOperations;

    public BinRepo(RedisTemplate<String, String> redisTemplate) {
        hashOperations = redisTemplate.boundHashOps("REQUEST_BINS");
    }

    public boolean exists(String name) {
        return null != hashOperations.hasKey(name) && hashOperations.hasKey(name);
    }

    public Bin create(String name, Map<String, Object> schema) {
        if (!schema.isEmpty() && !schema.containsKey(ID_FIELD)) {
            schema.put(ID_FIELD, "{{Internet.uuid()}}");
        }
        hashOperations.put(name, schema);
        return new Bin(name, GET_BIN_URL.apply(name), schema);
    }

    public Bin get(String name) {
        Map<String, Object> schema = hashOperations.get(name);
        return new Bin(name, GET_BIN_URL.apply(name), schema);
    }

    public void delete(String name) {
        hashOperations.delete(name);
    }

    public Set<String> keys() {
        return hashOperations.keys();
    }

    public List<Map<String, Object>> values() {
        return hashOperations.values();
    }

    public long count() {
        return hashOperations.size();
    }
}
