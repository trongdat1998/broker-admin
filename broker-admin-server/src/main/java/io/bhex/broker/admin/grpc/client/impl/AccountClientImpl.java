package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.base.clear.AccountServiceGrpc;
import io.bhex.base.clear.AssetRequest;
import io.bhex.base.clear.AssetResponse;
import io.bhex.broker.admin.grpc.client.AccountClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.grpc.client.impl
 * @Author: ming.xu
 * @CreateDate: 09/11/2018 3:17 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Slf4j
@Service
public class AccountClientImpl implements AccountClient {

    @Resource
    GrpcClientConfig grpcConfig;


    private AccountServiceGrpc.AccountServiceBlockingStub getAccountStub() {
        return grpcConfig.clearAccountServiceBlockingStub(GrpcClientConfig.CLEAR_CHANNEL_NAME);
    }

    @Override
    public AssetResponse getAsset(AssetRequest request) {
        return getAccountStub().getAsset(request);
    }


}
