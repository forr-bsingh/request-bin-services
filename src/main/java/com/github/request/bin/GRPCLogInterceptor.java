package com.github.request.bin;

import io.grpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GRPCLogInterceptor implements ServerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(GRPCLogInterceptor.class);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers,
                                                                 ServerCallHandler<ReqT, RespT> next) {
        LOGGER.info("Calling: " + call.getMethodDescriptor().getFullMethodName());
        LOGGER.info("Client Headers : " + headers);
        ServerCall<ReqT, RespT> wCall = new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
            public void sendMessage(RespT message) {
                super.sendMessage(message);
            }

            public void sendHeaders(Metadata headers) {
                super.sendHeaders(headers);
            }

            public void close(Status status, Metadata trailers) {
                super.close(status, trailers);
            }
        };
        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(next.startCall(wCall, headers)) {
            public void onMessage(ReqT message) {
                super.onMessage(message);
            }

            public void onHalfClose() {
                super.onHalfClose();
            }

            public void onCancel() {
                super.onCancel();
            }
        };
    }

}
