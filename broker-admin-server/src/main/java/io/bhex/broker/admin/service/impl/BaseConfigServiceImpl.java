package io.bhex.broker.admin.service.impl;

import com.google.common.base.Strings;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.base.common.*;
import io.bhex.bhop.common.service.AdminUserNameService;
import io.bhex.bhop.common.util.Combo2;
import io.bhex.broker.admin.controller.dto.BaseConfigDTO;
import io.bhex.broker.admin.controller.param.BaseConfigPO;
import io.bhex.broker.admin.controller.param.BatchBaseConfigPO;
import io.bhex.broker.admin.service.BaseConfigService;
import io.bhex.broker.admin.util.BeanCopyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("baseConfigService")
public class BaseConfigServiceImpl  extends BaseConfigServiceAbstract implements BaseConfigService {

    @Autowired
    private AdminUserNameService adminUserNameService;

    @Override
    public EditReply editConfig(long orgId, BaseConfigPO po, AdminUserReply adminUserReply) {

        BatchBaseConfigPO baseConfigPO = new BatchBaseConfigPO();
        baseConfigPO.setConfigs(Arrays.asList(po));
        baseConfigPO.setOpPlatform(po.getOpPlatform());
        return editConfigs(orgId, baseConfigPO, adminUserReply);
    }

    @Override
    public EditReply editConfigs(long orgId, BatchBaseConfigPO batchPo, AdminUserReply adminUserReply) {
        List<BaseConfigPO> poList = batchPo.getConfigs();
        EditBaseConfigsRequest.Builder requestBuilder = EditBaseConfigsRequest.newBuilder();
        for (BaseConfigPO po : poList) {

            Combo2<Boolean, String> combo2 = validKeyAndValue(po.getGroup(), po.getKey(), po.getValue().toString(), adminUserReply, batchPo.getOpPlatform());
            log.info("com:{}", combo2);

            EditBaseConfigsRequest.Config config = EditBaseConfigsRequest.Config.newBuilder()
                    .setId(po.getId() != null ? po.getId() : 0)
                    .setOrgId(orgId)
                    .setAdminUserName(Strings.nullToEmpty(adminUserReply.getUsername()))
                    .setExtraValue(Strings.nullToEmpty(po.getExtraValue()))
                    .setGroup(po.getGroup())
                    .setKey(po.getKey())
                    .setValue(Strings.nullToEmpty(po.getValue().toString()))
                    .setLanguage(po.getWithLanguage() ? Strings.nullToEmpty(po.getLanguage()) : "")
                    .setStartTime(po.getStartTime() != null ? po.getStartTime() : 0)
                    .setEndTime(po.getEndTime() != null ? po.getEndTime() : 0)
                    .setStatus(po.getStatus())
                    .build();
            requestBuilder.addConfig(config);
        }
        EditReply reply = getClient(batchPo.getOpPlatform()).editBaseConfigs(requestBuilder.build());
        return reply;
    }

    @Override
    public EditReply cancelConfig(long orgId, BaseConfigPO po, String adminUserName) {
        CancelBaseConfigRequest request = CancelBaseConfigRequest.newBuilder()
                .setOrgId(orgId)
                .setGroup(po.getGroup())
                .setKey(po.getKey())
                .setLanguage(po.getWithLanguage() ? Strings.nullToEmpty(po.getLanguage()) : "")
                .setAdminUserName(Strings.nullToEmpty(adminUserName))
                .build();
        EditReply reply = getClient(po.getOpPlatform()).cancelBaseConfig(request);
        return reply;
    }

    @Override
    public EditReply switchConfig(long orgId, BaseConfigPO po, String adminUserName) {
        EditBaseConfigsRequest.Builder requestBuilder = EditBaseConfigsRequest.newBuilder();
        EditBaseConfigsRequest.Config config = EditBaseConfigsRequest.Config.newBuilder()
                .setId(po.getId() != null ? po.getId() : 0)
                .setOrgId(orgId)
                .setGroup(po.getGroup())
                .setKey(po.getKey())
                .setLanguage(po.getWithLanguage() ? Strings.nullToEmpty(po.getLanguage()) : "")
                .setAdminUserName(Strings.nullToEmpty(adminUserName))
                .setSwitchStatus(po.getSwitchStatus() ? SwtichStatus.SWITCH_OPEN : SwtichStatus.SWITCH_CLOSE)
                .setValue(po.getSwitchStatus() ? "true" : "false")
                .setStartTime(po.getStartTime() != null ? po.getStartTime() : 0)
                .setEndTime(po.getEndTime() != null ? po.getEndTime() : 0)
                .build();
        requestBuilder.addConfig(config);
        EditReply reply = getClient(po.getOpPlatform()).editBaseConfigs(requestBuilder.build());
        return reply;
    }

    @Override
    public ConfigSwitchReply getSwitchConfig(long orgId, BaseConfigPO po) {
        GetConfigSwitchRequest request = GetConfigSwitchRequest.newBuilder()
                .setOrgId(orgId)
                .setGroup(po.getGroup())
                .setKey(po.getKey())
                .setLanguage(po.getWithLanguage() ? Strings.nullToEmpty(po.getLanguage()) : "")
                .build();
        ConfigSwitchReply reply = getClient(po.getOpPlatform()).getConfigSwitch(request);
        return reply;
    }

    @Override
    public BaseConfigDTO getOneConfig(long orgId, BaseConfigPO po) {
        GetOneBaseConfigRequest request = GetOneBaseConfigRequest.newBuilder()
                .setOrgId(orgId)
                .setGroup(po.getGroup())
                .setKey(po.getKey())
                .setLanguage(po.getWithLanguage() ? Strings.nullToEmpty(po.getLanguage()) : "")
                .build();
        BaseConfig baseConfig = getClient(po.getOpPlatform()).getOneBaseConfig(request);
        if (baseConfig.getId() == 0) {
            return null;
        }
        BaseConfigDTO dto = new BaseConfigDTO();
        BeanCopyUtils.copyPropertiesIgnoreNull(baseConfig, dto);
        if (baseConfig.getAdminUserName().contains("@")) {
            dto.setAdminUserName(adminUserNameService.getAdminName(orgId, baseConfig.getAdminUserName()));
        } else {
            dto.setAdminUserName(baseConfig.getAdminUserName());
        }

        return dto;
    }

    @Override
    public String getBrokerConfig(long orgId, String group, String key, String language) {
        BaseConfigPO configPO = new BaseConfigPO();
        configPO.setGroup(group);
        configPO.setKey(key);
        configPO.setOpPlatform(BaseConfigPO.OP_PLATFORM_BROKER);
        configPO.setLanguage(language);
        configPO.setWithLanguage(!StringUtils.isEmpty(language));
        configPO.setStatus(1);
        BaseConfigDTO dto = getOneConfig(orgId, configPO);
        return dto != null ? dto.getValue() : "";
    }

    @Override
    public List<BaseConfigDTO> getConfigsByGroup(long orgId, BaseConfigPO po) {
        List<String> keys = new ArrayList<>();
        if(!StringUtils.isEmpty(po.getKey())){
            keys.add(po.getKey());
        }
        return getConfigs(orgId, po, keys);
    }

    @Override
    public List<BaseConfigDTO> getConfigs(long orgId, BaseConfigPO po, List<String> keys) {
        GetBaseConfigsRequest request = GetBaseConfigsRequest.newBuilder()
                .setOrgId(orgId)
                .setGroup(po.getGroup())
                .addAllKey(!CollectionUtils.isEmpty(keys) ? keys : new ArrayList<>())
                .setLanguage(po.getWithLanguage() ? Strings.nullToEmpty(po.getLanguage()) : "")
                .setPageSize(po.getPageSize())
                .setLastId(po.getFromId())
                .build();
        List<BaseConfig> grpcConfigs = getClient(po.getOpPlatform()).getBaseConfigs(request).getBaseConfigList();
        if (CollectionUtils.isEmpty(grpcConfigs)) {
            return new ArrayList<>();
        }
        List<BaseConfigDTO> configs = grpcConfigs.stream().map(c -> {
            BaseConfigDTO dto = new BaseConfigDTO();
            BeanCopyUtils.copyPropertiesIgnoreNull(c, dto);
            dto.setAdminUserName(adminUserNameService.getAdminName(orgId, c.getAdminUserName()));
            return dto;
        }).collect(Collectors.toList());
        return configs;
    }

    @Override
    public List<BaseConfigDTO> getSymbolConfigs(long orgId, List<String> symbols, String group, List<String> keys, String language, String opPlatform, int pageSize, long lastId) {
        throw new RuntimeException("not supported");
    }

    @Override
    public List<BaseConfigDTO> getTokenConfigs(long orgId, List<String> tokens, String group, List<String> keys, String language, String opPlatform, int pageSize, long lastId) {
        throw new RuntimeException("not supported");
    }



}
