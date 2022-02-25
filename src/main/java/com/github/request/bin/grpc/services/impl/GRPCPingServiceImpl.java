package com.github.request.bin.grpc.services.impl;

import com.github.request.bin.grpc.services.GRPCPingServiceGrpc;
import com.github.request.bin.grpc.services.PingRequest;
import com.github.request.bin.grpc.services.PingResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

@GrpcService
public class GRPCPingServiceImpl extends GRPCPingServiceGrpc.GRPCPingServiceImplBase {

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
