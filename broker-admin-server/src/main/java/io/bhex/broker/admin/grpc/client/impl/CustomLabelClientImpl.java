package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.broker.admin.grpc.client.CustomLabelClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.admin.AdminCustomLabelServiceGrpc;
import io.bhex.broker.grpc.admin.DelCustomLabelReply;
import io.bhex.broker.grpc.admin.DelCustomLabelRequest;
import io.bhex.broker.grpc.admin.QueryCustomLabelReply;
import io.bhex.broker.grpc.admin.QueryCustomLabelRequest;
import io.bhex.broker.grpc.admin.SaveCustomLabelReply;
import io.bhex.broker.grpc.admin.SaveCustomLabelRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.grpc.client.impl
 * @Author: ming.xu
 * @CreateDate: 2019/12/12 8:36 PM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
@Slf4j
@Service
public class CustomLabelClientImpl implements CustomLabelClient {

    @Resource
    GrpcClientConfig grpcConfig;

    private AdminCustomLabelServiceGrpc.AdminCustomLabelServiceBlockingStub getCustomLabelStub() {
        return grpcConfig.adminCustomLabelServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    @Override
    public QueryCustomLabelReply queryCustomLabel(QueryCustomLabelRequest request) {
        return getCustomLabelStub().queryCustomLabel(request);
    }

    @Override
    public SaveCustomLabelReply saveCustomLabel(SaveCustomLabelRequest request) {
        return getCustomLabelStub().saveCustomLabel(request);
    }

    @Override
    public DelCustomLabelReply delCustomLabel(DelCustomLabelRequest request) {
        return getCustomLabelStub().delCustomLabel(request);
    }
}
