package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.base.exadmin.BrokerSmsTemplateServiceGrpc;
import io.bhex.base.exadmin.GetSignsReply;
import io.bhex.base.exadmin.GetSignsRequest;
import io.bhex.base.exadmin.SignReply;
import io.bhex.bhop.common.config.GrpcConfig;
import io.bhex.broker.admin.grpc.client.BrokerSmsTemplateClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Date: 2018/11/1 下午5:23
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */

@Slf4j
@Service
public class BrokerSmsTemplateClientImpl implements BrokerSmsTemplateClient {

    @Resource
    GrpcClientConfig grpcConfig;

    @Override
    public List<SignReply> getSmsSigns(List<Long> brokerIds, Long lastModify) {
        BrokerSmsTemplateServiceGrpc.BrokerSmsTemplateServiceBlockingStub stub
                = grpcConfig.brokerSmsTemplateServiceBlockingStub(GrpcConfig.SAAS_ADMIN_GRPC_CHANNEL_NAME);
        //如果是代理循环处理，同时合并结果集
        List<SignReply> list = new ArrayList<>();
        GetSignsRequest request = GetSignsRequest.newBuilder()
                .addAllBrokerIds(brokerIds)
                .setLastModify(lastModify)
                .build();
        GetSignsReply reply = stub.getSigns(request);
        list.addAll(reply.getSignsList());
        return list;
    }
}
