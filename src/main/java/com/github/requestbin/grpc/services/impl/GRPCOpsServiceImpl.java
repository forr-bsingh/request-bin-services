package com.github.requestbin.grpc.services.impl;

import com.github.requestbin.grpc.services.*;
import com.github.requestbin.repos.BinRepo;
import com.github.requestbin.repos.OpsRepo;
import com.github.requestbin.utils.GRPCUtils;
import com.github.requestbin.utils.LogThis;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@GrpcService
public class GRPCOpsServiceImpl extends GRPCOpsServiceGrpc.GRPCOpsServiceImplBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(GRPCOpsServiceImpl.class);

    private final BinRepo binRepo;

    private final OpsRepo opsRepo;

    public GRPCOpsServiceImpl(BinRepo binRepo, OpsRepo opsRepo) {
        this.binRepo = binRepo;
        this.opsRepo = opsRepo;
    }

    @LogThis
    @Override
    public void unaryGet(OpsGetRequest request, StreamObserver<OpsGetResponse> responseObserver) {
        try {
            if (StringUtils.isBlank(request.getBin()) || StringUtils.isBlank(request.getIdentifier())) {
                responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Missing information. Please provider bin and identifier.")
                        .asException());
            } else {
                if (binRepo.exists(request.getBin())) {
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info(String.format("Bin found by the name of: %s, Proceeding with Get operation.", request.getBin()));
                    }
                    if (opsRepo.exists(request.getBin(), request.getIdentifier())) {
                        Map<String, Object> payload = opsRepo.get(request.getBin(), request.getIdentifier());
                        responseObserver.onNext(OpsGetResponse.newBuilder().putAllPayload(GRPCUtils.mapToStruct(payload)).build());
                        responseObserver.onCompleted();
                    } else {
                        responseObserver.onError(Status.NOT_FOUND
                                .withDescription(String.format("Record not found for id: %s",
                                        request.getIdentifier())).asException());
                    }
                } else {
                    responseObserver.onError(Status.NOT_FOUND
                            .withDescription(String.format("Bin not found by the name of: %s, Create bin first.",
                                    request.getBin())).asException());
                }
            }
        } catch (Exception e) {
            responseObserver.onError(Status.UNKNOWN.withDescription("Exception with get operation.").withCause(e).asException());
        }
    }

    @LogThis
    @Override
    public void unaryPost(OpsPostRequest request, StreamObserver<OpsPostResponse> responseObserver) {
        try {
            if (StringUtils.isBlank(request.getBin()) || request.getPayloadMap().isEmpty()) {
                responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Missing information. Please provide bin and payload.")
                        .asException());
            } else {
                if (binRepo.exists(request.getBin())) {
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info(String.format("Bin found by the name of: %s, Proceeding with Post operation.", request.getBin()));
                    }
                    Map<String, Object> payload = opsRepo.create(request.getBin(), GRPCUtils.structToMap(request.getPayloadMap()));
                    responseObserver.onNext(OpsPostResponse.newBuilder().putAllPayload(GRPCUtils.mapToStruct(payload)).build());
                    responseObserver.onCompleted();
                } else {
                    responseObserver.onError(Status.NOT_FOUND
                            .withDescription(String.format("Bin not found by the name of: %s, Create bin first.",
                                    request.getBin())).asException());
                }
            }
        } catch (Exception e) {
            responseObserver.onError(Status.UNKNOWN.withDescription("Exception with post operation.").withCause(e).asException());
        }
    }

    @LogThis
    @Override
    public void unaryPut(OpsPutRequest request, StreamObserver<OpsPutResponse> responseObserver) {
        try {
            if (StringUtils.isBlank(request.getBin()) || StringUtils.isBlank(request.getIdentifier()) || request.getPayloadMap().isEmpty()) {
                responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Missing information. Please provide bin, identifier and payload.")
                        .asException());
            } else {
                if (binRepo.exists(request.getBin())) {
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info(String.format("Bin found by the name of: %s, Proceeding with Put operation.", request.getBin()));
                    }
                    if (opsRepo.exists(request.getBin(), request.getIdentifier())) {
                        Map<String, Object> payload = GRPCUtils.structToMap(request.getPayloadMap());
                        payload.put("id", request.getIdentifier());
                        responseObserver.onNext(OpsPutResponse.newBuilder().putAllPayload(GRPCUtils.mapToStruct(opsRepo.update(request.getBin(), request.getIdentifier(), payload))).build());
                        responseObserver.onCompleted();
                    } else {
                        responseObserver.onError(Status.NOT_FOUND
                                .withDescription(String.format("Record not found for id: %s",
                                        request.getIdentifier())).asException());
                    }
                } else {
                    responseObserver.onError(Status.NOT_FOUND
                            .withDescription(String.format("Bin not found by the name of: %s, Create bin first.",
                                    request.getBin())).asException());
                }
            }
        } catch (Exception e) {
            responseObserver.onError(Status.UNKNOWN.withDescription("Exception with put operation.").withCause(e).asException());
        }
    }

    @LogThis
    @Override
    public void unaryList(OpsListRequest request, StreamObserver<OpsListResponse> responseObserver) {
        try {
            if (StringUtils.isBlank(request.getBin())) {
                responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Missing information. Please provide bin.")
                        .asException());
            } else {
                int page = (request.getPage() == 0) ? 0 : request.getPage() - 1;
                int limit = (request.getLimit() == 0) ? 10 : request.getLimit();
                if (binRepo.exists(request.getBin())) {
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info(String.format("Bin found by the name of: %s, Proceeding with List operation.", request.getBin()));
                    }
                    long total = opsRepo.count(request.getBin());
                    List<Content> contents = opsRepo.values(request.getBin()).stream()
                            .filter(Objects::nonNull).map(GRPCUtils::mapToStruct)
                            .skip((long) page * limit).limit(limit)
                            .map(content -> Content.newBuilder().putAllPayload(content).build())
                            .collect(Collectors.toList());
                    OpsListResponse opsListResponse = OpsListResponse.newBuilder().addAllContents(contents)
                            .setCurrentPage(page + 1).setNumberOfElements(contents.size())
                            .setFirst(Boolean.toString(page == 0)).setLast(Boolean.toString(contents.size() == total)).setEmpty(Boolean.toString(contents.isEmpty()))
                            .setTotalElements(total).setTotalPages(Math.round(Math.ceil((double) total / (double) limit))).build();
                    responseObserver.onNext(opsListResponse);
                    responseObserver.onCompleted();
                } else {
                    responseObserver.onError(Status.NOT_FOUND
                            .withDescription(String.format("Bin not found by the name of: %s, Create bin first.",
                                    request.getBin())).asException());
                }
            }
        } catch (Exception e) {
            responseObserver.onError(Status.UNKNOWN.withDescription("Exception with list operation.").withCause(e).asException());
        }
    }

    @LogThis
    @Override
    public void unaryPatch(OpsPatchRequest request, StreamObserver<OpsPatchResponse> responseObserver) {
        try {
            if (StringUtils.isBlank(request.getBin()) || StringUtils.isBlank(request.getIdentifier()) || request.getPayloadMap().isEmpty()) {
                responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Missing information. Please provide bin, identifier and payload.")
                        .asException());
            } else {
                if (binRepo.exists(request.getBin())) {
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info(String.format("Bin found by the name of: %s, Proceeding with Patch operation.", request.getBin()));
                    }
                    if (opsRepo.exists(request.getBin(), request.getIdentifier())) {
                        Map<String, Object> existing = opsRepo.get(request.getBin(), request.getIdentifier());
                        if (existing != null) {
                            Map<String, Object> merged = Stream.concat(existing.entrySet().stream(), GRPCUtils.structToMap(request.getPayloadMap()).entrySet().stream())
                                    .collect(Collectors.toMap(
                                            Map.Entry::getKey,
                                            Map.Entry::getValue, (oldV, newV) -> newV));
                            responseObserver.onNext(OpsPatchResponse.newBuilder().putAllPayload(GRPCUtils.mapToStruct(opsRepo.update(request.getBin(), request.getIdentifier(), merged))).build());
                            responseObserver.onCompleted();
                        }
                    } else {
                        responseObserver.onError(Status.NOT_FOUND
                                .withDescription(String.format("Record not found for id: %s",
                                        request.getIdentifier())).asException());
                    }
                } else {
                    responseObserver.onError(Status.NOT_FOUND
                            .withDescription(String.format("Bin not found by the name of: %s, Create bin first.",
                                    request.getBin())).asException());
                }
            }
        } catch (Exception e) {
            responseObserver.onError(Status.UNKNOWN.withDescription("Exception with patch operation.").withCause(e).asException());
        }
    }

    @LogThis
    @Override
    public void unaryDelete(OpsDeleteRequest request, StreamObserver<OpsDeleteResponse> responseObserver) {
        try {
            if (StringUtils.isBlank(request.getBin()) || StringUtils.isBlank(request.getIdentifier())) {
                responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Missing information. Please provider bin and identifier.")
                        .asException());
            } else {
                if (binRepo.exists(request.getBin())) {
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info(String.format("Bin found by the name of: %s, Proceeding with Delete operation.", request.getBin()));
                    }
                    if (opsRepo.exists(request.getBin(), request.getIdentifier())) {
                        opsRepo.delete(request.getBin(), request.getIdentifier());
                        responseObserver.onNext(OpsDeleteResponse.newBuilder().setPayload(Empty.newBuilder().build()).build());
                        responseObserver.onCompleted();
                    } else {
                        responseObserver.onError(Status.NOT_FOUND
                                .withDescription(String.format("Record not found for id: %s",
                                        request.getIdentifier())).asException());
                    }
                } else {
                    responseObserver.onError(Status.NOT_FOUND
                            .withDescription(String.format("Bin not found by the name of: %s, Create bin first.",
                                    request.getBin())).asException());
                }
            }
        } catch (Exception e) {
            responseObserver.onError(Status.UNKNOWN.withDescription("Exception with delete operation.").withCause(e).asException());
        }
    }

    @LogThis
    @Override
    public StreamObserver<OpsGetRequest> streamGet(StreamObserver<OpsGetResponse> responseObserver) {
        return new StreamObserver<OpsGetRequest>() {
            @Override
            public void onNext(OpsGetRequest request) {
                if (StringUtils.isBlank(request.getBin()) || StringUtils.isBlank(request.getIdentifier())) {
                    onError(Status.INVALID_ARGUMENT.withDescription("Missing information. Please provider bin and identifier.")
                            .asException());
                } else {
                    if (binRepo.exists(request.getBin())) {
                        if (LOGGER.isInfoEnabled()) {
                            LOGGER.info(String.format("Bin found by the name of: %s, Proceeding with Get operation.", request.getBin()));
                        }
                        if (opsRepo.exists(request.getBin(), request.getIdentifier())) {
                            Map<String, Object> payload = opsRepo.get(request.getBin(), request.getIdentifier());
                            responseObserver.onNext(OpsGetResponse.newBuilder().putAllPayload(GRPCUtils.mapToStruct(payload)).build());
                        } else {
                            onError(Status.NOT_FOUND
                                    .withDescription(String.format("Record not found for id: %s",
                                            request.getIdentifier())).asException());
                        }
                    } else {
                        onError(Status.NOT_FOUND
                                .withDescription(String.format("Bin not found by the name of: %s, Create bin first.",
                                        request.getBin())).asException());
                    }
                }
            }

            @Override
            public void onError(Throwable throwable) {
                LOGGER.info("Exception while get operation", throwable);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    @LogThis
    @Override
    public StreamObserver<OpsPostRequest> streamPost(StreamObserver<OpsPostResponse> responseObserver) {
        return new StreamObserver<OpsPostRequest>() {
            @Override
            public void onNext(OpsPostRequest request) {
                try {
                    if (StringUtils.isBlank(request.getBin()) || request.getPayloadMap().isEmpty()) {
                        onError(Status.INVALID_ARGUMENT.withDescription("Missing information. Please provide bin and payload.")
                                .asException());
                    } else {
                        if (binRepo.exists(request.getBin())) {
                            if (LOGGER.isInfoEnabled()) {
                                LOGGER.info(String.format("Bin found by the name of: %s, Proceeding with Post operation.", request.getBin()));
                            }
                            Map<String, Object> payload = opsRepo.create(request.getBin(), GRPCUtils.structToMap(request.getPayloadMap()));
                            responseObserver.onNext(OpsPostResponse.newBuilder().putAllPayload(GRPCUtils.mapToStruct(payload)).build());
                        } else {
                            onError(Status.NOT_FOUND
                                    .withDescription(String.format("Bin not found by the name of: %s, Create bin first.",
                                            request.getBin())).asException());
                        }
                    }
                } catch (Exception e) {
                    onError(Status.UNKNOWN.withDescription("Exception with post operation.").withCause(e).asException());
                }
            }

            @Override
            public void onError(Throwable throwable) {
                LOGGER.info("Exception while post operation", throwable);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    @LogThis
    @Override
    public StreamObserver<OpsPutRequest> streamPut(StreamObserver<OpsPutResponse> responseObserver) {
        return new StreamObserver<OpsPutRequest>() {
            @Override
            public void onNext(OpsPutRequest request) {
                try {
                    if (StringUtils.isBlank(request.getBin()) || StringUtils.isBlank(request.getIdentifier()) || request.getPayloadMap().isEmpty()) {
                        onError(Status.INVALID_ARGUMENT.withDescription("Missing information. Please provide bin, identifier and payload.")
                                .asException());
                    } else {
                        if (binRepo.exists(request.getBin())) {
                            if (LOGGER.isInfoEnabled()) {
                                LOGGER.info(String.format("Bin found by the name of: %s, Proceeding with Put operation.", request.getBin()));
                            }
                            if (opsRepo.exists(request.getBin(), request.getIdentifier())) {
                                Map<String, Object> payload = GRPCUtils.structToMap(request.getPayloadMap());
                                payload.put("id", request.getIdentifier());
                                responseObserver.onNext(OpsPutResponse.newBuilder().putAllPayload(GRPCUtils.mapToStruct(opsRepo.update(request.getBin(), request.getIdentifier(), payload))).build());
                            } else {
                                onError(Status.NOT_FOUND
                                        .withDescription(String.format("Record not found for id: %s",
                                                request.getIdentifier())).asException());
                            }
                        } else {
                            onError(Status.NOT_FOUND
                                    .withDescription(String.format("Bin not found by the name of: %s, Create bin first.",
                                            request.getBin())).asException());
                        }
                    }
                } catch (Exception e) {
                    onError(Status.UNKNOWN.withDescription("Exception with put operation.").withCause(e).asException());
                }
            }

            @Override
            public void onError(Throwable throwable) {
                LOGGER.info("Exception while put operation", throwable);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    @LogThis
    @Override
    public StreamObserver<OpsListRequest> streamList(StreamObserver<OpsListResponse> responseObserver) {
        return new StreamObserver<OpsListRequest>() {
            @Override
            public void onNext(OpsListRequest request) {
                try {
                    if (StringUtils.isBlank(request.getBin())) {
                        onError(Status.INVALID_ARGUMENT.withDescription("Missing information. Please provide bin.")
                                .asException());
                    } else {
                        int page = (request.getPage() == 0) ? 0 : request.getPage() - 1;
                        int limit = (request.getLimit() == 0) ? 10 : request.getLimit();
                        if (binRepo.exists(request.getBin())) {
                            if (LOGGER.isInfoEnabled()) {
                                LOGGER.info(String.format("Bin found by the name of: %s, Proceeding with List operation.", request.getBin()));
                            }
                            long total = opsRepo.count(request.getBin());
                            List<Content> contents = opsRepo.values(request.getBin()).stream()
                                    .filter(Objects::nonNull).map(GRPCUtils::mapToStruct)
                                    .skip((long) page * limit).limit(limit)
                                    .map(content -> Content.newBuilder().putAllPayload(content).build())
                                    .collect(Collectors.toList());
                            responseObserver.onNext(OpsListResponse.newBuilder().addAllContents(contents)
                                    .setCurrentPage(page + 1).setNumberOfElements(contents.size())
                                    .setFirst(Boolean.toString(page == 0)).setLast(Boolean.toString(contents.size() == total)).setEmpty(Boolean.toString(contents.isEmpty()))
                                    .setTotalElements(total).setTotalPages(Math.round(Math.ceil((double) total / (double) limit))).build());
                        } else {
                            onError(Status.NOT_FOUND
                                    .withDescription(String.format("Bin not found by the name of: %s, Create bin first.",
                                            request.getBin())).asException());
                        }
                    }
                } catch (Exception e) {
                    onError(Status.UNKNOWN.withDescription("Exception with list operation.").withCause(e).asException());
                }
            }

            @Override
            public void onError(Throwable throwable) {
                LOGGER.info("Exception while list operation", throwable);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    @LogThis
    @Override
    public StreamObserver<OpsPatchRequest> streamPatch(StreamObserver<OpsPatchResponse> responseObserver) {
        return new StreamObserver<OpsPatchRequest>() {
            @Override
            public void onNext(OpsPatchRequest request) {
                try {
                    if (StringUtils.isBlank(request.getBin()) || StringUtils.isBlank(request.getIdentifier()) || request.getPayloadMap().isEmpty()) {
                        onError(Status.INVALID_ARGUMENT.withDescription("Missing information. Please provide bin, identifier and payload.")
                                .asException());
                    } else {
                        if (binRepo.exists(request.getBin())) {
                            if (LOGGER.isInfoEnabled()) {
                                LOGGER.info(String.format("Bin found by the name of: %s, Proceeding with Patch operation.", request.getBin()));
                            }
                            if (opsRepo.exists(request.getBin(), request.getIdentifier())) {
                                Map<String, Object> existing = opsRepo.get(request.getBin(), request.getIdentifier());
                                if (existing != null) {
                                    Map<String, Object> merged = Stream.concat(existing.entrySet().stream(), GRPCUtils.structToMap(request.getPayloadMap()).entrySet().stream())
                                            .collect(Collectors.toMap(
                                                    Map.Entry::getKey,
                                                    Map.Entry::getValue, (oldV, newV) -> newV));
                                    responseObserver.onNext(OpsPatchResponse.newBuilder().putAllPayload(GRPCUtils.mapToStruct(opsRepo.update(request.getBin(), request.getIdentifier(), merged))).build());
                                }
                            } else {
                                onError(Status.NOT_FOUND
                                        .withDescription(String.format("Record not found for id: %s",
                                                request.getIdentifier())).asException());
                            }
                        } else {
                            onError(Status.NOT_FOUND
                                    .withDescription(String.format("Bin not found by the name of: %s, Create bin first.",
                                            request.getBin())).asException());
                        }
                    }
                } catch (Exception e) {
                    onError(Status.UNKNOWN.withDescription("Exception with patch operation.").withCause(e).asException());
                }
            }

            @Override
            public void onError(Throwable throwable) {
                LOGGER.info("Exception while patch operation", throwable);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    @LogThis
    @Override
    public StreamObserver<OpsDeleteRequest> streamDelete(StreamObserver<OpsDeleteResponse> responseObserver) {
        return new StreamObserver<OpsDeleteRequest>() {
            @Override
            public void onNext(OpsDeleteRequest request) {
                try {
                    if (StringUtils.isBlank(request.getBin()) || StringUtils.isBlank(request.getIdentifier())) {
                        onError(Status.INVALID_ARGUMENT.withDescription("Missing information. Please provider bin and identifier.")
                                .asException());
                    } else {
                        if (binRepo.exists(request.getBin())) {
                            if (LOGGER.isInfoEnabled()) {
                                LOGGER.info(String.format("Bin found by the name of: %s, Proceeding with Delete operation.", request.getBin()));
                            }
                            if (opsRepo.exists(request.getBin(), request.getIdentifier())) {
                                opsRepo.delete(request.getBin(), request.getIdentifier());
                                responseObserver.onNext(OpsDeleteResponse.newBuilder().setPayload(Empty.newBuilder().build()).build());
                            } else {
                                onError(Status.NOT_FOUND
                                        .withDescription(String.format("Record not found for id: %s",
                                                request.getIdentifier())).asException());
                            }
                        } else {
                            onError(Status.NOT_FOUND
                                    .withDescription(String.format("Bin not found by the name of: %s, Create bin first.",
                                            request.getBin())).asException());
                        }
                    }
                } catch (Exception e) {
                    onError(Status.UNKNOWN.withDescription("Exception with delete operation.").withCause(e).asException());
                }
            }

            @Override
            public void onError(Throwable throwable) {
                LOGGER.info("Exception while delete operation", throwable);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
