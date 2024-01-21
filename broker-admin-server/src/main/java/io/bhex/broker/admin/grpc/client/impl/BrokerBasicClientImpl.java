package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.broker.admin.grpc.client.BrokerBasicClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.common.exception.BrokerErrorCode;
import io.bhex.broker.common.exception.BrokerException;
import io.bhex.broker.grpc.admin.AdminCurrencyServiceGrpc;
import io.bhex.broker.grpc.admin.ListCurrencyRequest;
import io.bhex.broker.grpc.admin.ListCurrencyResponse;
import io.bhex.broker.grpc.basic.BasicServiceGrpc;
import io.bhex.broker.grpc.basic.QuerySymbolRequest;
import io.bhex.broker.grpc.basic.QuerySymbolResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Slf4j
@Service
public class BrokerBasicClientImpl implements BrokerBasicClient {

    @Resource
    GrpcClientConfig grpcConfig;

    @Override
    public QuerySymbolResponse querySymbols(QuerySymbolRequest request) {
        BasicServiceGrpc.BasicServiceBlockingStub stub = grpcConfig.basicServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
        QuerySymbolResponse response = stub.querySymbols(request);
        if (response.getRet() != 0) {
            throw new BrokerException(BrokerErrorCode.fromCode(response.getRet()));
        }
        return response;
    }


    @Override
    public ListCurrencyResponse queryCurrencies(ListCurrencyRequest request) {
        AdminCurrencyServiceGrpc.AdminCurrencyServiceBlockingStub stub = grpcConfig.adminCurrencyServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
        ListCurrencyResponse response = stub.listCurrency(request);
        if (response.getRet() != 0) {
            throw new BrokerException(BrokerErrorCode.fromCode(response.getRet()));
        }
        return response;
    }
}
