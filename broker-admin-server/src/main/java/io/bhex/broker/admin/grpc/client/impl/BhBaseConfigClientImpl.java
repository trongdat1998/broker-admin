package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.base.common.BaseConfig;
import io.bhex.base.common.BaseConfigServiceGrpc;
import io.bhex.base.common.BaseConfigsReply;
import io.bhex.base.common.BaseSymbolConfig;
import io.bhex.base.common.BaseSymbolConfigsReply;
import io.bhex.base.common.BaseTokenConfig;
import io.bhex.base.common.BaseTokenConfigsReply;
import io.bhex.base.common.CancelBaseConfigRequest;
import io.bhex.base.common.CancelBaseSymbolConfigRequest;
import io.bhex.base.common.CancelBaseTokenConfigRequest;
import io.bhex.base.common.ConfigSwitchReply;
import io.bhex.base.common.EditBaseConfigsRequest;
import io.bhex.base.common.EditBaseSymbolConfigsRequest;
import io.bhex.base.common.EditBaseTokenConfigsRequest;
import io.bhex.base.common.EditReply;
import io.bhex.base.common.GetBaseConfigsRequest;
import io.bhex.base.common.GetBaseSymbolConfigsRequest;
import io.bhex.base.common.GetBaseTokenConfigsRequest;
import io.bhex.base.common.GetConfigMetasReply;
import io.bhex.base.common.GetConfigMetasRequest;
import io.bhex.base.common.GetConfigSwitchRequest;
import io.bhex.base.common.GetOneBaseConfigRequest;
import io.bhex.base.common.GetOneBaseSymbolConfigRequest;
import io.bhex.base.common.GetOneBaseTokenConfigRequest;
import io.bhex.base.common.GetSymbolConfigSwitchRequest;
import io.bhex.base.common.GetTokenConfigSwitchRequest;
import io.bhex.broker.admin.grpc.client.BaseConfigClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component("bhBaseConfigClient")
public class BhBaseConfigClientImpl implements BaseConfigClient {

    @Resource
    GrpcClientConfig grpcConfig;

    private BaseConfigServiceGrpc.BaseConfigServiceBlockingStub getStub() {
        return  grpcConfig.baseConfigServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }


    @Override
    public GetConfigMetasReply getConfigMetas(GetConfigMetasRequest request) {
        GetConfigMetasReply response  = getStub().getBhConfigMetas(request);
        return response;
    }

    @Override
    public EditReply editBaseConfigs(EditBaseConfigsRequest request) {
        EditReply response  = getStub().editBhBaseConfigs(request);
        return response;
    }

    @Override
    public EditReply cancelBaseConfig(CancelBaseConfigRequest request) {
        EditReply response  = getStub().cancelBhBaseConfig(request);
        return response;
    }

    @Override
    public BaseConfigsReply getBaseConfigs(GetBaseConfigsRequest request) {
        BaseConfigsReply response  = getStub().getBhBaseConfigs(request);
        return response;
    }

    @Override
    public BaseConfig getOneBaseConfig(GetOneBaseConfigRequest request) {
        BaseConfig response  = getStub().getOneBhBaseConfig(request);
        return response;
    }

    @Override
    public ConfigSwitchReply getConfigSwitch(GetConfigSwitchRequest request) {
        ConfigSwitchReply response  = getStub().getBhConfigSwitch(request);
        return response;
    }

    @Override
    public EditReply editBaseSymbolConfigs(EditBaseSymbolConfigsRequest request) {
        EditReply response  = getStub().editBhBaseSymbolConfigs(request);
        return response;
    }

    @Override
    public EditReply cancelBaseSymbolConfig(CancelBaseSymbolConfigRequest request) {
        EditReply response  = getStub().cancelBhBaseSymbolConfig(request);
        return response;
    }

    @Override
    public BaseSymbolConfigsReply getBaseSymbolConfigs(GetBaseSymbolConfigsRequest request) {
        BaseSymbolConfigsReply response  = getStub().getBhBaseSymbolConfigs(request);
        return response;
    }

    @Override
    public BaseSymbolConfig getOneBaseSymbolConfig(GetOneBaseSymbolConfigRequest request) {
        BaseSymbolConfig response  = getStub().getOneBhBaseSymbolConfig(request);
        return response;
    }

    @Override
    public ConfigSwitchReply getSymbolConfigSwitch(GetSymbolConfigSwitchRequest request) {
        ConfigSwitchReply response  = getStub().getBhSymbolConfigSwitch(request);
        return response;
    }

    @Override
    public EditReply editBaseTokenConfig(EditBaseTokenConfigsRequest request) {
        EditReply response  = getStub().editBhBaseTokenConfigs(request);
        return response;
    }

    @Override
    public EditReply cancelBaseTokenConfig(CancelBaseTokenConfigRequest request) {
        EditReply response  = getStub().cancelBhBaseTokenConfig(request);
        return response;
    }

    @Override
    public BaseTokenConfigsReply getBaseTokenConfigs(GetBaseTokenConfigsRequest request) {
        BaseTokenConfigsReply response  = getStub().getBhBaseTokenConfigs(request);
        return response;
    }

    @Override
    public BaseTokenConfig getOneBaseTokenConfig(GetOneBaseTokenConfigRequest request) {
        BaseTokenConfig response  = getStub().getOneBhBaseTokenConfig(request);
        return response;
    }

    @Override
    public ConfigSwitchReply getTokenConfigSwitch(GetTokenConfigSwitchRequest request) {
        ConfigSwitchReply response  = getStub().getBhTokenConfigSwitch(request);
        return response;
    }

}
