package io.bhex.broker.admin.service.impl;

import com.google.common.collect.Lists;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.util.LocaleUtil;
import io.bhex.broker.admin.controller.dto.BrokerSymbolTaskConfigDTO;
import io.bhex.broker.admin.controller.param.BrokerTokenTaskConfigDTO;
import io.bhex.broker.admin.grpc.client.impl.BrokerTaskConfigClient;
import io.bhex.broker.admin.util.BeanCopyUtils;
import io.bhex.broker.grpc.admin.BrokerTaskConfigDetail;
import io.bhex.broker.grpc.admin.GetBrokerTaskConfigsRequest;
import io.bhex.broker.grpc.admin.SaveBrokerTaskConfigReply;
import io.bhex.broker.grpc.admin.SaveBrokerTaskConfigRequest;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BrokerTaskConfigService {
    @Autowired
    private BrokerTaskConfigClient brokerTaskConfigClient;

    public List<BrokerSymbolTaskConfigDTO> getSymbolTaskConfigs(long orgId, int pageSize, long fromId) {
        List<BrokerTaskConfigDetail> configDetails = queryTaskConfigs(1, orgId, pageSize, fromId);
        if (CollectionUtils.isEmpty(configDetails)) {
            configDetails = Lists.newArrayList();
        }
        List<BrokerSymbolTaskConfigDTO> result = configDetails.stream().map(c -> {
            BrokerSymbolTaskConfigDTO dto = new BrokerSymbolTaskConfigDTO();
            BeanCopyUtils.copyPropertiesIgnoreNull(c, dto);

            Map<String, String> actionMap = c.getActionContentMap();
            dto.setPublished(MapUtils.getBoolean(actionMap, "published", false));
            dto.setShowStatus(MapUtils.getBoolean(actionMap, "showStatus", false));
            dto.setBanSellStatus(MapUtils.getBoolean(actionMap, "banSellStatus", false));
            dto.setBanBuyStatus(MapUtils.getBoolean(actionMap, "banBuyStatus", false));

            return dto;
        }).collect(Collectors.toList());
        return result;
    }

    public SaveBrokerTaskConfigReply editSymbolTaskConfig(long orgId, AdminUserReply adminUserReply, BrokerSymbolTaskConfigDTO dto) {
        BrokerTaskConfigDetail.Builder builder = BrokerTaskConfigDetail.newBuilder();
        BeanCopyUtils.copyPropertiesIgnoreNull(dto, builder);

        Map<String, String> actionMap = new HashMap<>();
        if (dto.getStatus() == 1) {
            actionMap.put("published", dto.getPublished().toString());
            actionMap.put("showStatus", dto.getShowStatus().toString());
            actionMap.put("banSellStatus", dto.getBanSellStatus().toString());
            actionMap.put("banBuyStatus", dto.getBanBuyStatus().toString());
        }
        actionMap.put("areaCode", adminUserReply.getAreaCode());
        actionMap.put("phone", adminUserReply.getTelephone());
        actionMap.put("email", adminUserReply.getEmail());
        actionMap.put("language", LocaleUtil.getLanguage());

        builder.putAllActionContent(actionMap);

        builder.setOrgId(orgId);
        builder.setType(1);
        builder.setAdminUserName(adminUserReply.getUsername());
        SaveBrokerTaskConfigReply reply = brokerTaskConfigClient.saveBrokerTaskConfig(SaveBrokerTaskConfigRequest.newBuilder().setTaskConfig(builder.build()).build());
        return reply;
    }

    public SaveBrokerTaskConfigReply editTokenTaskConfig(long orgId, AdminUserReply adminUserReply, BrokerTokenTaskConfigDTO dto) {
        BrokerTaskConfigDetail.Builder builder = BrokerTaskConfigDetail.newBuilder();
        BeanCopyUtils.copyPropertiesIgnoreNull(dto, builder);

        Map<String, String> actionMap = new HashMap<>();
        if (dto.getStatus() == 1) {
            actionMap.put("published", dto.getPublished().toString());
            actionMap.put("withdrawStatus", dto.getWithdrawStatus().toString());
            actionMap.put("depositStatus", dto.getDepositStatus().toString());
        }

        actionMap.put("areaCode", adminUserReply.getAreaCode());
        actionMap.put("phone", adminUserReply.getTelephone());
        actionMap.put("email", adminUserReply.getEmail());
        actionMap.put("language", LocaleUtil.getLanguage());

        builder.putAllActionContent(actionMap);

        builder.setOrgId(orgId);
        //type = 2 为币种
        builder.setType(2);
        builder.setAdminUserName(adminUserReply.getUsername());
        SaveBrokerTaskConfigReply reply = brokerTaskConfigClient.saveBrokerTaskConfig(SaveBrokerTaskConfigRequest.newBuilder().setTaskConfig(builder.build()).build());
        return reply;
    }

    public List<BrokerTokenTaskConfigDTO> getTokenTaskConfigs(long orgId, int pageSize, long fromId) {
        List<BrokerTaskConfigDetail> configDetails = queryTaskConfigs(2, orgId, pageSize, fromId);
        if (CollectionUtils.isEmpty(configDetails)) {
            configDetails = Lists.newArrayList();
        }
        List<BrokerTokenTaskConfigDTO> result = configDetails.stream().map(c -> {
            BrokerTokenTaskConfigDTO dto = new BrokerTokenTaskConfigDTO();
            BeanCopyUtils.copyPropertiesIgnoreNull(c, dto);

            Map<String, String> actionMap = c.getActionContentMap();
            dto.setPublished(MapUtils.getBoolean(actionMap, "published", false));
            dto.setDepositStatus(MapUtils.getBoolean(actionMap, "depositStatus", false));
            dto.setWithdrawStatus(MapUtils.getBoolean(actionMap, "withdrawStatus", false));
            return dto;
        }).collect(Collectors.toList());
        return result;
    }


    private List<BrokerTaskConfigDetail> queryTaskConfigs(int type, long orgId, int pageSize, long fromId) {
        GetBrokerTaskConfigsRequest request = GetBrokerTaskConfigsRequest.newBuilder()
                .setOrgId(orgId)
                .setLastId(fromId)
                .setPageSize(pageSize)
                .setType(type)
                .build();
        return brokerTaskConfigClient.getBrokerTaskConfigs(request).getTaskConfigsList();
    }


}
