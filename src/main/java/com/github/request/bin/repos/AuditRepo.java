package com.github.request.bin.repos;

import com.github.request.bin.data.Ops;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;

@Repository
public class AuditRepo {

    private static final UnaryOperator<String> AUDIT_BIN = bin -> "AUDIT-" + bin;
    private final HashOperations<String, String, Ops> auditHash;

    public AuditRepo(RedisTemplate<String, String> redisTemplate) {
        this.auditHash = redisTemplate.opsForHash();
    }

    @Value("${bin.default.redis.time-to-live}")
    private int binTTL;

    public Ops get(String bin, String identifier) {
        return auditHash.get(AUDIT_BIN.apply(bin), identifier);
    }

    public Ops create(String bin, String operation, Object request, Object response, Object headers) {
        String id = UUID.randomUUID().toString();
        Ops ops = new Ops(id, operation, request, response,
                headers, LocalDateTime.now(Clock.systemUTC()).format(DateTimeFormatter.ISO_DATE_TIME));
        auditHash.put(AUDIT_BIN.apply(bin), id, ops);
        expire(bin, Duration.ofMillis(binTTL));
        return get(bin, id);
    }

    public Ops update(String bin, String identifier, Ops ops) {
        auditHash.put(AUDIT_BIN.apply(bin), identifier, ops);
        return get(bin, identifier);
    }

    public List<Ops> values(String bin) {
        return auditHash.values(AUDIT_BIN.apply(bin));
    }

    public long count(String bin) {
        return auditHash.size(AUDIT_BIN.apply(bin));
    }

    public void drop(String bin) {
        auditHash.getOperations().delete(AUDIT_BIN.apply(bin));
    }

    public void expire(String bin, Duration ttl) {
        auditHash.getOperations().expire(AUDIT_BIN.apply(bin), ttl);
    }
}
