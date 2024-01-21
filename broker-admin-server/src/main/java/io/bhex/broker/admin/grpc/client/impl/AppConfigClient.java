package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.app_config.AppConfigServiceGrpc;
import io.bhex.broker.grpc.app_config.EditAppIndexIconResponse;
import io.bhex.broker.grpc.app_config.EditAppIndexModuleRequest;
import io.bhex.broker.grpc.app_config.ListAppIndexModulesRequest;
import io.bhex.broker.grpc.app_config.ListAppIndexModulesResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Description:
 * @Date: 2019/10/11 下午4:56
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Service
public class AppConfigClient {

    @Resource
    GrpcClientConfig grpcConfig;

    private AppConfigServiceGrpc.AppConfigServiceBlockingStub getStub() {
        return grpcConfig.appConfigServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    public EditAppIndexIconResponse editAppIndexModule(EditAppIndexModuleRequest request) {
        return getStub().editAppIndexModule(request);
    }

    public ListAppIndexModulesResponse listAppIndexModules(ListAppIndexModulesRequest request) {
        return getStub().listAppIndexModules(request);
    }



}
