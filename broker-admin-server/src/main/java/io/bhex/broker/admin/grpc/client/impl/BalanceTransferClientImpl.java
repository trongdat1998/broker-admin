package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.base.account.BatchTransferRequest;
import io.bhex.base.account.BatchTransferResponse;
import io.bhex.base.account.BatchTransferServiceGrpc;
import io.bhex.base.account.SyncTransferRequest;
import io.bhex.base.account.SyncTransferResponse;
import io.bhex.bhop.common.config.GrpcConfig;
import io.bhex.broker.admin.grpc.client.BalanceTransferClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.grpc.client.impl
 * @Author: ming.xu
 * @CreateDate: 08/11/2018 9:38 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Slf4j
@Service
public class BalanceTransferClientImpl implements BalanceTransferClient {

    @Resource
    GrpcClientConfig grpcConfig;

    private BatchTransferServiceGrpc.BatchTransferServiceBlockingStub getTransferStub() {
        return grpcConfig.batchTransferServiceBlockingStub(GrpcConfig.SAAS_ADMIN_GRPC_CHANNEL_NAME);
    }

    @Override
    public BatchTransferResponse batchTransfer(BatchTransferRequest request) {
        BatchTransferResponse reply = getTransferStub().batchTransfer(request);
        return reply;
    }

    @Override
    public SyncTransferResponse syncTransfer(SyncTransferRequest request) {
        return getTransferStub().syncTransfer(request);
    }
}
