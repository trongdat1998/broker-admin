package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.base.common.MessageReply;
import io.bhex.base.common.MessageServiceGrpc;
import io.bhex.base.common.SimpleSMSRequest;
import io.bhex.bhop.common.config.GrpcConfig;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class SupportClientImpl {

    @Resource
    GrpcClientConfig grpcConfig;

    public MessageReply sendSms(SimpleSMSRequest smsRequest){
        MessageServiceGrpc.MessageServiceBlockingStub stub = grpcConfig.messageServiceBlockingStub(GrpcConfig.COMMON_SERVER_CHANNEL_NAME);
        try {
            return stub.sendSimpleSMS(smsRequest);
        } catch (Exception e) {
            log.error("sendSmsNotice Exception", e);
        }

        return null;
    }
}
