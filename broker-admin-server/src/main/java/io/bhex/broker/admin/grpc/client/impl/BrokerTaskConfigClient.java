package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.admin.GetBrokerTaskConfigsReply;
import io.bhex.broker.grpc.admin.GetBrokerTaskConfigsRequest;
import io.bhex.broker.grpc.admin.SaveBrokerTaskConfigReply;
import io.bhex.broker.grpc.admin.SaveBrokerTaskConfigRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class BrokerTaskConfigClient {

    @Resource
    GrpcClientConfig grpcConfig;


    public SaveBrokerTaskConfigReply saveBrokerTaskConfig(SaveBrokerTaskConfigRequest request) {
        return grpcConfig.adminBrokerTaskConfigServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).saveBrokerTaskConfig(request);
    }

    public GetBrokerTaskConfigsReply getBrokerTaskConfigs(GetBrokerTaskConfigsRequest request) {
        return grpcConfig.adminBrokerTaskConfigServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).getBrokerTaskConfigs(request);
    }

}
