package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.broker.admin.grpc.client.OptionClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.admin.CreateOptionReply;
import io.bhex.broker.grpc.admin.CreateOptionRequest;
import io.bhex.broker.grpc.admin.QueryOptionListRequest;
import io.bhex.broker.grpc.admin.QueryOptionListResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ProjectName:
 * @Package:
 * @Author: yuehao  <hao.yue@bhex.com>
 * @CreateDate: 2020/9/8 下午6:41
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */


@Slf4j
@Service
public class OptionClientImpl implements OptionClient {

    @Resource
    GrpcClientConfig grpcConfig;


    @Override
    public CreateOptionReply createOption(CreateOptionRequest request) {
        return grpcConfig.adminBrokerOptionServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).createOption(request);
    }

    @Override
    public QueryOptionListResponse queryOptionList(QueryOptionListRequest request) {
        return grpcConfig.adminBrokerOptionServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).queryOptionList(request);
    }
}
