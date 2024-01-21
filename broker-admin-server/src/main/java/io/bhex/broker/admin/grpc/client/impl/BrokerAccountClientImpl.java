package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.broker.admin.grpc.client.BrokerAccountClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.account.AccountServiceGrpc;
import io.bhex.broker.grpc.account.GetBrokerAccountCountRequest;
import io.bhex.broker.grpc.account.GetBrokerAccountCountResponse;
import io.bhex.broker.grpc.account.GetBrokerAccountListRequest;
import io.bhex.broker.grpc.account.GetBrokerAccountListResponse;
import io.bhex.broker.grpc.account.VerifyBrokerAccountRequest;
import io.bhex.broker.grpc.account.VerifyBrokerAccountResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.grpc.client.impl
 * @Author: ming.xu
 * @CreateDate: 10/11/2018 12:18 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Slf4j
@Service
public class BrokerAccountClientImpl implements BrokerAccountClient {

    @Resource
    GrpcClientConfig grpcConfig;

    private AccountServiceGrpc.AccountServiceBlockingStub getAccountStub() {
        return grpcConfig.brokerAccountServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    @Override
    public GetBrokerAccountCountResponse getBrokerAccountCount(GetBrokerAccountCountRequest request) {
        GetBrokerAccountCountResponse reply = getAccountStub().getBrokerAccountCount(request);
        return reply;
    }

    @Override
    public VerifyBrokerAccountResponse verifyBrokerAccount(VerifyBrokerAccountRequest request) {
        VerifyBrokerAccountResponse reply = getAccountStub().verifyBrokerAccount(request);
        return reply;
    }

    @Override
    public GetBrokerAccountListResponse getBrokerAccountList(GetBrokerAccountListRequest request) {
        GetBrokerAccountListResponse reply = getAccountStub().getBrokerAccountList(request);
        return reply;
    }

}
