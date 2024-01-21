package io.bhex.broker.admin.service.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.base.common.*;
import io.bhex.bhop.common.exception.BizException;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.bhop.common.util.Combo2;
import io.bhex.broker.admin.controller.dto.BaseConfigDTO;
import io.bhex.broker.admin.controller.param.BaseConfigPO;
import io.bhex.broker.admin.controller.param.BatchBaseConfigPO;
import io.bhex.broker.admin.grpc.client.TokenClient;
import io.bhex.broker.admin.service.BaseConfigService;
import io.bhex.broker.admin.util.BeanCopyUtils;
import io.bhex.broker.grpc.admin.QueryTokenReply;
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
@Service("baseTokenConfigService")
public class BaseTokenConfigServiceImpl  extends BaseConfigServiceAbstract  implements BaseConfigService {

    @Autowired
    private TokenClient tokenClient;

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
        EditBaseTokenConfigsRequest.Builder requestBuilder = EditBaseTokenConfigsRequest.newBuilder();
        for (BaseConfigPO po : poList) {
            Combo2<Boolean, String> combo2 = validKeyAndValue(po.getGroup(), po.getKey(), po.getValue().toString(), adminUserReply, batchPo.getOpPlatform());
            log.info("com:{}", combo2);

            validateToken(orgId, po.getToken());
            EditBaseTokenConfigsRequest.Config config = EditBaseTokenConfigsRequest.Config.newBuilder()
                    .setId(po.getId() != null ? po.getId() : 0)
                    .setOrgId(orgId)
                    .setAdminUserName(Strings.nullToEmpty(adminUserReply.getUsername()))
                    .setExtraValue(Strings.nullToEmpty(po.getExtraValue()))
                    .setGroup(po.getGroup())
                    .setKey(po.getKey())
                    .setValue(Strings.nullToEmpty(po.getValue().toString()))
                    .setLanguage(po.getWithLanguage() ? Strings.nullToEmpty(po.getLanguage()) : "")
                    .setToken(Strings.nullToEmpty(po.getToken()))
                    .setStartTime(po.getStartTime() != null ? po.getStartTime() : 0)
                    .setEndTime(po.getEndTime() != null ? po.getEndTime() : 0)
                    .setStatus(po.getStatus())
                    .build();
            requestBuilder.addConfig(config);
        }
        EditReply reply = getClient(batchPo.getOpPlatform()).editBaseTokenConfig(requestBuilder.build());
        return reply;
    }

    @Override
    public EditReply cancelConfig(long orgId, BaseConfigPO po, String adminUserName) {
        validateToken(orgId, po.getToken());
        CancelBaseTokenConfigRequest request = CancelBaseTokenConfigRequest.newBuilder()
                .setOrgId(orgId)
                .setGroup(po.getGroup())
                .setKey(po.getKey())
                .setLanguage(po.getWithLanguage() ? Strings.nullToEmpty(po.getLanguage()) : "")
                .setAdminUserName(Strings.nullToEmpty(adminUserName))
                .setToken(Strings.nullToEmpty(po.getToken()))
                .build();
        EditReply reply = getClient(po.getOpPlatform()).cancelBaseTokenConfig(request);
        return reply;
    }

    private void validateToken(long orgId, String token) {
        if (StringUtils.isEmpty(token)) {
            return;
        }
        QueryTokenReply reply = tokenClient.queryToken(1, 1, 0, token, "", orgId);
        if (CollectionUtils.isEmpty(reply.getTokenDetailsList())) {
            log.error("{} no token:{}", orgId, token);
            throw new BizException(ErrorCode.ERR_REQUEST_PARAMETER);
        }
    }
    @Override
    public EditReply switchConfig(long orgId, BaseConfigPO po, String adminUserName) {
        validateToken(orgId, po.getToken());
        EditBaseTokenConfigsRequest.Builder requestBuilder = EditBaseTokenConfigsRequest.newBuilder();
        EditBaseTokenConfigsRequest.Config config = EditBaseTokenConfigsRequest.Config.newBuilder()
                .setId(po.getId() != null ? po.getId() : 0)
                .setOrgId(orgId)
                .setGroup(po.getGroup())
                .setKey(po.getKey())
                .setLanguage(po.getWithLanguage() ? Strings.nullToEmpty(po.getLanguage()) : "")
                .setAdminUserName(Strings.nullToEmpty(adminUserName))
                .setSwitchStatus(po.getSwitchStatus() ? SwtichStatus.SWITCH_OPEN : SwtichStatus.SWITCH_CLOSE)
                .setValue(po.getSwitchStatus() ? "true" : "false")
                .setToken(Strings.nullToEmpty(po.getToken()))
                .setStartTime(po.getStartTime() != null ? po.getStartTime() : 0)
                .setEndTime(po.getEndTime() != null ? po.getEndTime() : 0)
                .build();
        requestBuilder.addConfig(config);
        EditReply reply = getClient(po.getOpPlatform()).editBaseTokenConfig(requestBuilder.build());
        return reply;
    }

    @Override
    public ConfigSwitchReply getSwitchConfig(long orgId, BaseConfigPO po) {
        GetTokenConfigSwitchRequest request = GetTokenConfigSwitchRequest.newBuilder()
                .setOrgId(orgId)
                .setGroup(po.getGroup())
                .setKey(po.getKey())
                .setLanguage(po.getWithLanguage() ? Strings.nullToEmpty(po.getLanguage()) : "")
                .setToken(Strings.nullToEmpty(po.getToken()))
                .build();
        ConfigSwitchReply reply = getClient(po.getOpPlatform()).getTokenConfigSwitch(request);
        return reply;
    }

    @Override
    public BaseConfigDTO getOneConfig(long orgId, BaseConfigPO po) {
        GetOneBaseTokenConfigRequest request = GetOneBaseTokenConfigRequest.newBuilder()
                .setOrgId(orgId)
                .setGroup(po.getGroup())
                .setKey(po.getKey())
                .setLanguage(po.getWithLanguage() ? Strings.nullToEmpty(po.getLanguage()) : "")
                .setToken(Strings.nullToEmpty(po.getToken()))
                .build();
        BaseTokenConfig baseConfig = getClient(po.getOpPlatform()).getOneBaseTokenConfig(request);
        if (baseConfig.getId() == 0) {
            return new BaseConfigDTO();
        }
        BaseConfigDTO dto = new BaseConfigDTO();
        BeanCopyUtils.copyPropertiesIgnoreNull(baseConfig, dto);
        return dto;
    }

    @Override
    public List<BaseConfigDTO> getConfigsByGroup(long orgId, BaseConfigPO po) {
        po.setToken(null);
        return getConfigs(orgId, po, StringUtils.isEmpty(po.getKey()) ? new ArrayList<>() : Lists.newArrayList(po.getKey()));
    }

    @Override
    public List<BaseConfigDTO> getConfigs(long orgId, BaseConfigPO po, List<String> keys) {
        List<String> tokens = !StringUtils.isEmpty(po.getToken()) ? Arrays.asList(po.getToken()) : new ArrayList<>();
        return getTokenConfigs(orgId, tokens, po.getGroup(), keys, po.getWithLanguage() ? Strings.nullToEmpty(po.getLanguage()) : "", po.getOpPlatform(), po.getPageSize(), po.getFromId());
    }

    @Override
    public List<BaseConfigDTO> getSymbolConfigs(long orgId, List<String> symbols, String group, List<String> keys, String language, String opPlatform, int pageSize, long lastId) {
        throw new RuntimeException("not supported");
    }

    @Override
    public List<BaseConfigDTO> getTokenConfigs(long orgId, List<String> tokens, String group, List<String> keys, String language, String opPlatform, int pageSize, long lastId) {
        GetBaseTokenConfigsRequest request = GetBaseTokenConfigsRequest.newBuilder()
                .setOrgId(orgId)
                .setGroup(group)
                .addAllKey(!CollectionUtils.isEmpty(keys) ? keys : new ArrayList<>())
                .setLanguage(Strings.nullToEmpty(language))
                .addAllKey(!CollectionUtils.isEmpty(tokens) ? tokens : new ArrayList<>())
                .setPageSize(pageSize)
                .setLastId(lastId)
                .build();
        List<BaseTokenConfig> grpcConfigs = getClient(opPlatform).getBaseTokenConfigs(request).getBaseConfigList();
        if (CollectionUtils.isEmpty(grpcConfigs)) {
            return new ArrayList<>();
        }
        List<BaseConfigDTO> configs = grpcConfigs.stream().map(c -> {
            BaseConfigDTO dto = new BaseConfigDTO();
            BeanCopyUtils.copyPropertiesIgnoreNull(c, dto);
            return dto;
        }).collect(Collectors.toList());
        return configs;
    }

    @Override
    public String getBrokerConfig(long orgId, String group, String key, String language) {
        throw new RuntimeException("not supported");
    }
}
