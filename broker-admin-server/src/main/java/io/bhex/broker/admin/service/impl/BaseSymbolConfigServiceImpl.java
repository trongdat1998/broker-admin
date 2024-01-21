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
import io.bhex.broker.admin.grpc.client.SymbolClient;
import io.bhex.broker.admin.service.BaseConfigService;
import io.bhex.broker.admin.util.BeanCopyUtils;
import io.bhex.broker.grpc.admin.QuerySymbolReply;
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
@Service("baseSymbolConfigService")
public class BaseSymbolConfigServiceImpl extends BaseConfigServiceAbstract implements BaseConfigService {

    @Autowired
    private SymbolClient symbolClient;

    @Override
    public EditReply editConfig(long orgId, BaseConfigPO po, AdminUserReply adminUserReply) {

        BatchBaseConfigPO baseConfigPO = new BatchBaseConfigPO();
        baseConfigPO.setConfigs(Arrays.asList(po));
        baseConfigPO.setOpPlatform(po.getOpPlatform());
        baseConfigPO.setWithLanguage(po.getWithLanguage());
        return editConfigs(orgId, baseConfigPO, adminUserReply);
    }

    @Override
    public EditReply editConfigs(long orgId, BatchBaseConfigPO batchPo, AdminUserReply adminUserReply) {
        List<BaseConfigPO> poList = batchPo.getConfigs();
        EditBaseSymbolConfigsRequest.Builder requestBuilder = EditBaseSymbolConfigsRequest.newBuilder();
        for (BaseConfigPO po : poList) {
            Combo2<Boolean, String> combo2 = validKeyAndValue(po.getGroup(), po.getKey(), po.getValue().toString(), adminUserReply, batchPo.getOpPlatform());
            log.info("com:{}", combo2);
            validateSymbol(orgId, po.getSymbol());
            EditBaseSymbolConfigsRequest.Config config = EditBaseSymbolConfigsRequest.Config.newBuilder()
                    .setId(po.getId() != null ? po.getId() : 0)
                    .setOrgId(orgId)
                    .setAdminUserName(Strings.nullToEmpty(adminUserReply.getUsername()))
                    .setExtraValue(Strings.nullToEmpty(po.getExtraValue()))
                    .setGroup(po.getGroup())
                    .setKey(po.getKey())
                    .setValue(Strings.nullToEmpty(po.getValue().toString()))
                    .setLanguage(batchPo.getWithLanguage() ? Strings.nullToEmpty(po.getLanguage()) : "")
                    .setSymbol(Strings.nullToEmpty(po.getSymbol()))
                    .setStartTime(po.getStartTime() != null ? po.getStartTime() : 0)
                    .setEndTime(po.getEndTime() != null ? po.getEndTime() : 0)
                    .setStatus(po.getStatus())
                    .build();
            requestBuilder.addConfig(config);
        }
        EditReply reply = getClient(batchPo.getOpPlatform()).editBaseSymbolConfigs(requestBuilder.build());
        return reply;
    }

    private void validateSymbol(long orgId, String symbol) {
        if (StringUtils.isEmpty(symbol)) {
            return;
        }
        QuerySymbolReply reply = symbolClient.querySymbol(1, 1, 0, null, symbol, orgId, null, null);
        if (CollectionUtils.isEmpty(reply.getSymbolDetailsList())) {
            log.error("{} no symbol:{}", orgId, symbol);
            throw new BizException(ErrorCode.ERR_REQUEST_PARAMETER);
        }
    }

    @Override
    public EditReply cancelConfig(long orgId, BaseConfigPO po, String adminUserName) {
        validateSymbol(orgId, po.getSymbol());
        CancelBaseSymbolConfigRequest request = CancelBaseSymbolConfigRequest.newBuilder()
                .setOrgId(orgId)
                .setGroup(po.getGroup())
                .setKey(po.getKey())
                .setLanguage(po.getWithLanguage() ? Strings.nullToEmpty(po.getLanguage()) : "")
                .setAdminUserName(Strings.nullToEmpty(adminUserName))
                .setSymbol(Strings.nullToEmpty(po.getSymbol()))
                .build();
        EditReply reply = getClient(po.getOpPlatform()).cancelBaseSymbolConfig(request);
        return reply;
    }

    @Override
    public EditReply switchConfig(long orgId, BaseConfigPO po, String adminUserName) {
        validateSymbol(orgId, po.getSymbol());
        EditBaseSymbolConfigsRequest.Builder requestBuilder = EditBaseSymbolConfigsRequest.newBuilder();
        EditBaseSymbolConfigsRequest.Config config = EditBaseSymbolConfigsRequest.Config.newBuilder()
                .setId(po.getId() != null ? po.getId() : 0)
                .setOrgId(orgId)
                .setGroup(po.getGroup())
                .setKey(po.getKey())
                .setLanguage(po.getWithLanguage() ? Strings.nullToEmpty(po.getLanguage()) : "")
                .setAdminUserName(Strings.nullToEmpty(adminUserName))
                .setSwitchStatus(po.getSwitchStatus() ? SwtichStatus.SWITCH_OPEN : SwtichStatus.SWITCH_CLOSE)
                .setValue(po.getSwitchStatus() ? "true" : "false")
                .setSymbol(Strings.nullToEmpty(po.getSymbol()))
                .setStartTime(po.getStartTime() != null ? po.getStartTime() : 0)
                .setEndTime(po.getEndTime() != null ? po.getEndTime() : 0)
                .build();
        requestBuilder.addConfig(config);
        EditReply reply = getClient(po.getOpPlatform()).editBaseSymbolConfigs(requestBuilder.build());
        return reply;
    }

    @Override
    public ConfigSwitchReply getSwitchConfig(long orgId, BaseConfigPO po) {
        GetSymbolConfigSwitchRequest request = GetSymbolConfigSwitchRequest.newBuilder()
                .setOrgId(orgId)
                .setGroup(po.getGroup())
                .setKey(po.getKey())
                .setLanguage(po.getWithLanguage() ? Strings.nullToEmpty(po.getLanguage()) : "")
                .setSymbol(Strings.nullToEmpty(po.getSymbol()))
                .build();
        ConfigSwitchReply reply = getClient(po.getOpPlatform()).getSymbolConfigSwitch(request);
        return reply;
    }

    @Override
    public BaseConfigDTO getOneConfig(long orgId, BaseConfigPO po) {
        GetOneBaseSymbolConfigRequest request = GetOneBaseSymbolConfigRequest.newBuilder()
                .setOrgId(orgId)
                .setGroup(po.getGroup())
                .setKey(po.getKey())
                .setLanguage(po.getWithLanguage() ? Strings.nullToEmpty(po.getLanguage()) : "")
                .setSymbol(Strings.nullToEmpty(po.getSymbol()))
                .build();
        BaseSymbolConfig baseConfig = getClient(po.getOpPlatform()).getOneBaseSymbolConfig(request);
        if (baseConfig.getId() == 0) {
            return null;
        }
        BaseConfigDTO dto = new BaseConfigDTO();
        BeanCopyUtils.copyPropertiesIgnoreNull(baseConfig, dto);
        return dto;
    }

    @Override
    public List<BaseConfigDTO> getConfigsByGroup(long orgId, BaseConfigPO po) {
        return getConfigs(orgId, po, StringUtils.isEmpty(po.getKey()) ? new ArrayList<>() : Lists.newArrayList(po.getKey()));
    }

    @Override
    public List<BaseConfigDTO> getConfigs(long orgId, BaseConfigPO po, List<String> keys) {
        List<String> symbols = !StringUtils.isEmpty(po.getSymbol()) ? Arrays.asList(po.getSymbol()) : new ArrayList<>();
        return getSymbolConfigs(orgId, symbols, po.getGroup(), keys, po.getWithLanguage() ? Strings.nullToEmpty(po.getLanguage()) : "", po.getOpPlatform(), po.getPageSize(), po.getFromId());
    }

    @Override
    public List<BaseConfigDTO> getSymbolConfigs(long orgId, List<String> symbols, String group, List<String> keys, String language, String opPlatform, int pageSize, long lastId) {
        GetBaseSymbolConfigsRequest request = GetBaseSymbolConfigsRequest.newBuilder()
                .setOrgId(orgId)
                .setGroup(group)
                .addAllKey(!CollectionUtils.isEmpty(keys) ? keys : new ArrayList<>())
                .setLanguage(Strings.nullToEmpty(language))
                .addAllSymbol(CollectionUtils.isEmpty(symbols) ? Arrays.asList() : symbols)
                .setPageSize(pageSize)
                .setLastId(lastId)
                .build();
        List<BaseSymbolConfig> grpcConfigs = getClient(opPlatform).getBaseSymbolConfigs(request).getBaseConfigList();
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
    public List<BaseConfigDTO> getTokenConfigs(long orgId, List<String> tokens, String group, List<String> keys, String language, String opPlatform, int pageSize, long lastId) {
        throw new RuntimeException("not supported");
    }

    @Override
    public String getBrokerConfig(long orgId, String group, String key, String language) {
        throw new RuntimeException("not supported");
    }
}
