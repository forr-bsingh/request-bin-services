package com.github.requestbin.grpc.services.impl;

import com.github.requestbin.grpc.services.GRPCPingServiceGrpc.GRPCPingServiceImplBase;
import com.github.requestbin.grpc.services.PingRequest;
import com.github.requestbin.grpc.services.PingResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

@GrpcService
public class GRPCPingServiceImpl extends GRPCPingServiceImplBase {

    private final ValueOperations<String, String> valueOps;

    public GRPCPingServiceImpl(RedisTemplate<String, String> redisTemplate) {
        valueOps = redisTemplate.opsForValue();
    }

    @Override
    public void ping(
            PingRequest request, StreamObserver<PingResponse> responseObserver) {
        if (StringUtils.isBlank(valueOps.get("PING"))) {
            valueOps.set("PING", "PONG");
            valueOps.getOperations().expire("PING", Duration.ofMinutes(30));
        }

        PingResponse response = PingResponse.newBuilder()
                .setMessage(valueOps.get("PING"))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
