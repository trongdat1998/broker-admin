package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.base.account.AskWalletAddressReply;
import io.bhex.base.account.AskWalletAddressRequest;
import io.bhex.base.account.DepositRecordList;
import io.bhex.base.account.GetDepositRecordsRequest;
import io.bhex.base.account.ReceiptReply;
import io.bhex.base.account.ReceiptRequest;
import io.bhex.broker.admin.grpc.client.DepositClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.deposit.DepositServiceGrpc;
import io.bhex.broker.grpc.deposit.QueryDepositOrdersRequest;
import io.bhex.broker.grpc.deposit.QueryDepositOrdersResponse;
import io.bhex.broker.grpc.statistics.QueryOrgDepositOrderRequest;
import io.bhex.broker.grpc.statistics.QueryOrgDepositOrderResponse;
import io.bhex.broker.grpc.statistics.StatisticsServiceGrpc;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.grpc.client.impl
 * @Author: ming.xu
 * @CreateDate: 20/11/2018 5:11 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Service
public class DepositClientImpl implements DepositClient {

    @Resource
    GrpcClientConfig grpcConfig;

    private StatisticsServiceGrpc.StatisticsServiceBlockingStub getStatisticStub() {
        return grpcConfig.statisticsServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    private DepositServiceGrpc.DepositServiceBlockingStub getDepositStub() {
        return grpcConfig.depositServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    private io.bhex.base.account.DepositServiceGrpc.DepositServiceBlockingStub bhDepositStub() {
        return grpcConfig.bhDepositServiceBlockingStub(GrpcClientConfig.BH_SERVER_CHANNEL_NAME);
    }

    @Override
    public QueryDepositOrdersResponse queryDepositOrders(QueryDepositOrdersRequest request) {
        return getDepositStub().queryDepositOrders(request);
    }

    @Override
    public QueryOrgDepositOrderResponse queryOrgDepositOrder(QueryOrgDepositOrderRequest request) {
        return getStatisticStub().queryOrgDepositOrder(request);
    }

    @Override
    public DepositRecordList queryUnReceiptOrder(GetDepositRecordsRequest request) {
        return bhDepositStub().getDepositRecords(request);
    }

    @Override
    public ReceiptReply receipt(ReceiptRequest request) {
        return bhDepositStub().receipt(request);
    }

    @Override
    public AskWalletAddressReply askWalletAddress(AskWalletAddressRequest request) {
        return bhDepositStub().askWalletAddress(request);
    }
}
