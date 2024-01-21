package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.statistics.QueryOdsDataRequest;
import io.bhex.broker.grpc.statistics.QueryOdsDataResponse;
import io.bhex.broker.grpc.statistics.QueryOdsSymbolDataResponse;
import io.bhex.broker.grpc.statistics.QueryOdsTokenDataResponse;
import io.bhex.broker.grpc.statistics.QueryTopDataRequest;
import io.bhex.broker.grpc.statistics.QueryTopDataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class OdsClient {

    @Resource
    GrpcClientConfig grpcConfig;

    public QueryOdsDataResponse queryOdsData(QueryOdsDataRequest request) {
        return grpcConfig.odsServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).queryOdsData(request);
    }

    public QueryOdsTokenDataResponse queryOdsTokenData(QueryOdsDataRequest request) {
        return grpcConfig.odsServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).queryOdsTokenData(request);
    }

    public QueryOdsSymbolDataResponse queryOdsSymbolData(QueryOdsDataRequest request) {
        return grpcConfig.odsServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).queryOdsSymbolData(request);
    }

    public QueryTopDataResponse queryTopData(QueryTopDataRequest request) {
        return grpcConfig.odsServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).queryTopData(request);
    }
}
