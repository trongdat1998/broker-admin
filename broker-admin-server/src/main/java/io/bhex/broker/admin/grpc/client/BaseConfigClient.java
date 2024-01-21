package io.bhex.broker.admin.grpc.client;

import io.bhex.base.common.*;

public interface BaseConfigClient {

    GetConfigMetasReply getConfigMetas(GetConfigMetasRequest request);

    EditReply editBaseConfigs(EditBaseConfigsRequest request);

    EditReply cancelBaseConfig(CancelBaseConfigRequest request);

    BaseConfigsReply getBaseConfigs(GetBaseConfigsRequest request);

    BaseConfig getOneBaseConfig(GetOneBaseConfigRequest request);

    ConfigSwitchReply getConfigSwitch(GetConfigSwitchRequest request);

    EditReply editBaseSymbolConfigs(EditBaseSymbolConfigsRequest request);

    EditReply cancelBaseSymbolConfig(CancelBaseSymbolConfigRequest request);

    BaseSymbolConfigsReply getBaseSymbolConfigs(GetBaseSymbolConfigsRequest request);

    BaseSymbolConfig getOneBaseSymbolConfig(GetOneBaseSymbolConfigRequest request);

    ConfigSwitchReply getSymbolConfigSwitch(GetSymbolConfigSwitchRequest request);

    EditReply editBaseTokenConfig(EditBaseTokenConfigsRequest request);

    EditReply cancelBaseTokenConfig(CancelBaseTokenConfigRequest request);

    BaseTokenConfigsReply getBaseTokenConfigs(GetBaseTokenConfigsRequest request);

    BaseTokenConfig getOneBaseTokenConfig(GetOneBaseTokenConfigRequest request);

    ConfigSwitchReply getTokenConfigSwitch(GetTokenConfigSwitchRequest request);

}
