package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.broker.admin.grpc.client.ShareConfigClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.order.SaveShareConfigInfoReply;
import io.bhex.broker.grpc.order.SaveShareConfigInfoRequest;
import io.bhex.broker.grpc.order.ShareConfigInfoByAdminReply;
import io.bhex.broker.grpc.order.ShareConfigInfoByAdminRequest;
import io.bhex.broker.grpc.order.ShareConfigServiceGrpc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.grpc.client.impl
 * @Author: ming.xu
 * @CreateDate: 2019/7/1 11:25 AM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
@Slf4j
@Service
public class ShareConfigClientImpl implements ShareConfigClient {

    @Resource
    GrpcClientConfig grpcConfig;

    private ShareConfigServiceGrpc.ShareConfigServiceBlockingStub getShareConfigStub() {
        return grpcConfig.shareConfigServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    @Override
    public ShareConfigInfoByAdminReply getShareConfigInfo(ShareConfigInfoByAdminRequest request) {
        ShareConfigInfoByAdminReply reply = getShareConfigStub().shareConfigInfoByAdmin(request);
        return reply;
    }

    @Override
    public SaveShareConfigInfoReply saveShareConfigInfo(SaveShareConfigInfoRequest request) {
        SaveShareConfigInfoReply reply = getShareConfigStub().saveShareConfigInfo(request);
        return reply;
    }
}
