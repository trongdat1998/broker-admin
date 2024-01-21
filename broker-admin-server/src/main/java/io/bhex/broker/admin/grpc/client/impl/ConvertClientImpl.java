package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.broker.admin.grpc.client.ConvertClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.admin.BrokerUserServiceGrpc;
import io.bhex.broker.grpc.admin.QueryFundAccountShowRequest;
import io.bhex.broker.grpc.admin.QueryFundAccountShowResponse;
import io.bhex.broker.grpc.convert.AddConvertSymbolRequest;
import io.bhex.broker.grpc.convert.AddConvertSymbolResponse;
import io.bhex.broker.grpc.convert.AdminQueryConvertOrdersRequest;
import io.bhex.broker.grpc.convert.AdminQueryConvertOrdersResponse;
import io.bhex.broker.grpc.convert.GetConvertSymbolsRequest;
import io.bhex.broker.grpc.convert.GetConvertSymbolsResponse;
import io.bhex.broker.grpc.convert.ModifyConvertSymbolRequest;
import io.bhex.broker.grpc.convert.ModifyConvertSymbolResponse;
import io.bhex.broker.grpc.convert.UpdateConvertSymbolStatusRequest;
import io.bhex.broker.grpc.convert.UpdateConvertSymbolStatusResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ConvertClientImpl implements ConvertClient {

    @Resource
    GrpcClientConfig grpcConfig;

    @Override
    public AddConvertSymbolResponse addConvertSymbol(AddConvertSymbolRequest request) {
        return grpcConfig.convertServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).addConvertSymbol(request);
    }

    @Override
    public GetConvertSymbolsResponse getConvertSymbols(GetConvertSymbolsRequest request) {
        return grpcConfig.convertServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).getConvertSymbols(request);
    }

    @Override
    public ModifyConvertSymbolResponse modifyConvertSymbol(ModifyConvertSymbolRequest request) {
        return grpcConfig.convertServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).modifyConvertSymbol(request);
    }

    @Override
    public UpdateConvertSymbolStatusResponse updateConvertSymbolStatus(UpdateConvertSymbolStatusRequest request) {
        return grpcConfig.convertServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).updateConvertSymbolStatus(request);
    }

    @Override
    public AdminQueryConvertOrdersResponse queryConvertOrders(AdminQueryConvertOrdersRequest request) {
        return grpcConfig.convertServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).adminQueryConvertOrders(request);
    }

    private BrokerUserServiceGrpc.BrokerUserServiceBlockingStub getBrokerUserStub() {
        return grpcConfig.brokerUserServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    @Override
    public QueryFundAccountShowResponse queryFundAccountShow(QueryFundAccountShowRequest request) {
        return getBrokerUserStub().queryFundAccountShow(request);
    }
}
