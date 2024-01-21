package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.broker.admin.grpc.client.BannerClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.admin.AdminBannerServiceGrpc;
import io.bhex.broker.grpc.admin.BannerDetail;
import io.bhex.broker.grpc.admin.CreateBannerRequest;
import io.bhex.broker.grpc.admin.DeleteBannerRequest;
import io.bhex.broker.grpc.admin.GetBannerByIdRequest;
import io.bhex.broker.grpc.admin.ListBannerReply;
import io.bhex.broker.grpc.admin.ListBannerRequest;
import io.bhex.broker.grpc.admin.UpdateBannerRequest;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.grpc.client.impl
 * @Author: ming.xu
 * @CreateDate: 28/08/2018 8:30 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Component
public class BannerClientImpl implements BannerClient {

    @Resource
    GrpcClientConfig grpcConfig;

    private AdminBannerServiceGrpc.AdminBannerServiceBlockingStub getBannerStub() {
        return grpcConfig.adminBannerServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    @Override
    public ListBannerReply listBanner(ListBannerRequest request) {
        return getBannerStub().listBanner(request);
    }

    @Override
    public BannerDetail getBannerById(GetBannerByIdRequest request) {
        return getBannerStub().getBannerById(request);
    }

    @Override
    public Boolean createBanner(CreateBannerRequest request) {
        return getBannerStub().createBanner(request).getResult();
    }

    @Override
    public Boolean updateBanner(UpdateBannerRequest request) {
        return getBannerStub().updateBanner(request).getResult();
    }

    @Override
    public Boolean deleteBanner(DeleteBannerRequest request) {
        return getBannerStub().deleteBanner(request).getResult();
    }
}
