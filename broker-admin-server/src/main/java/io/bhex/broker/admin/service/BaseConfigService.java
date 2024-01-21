package io.bhex.broker.admin.service;

import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.base.common.BaseConfigMeta;
import io.bhex.base.common.ConfigSwitchReply;
import io.bhex.base.common.EditReply;
import io.bhex.broker.admin.controller.dto.BaseConfigDTO;
import io.bhex.broker.admin.controller.param.BaseConfigPO;
import io.bhex.broker.admin.controller.param.BatchBaseConfigPO;

import java.util.List;

public interface BaseConfigService {

    //Combo2<Boolean, String> validKeyAndValue(String group, String key, String value, List<AuthInfo> authInfos, String opPlatform);

    BaseConfigMeta getConfigMeta(String group, String key, String opPlatform);

    EditReply editConfig(long orgId, BaseConfigPO po, AdminUserReply adminUserReply);

    EditReply editConfigs(long orgId, BatchBaseConfigPO po, AdminUserReply adminUserReply);

    EditReply cancelConfig(long orgId, BaseConfigPO po, String adminUserName);

    EditReply switchConfig(long orgId, BaseConfigPO po, String adminUserName);

    ConfigSwitchReply getSwitchConfig(long orgId, BaseConfigPO po);

    BaseConfigDTO getOneConfig(long orgId, BaseConfigPO po);

    List<BaseConfigDTO> getConfigsByGroup(long orgId, BaseConfigPO po);

    List<BaseConfigDTO> getConfigs(long orgId, BaseConfigPO po, List<String> keys);

    List<BaseConfigDTO> getSymbolConfigs(long orgId, List<String> symbols, String group, List<String> keys, String language, String opPlatform, int pageSize, long lastId);

    List<BaseConfigDTO> getTokenConfigs(long orgId, List<String> tokens, String group, List<String> keys, String language, String opPlatform, int pageSize, long lastId);

    String getBrokerConfig(long orgId, String group, String key, String language);
}
