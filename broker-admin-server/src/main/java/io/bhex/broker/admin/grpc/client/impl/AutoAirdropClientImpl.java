package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.broker.admin.grpc.client.AutoAirdropClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.airdrop.AutoAirdropInfo;
import io.bhex.broker.grpc.airdrop.AutoAirdropServiceGrpc;
import io.bhex.broker.grpc.airdrop.GetAutoAirdropInfoRequest;
import io.bhex.broker.grpc.airdrop.SaveAutoAirdropInfoRequest;
import io.bhex.broker.grpc.airdrop.SaveAutoAirdropInfoResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.grpc.client.impl
 * @Author: ming.xu
 * @CreateDate: 30/11/2018 3:59 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Service
public class AutoAirdropClientImpl implements AutoAirdropClient {

    @Resource
    GrpcClientConfig grpcConfig;

    private AutoAirdropServiceGrpc.AutoAirdropServiceBlockingStub getAutoAirdropStub() {
        return grpcConfig.autoAirdropServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    @Override
    public SaveAutoAirdropInfoResponse saveAutoAirdrop(SaveAutoAirdropInfoRequest request) {
        return getAutoAirdropStub().saveAutoAirdrop(request);
    }

    @Override
    public AutoAirdropInfo getAutoAirdrop(GetAutoAirdropInfoRequest request) {
        return getAutoAirdropStub().getAutoAirdrop(request);
    }
}
