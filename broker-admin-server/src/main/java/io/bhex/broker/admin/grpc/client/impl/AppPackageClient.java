package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.app_config.AppConfigServiceGrpc;
import io.bhex.broker.grpc.app_config.AppDownloadInfoRequest;
import io.bhex.broker.grpc.app_config.AppDownloadInfos;
import io.bhex.broker.grpc.app_config.QueryAppUpdateLogsRequest;
import io.bhex.broker.grpc.app_config.QueryAppUpdateLogsResponse;
import io.bhex.broker.grpc.app_config.SaveAppDownloadInfoResponse;
import io.bhex.broker.grpc.app_config.SaveAppUpdateInfoRequest;
import io.bhex.broker.grpc.app_config.SaveAppUpdateInfoResponse;
import io.bhex.broker.grpc.common.Header;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Description:
 * @Date: 2019/8/9 下午7:11
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Service
public class AppPackageClient {

    @Resource
    GrpcClientConfig grpcConfig;

    private AppConfigServiceGrpc.AppConfigServiceBlockingStub getStub() {
        return grpcConfig.appConfigServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    public AppDownloadInfos getAppDownloadInfos(Header header) {
        return getStub().getAppDownloadInfos(header);
    }

    public SaveAppDownloadInfoResponse saveAppDownloadInfo(AppDownloadInfoRequest request) {
        return getStub().saveAppDownloadInfo(request);
    }

    public SaveAppUpdateInfoResponse saveAppUpdateInfo(SaveAppUpdateInfoRequest request) {
        return getStub().saveAppUpdateInfo(request);
    }

    public QueryAppUpdateLogsResponse queryAppUpdateLogs(QueryAppUpdateLogsRequest request) {
        return getStub().queryAppUpdateLogs(request);
    }
}
