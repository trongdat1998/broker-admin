package io.bhex.broker.admin.grpc.client.impl;

import com.google.protobuf.TextFormat;
import io.bhex.broker.admin.grpc.client.BrokerConfigClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.common.exception.BrokerErrorCode;
import io.bhex.broker.common.exception.BrokerException;
import io.bhex.broker.grpc.admin.*;
import io.bhex.broker.grpc.basic.BasicServiceGrpc;
import io.bhex.broker.grpc.basic.QueryCountryRequest;
import io.bhex.broker.grpc.basic.QueryCountryResponse;
import io.grpc.Deadline;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.grpc.client.impl
 * @Author: ming.xu
 * @CreateDate: 29/09/2018 3:50 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Slf4j
@Service
public class BrokerConfigClientImpl implements BrokerConfigClient {

    @Resource
    GrpcClientConfig grpcConfig;

    private AdminBrokerConfigServiceGrpc.AdminBrokerConfigServiceBlockingStub getBrokerConfigStub() {
        return grpcConfig.adminBrokerConfigServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    @Override
    public AdminBrokerConfigDetail getBrokerWholeConfig(GetBrokerConfigRequest request) {
        return getBrokerConfigStub().getBrokerWholeConfig(request);
    }

    @Override
    public Boolean saveBrokerWholeConfig(SaveBrokerConfigRequest request) {
        log.info("request:{}", TextFormat.shortDebugString(request));
        return getBrokerConfigStub().saveBrokerWholeConfig(request).getResult();
    }

    @Override
    public SaveConfigReply editIndexCustomerConfig(EditIndexCustomerConfigRequest request) {
        return getBrokerConfigStub().editIndexCustomerConfig(request);
    }

    @Override
    public List<IndexCustomerConfig> getIndexCustomerConfig(GetIndexCustomerConfigRequest request) {
        return getBrokerConfigStub().getIndexCustomerConfig(request).getConfigsList();
    }

    @Override
    public SaveConfigReply switchIndexCustomerConfig(SwitchIndexCustomerConfigRequest request) {
        return getBrokerConfigStub().switchIndexCustomerConfig(request);
    }

    @Override
    public QueryCountryResponse queryCountries() {
        BasicServiceGrpc.BasicServiceBlockingStub stub = grpcConfig.basicServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);

        QueryCountryRequest request = QueryCountryRequest.newBuilder().build();
        QueryCountryResponse response = stub.queryCountries(request);
        if (response.getRet() != 0) {
            throw new BrokerException(BrokerErrorCode.fromCode(response.getRet()));
        }
        return response;

    }
}
