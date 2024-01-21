package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.broker.admin.grpc.client.BrokerExchangeClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.admin.AdminBrokerExchangeServiceGrpc;
import io.bhex.broker.grpc.admin.ChangeBrokerExchangeRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.grpc.client.impl
 * @Author: ming.xu
 * @CreateDate: 22/10/2018 3:09 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Service
public class BrokerExchangeClientImpl implements BrokerExchangeClient {

    @Resource
    GrpcClientConfig grpcConfig;

    private AdminBrokerExchangeServiceGrpc.AdminBrokerExchangeServiceBlockingStub getBrokerExchangeStub() {
        return grpcConfig.adminBrokerExchangeServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    @Override
    public Boolean enableContract(ChangeBrokerExchangeRequest request) {
        return getBrokerExchangeStub().enableContract(request).getResult();
    }

    @Override
    public Boolean disableContract(ChangeBrokerExchangeRequest request) {
        return getBrokerExchangeStub().disableContract(request).getResult();
    }
}
