package io.bhex.broker.admin.service.impl;

import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.base.common.EditReply;
import io.bhex.base.proto.DecimalUtil;
import io.bhex.bhop.common.exception.BizException;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.bhop.common.util.LocaleUtil;
import io.bhex.broker.admin.controller.dto.UserLevelHaveTokenDiscountDTO;
import io.bhex.broker.admin.controller.dto.UserLevelInfoDTO;
import io.bhex.broker.admin.controller.param.BaseConfigPO;
import io.bhex.broker.admin.controller.param.UserLevelConfigPO;
import io.bhex.broker.admin.grpc.client.impl.UserLevelClient;
import io.bhex.broker.admin.service.BaseConfigService;
import io.bhex.broker.admin.util.BeanCopyUtils;
import io.bhex.broker.common.util.JsonUtil;
import io.bhex.broker.grpc.common.Header;
import io.bhex.broker.grpc.margin.InterestConfig;
import io.bhex.broker.grpc.user.level.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserLevelService {

    @Autowired
    private UserLevelClient userLevelClient;
    @Autowired
    @Qualifier(value = "baseConfigService")
    private BaseConfigService baseConfigService;

    public Map<String, String> getDefaultWithdrawLimiter(long orgId) {
        return userLevelClient.getDefaultWithdrawLimiter(orgId);
    }

    public UserLevelConfigResponse userLevelConfig(long brokerId, boolean preview, UserLevelConfigPO configPO) {
        List<String> amountKeys = Lists.newArrayList("balanceAmount", "7dBalanceAmount", "30dBalanceAmount",
                "spotUserFee", "contractUserFee", "30dSpotTradeAmountBtc", "30dContractTradeAmountBtc");

        configPO.getConditions().forEach(c1 -> {
            c1.forEach(c2 -> {
                if (c2.getKey().equals("kycLevel") && c2.getValue() < 1) {
                    throw new BizException(ErrorCode.ERR_REQUEST_PARAMETER, "kyc level must be more than 0");
                }
                if (amountKeys.contains(c2.getKey()) && c2.getMaxValue() != null && c2.getMaxValue().compareTo(BigDecimal.ZERO) > 0
                        && c2.getMinValue() != null && c2.getMinValue().compareTo(BigDecimal.ZERO) > 0
                        && c2.getMaxValue().compareTo(c2.getMinValue()) <= 0) {
                    throw new BizException(ErrorCode.ERR_REQUEST_PARAMETER, "maxValue must be more than minValue");
                }
            });
        });
        UserLevelConfigObj.Builder builder = UserLevelConfigObj.newBuilder();
        BeanCopyUtils.copyPropertiesIgnoreNull(configPO, builder);
        builder.setOrgId(brokerId);
        builder.setLevelCondition(JsonUtil.defaultGson().toJson(configPO.getConditions()));
        builder.setLocaleDetail(JsonUtil.defaultGson().toJson(configPO.getLocaleDetail()));
        builder.setCancelOtc24HWithdrawLimit(configPO.getCancelOtc24hWithdrawLimit());
        builder.setInviteBonusStatus(configPO.getInviteBonusStatus());
        BigDecimal hundred = new BigDecimal("100");
        BigDecimal freeInterest = new BigDecimal("-1");
        List<InterestConfig> interestConfigs = configPO.getTokenInterests().stream()
                .map(tokenInterest -> InterestConfig.newBuilder()
                        .setTokenId(tokenInterest.getTokenId())
                        .setInterest(new BigDecimal(tokenInterest.getInterest()).compareTo(BigDecimal.ZERO) <= 0 ? freeInterest.stripTrailingZeros().toPlainString() :
                                DecimalUtil.toTrimString(new BigDecimal(tokenInterest.getInterest()).divide(hundred, 8, BigDecimal.ROUND_DOWN)))
                        .setInterestPeriod(1)
                        .setCalculationPeriod(3600)
                        .setSettlementPeriod(86400)
                        .build())
                .collect(Collectors.toList());

        UserLevelConfigResponse response = userLevelClient.userLevelConfig(UserLevelConfigRequest.newBuilder()
                .setUserLevelConfig(builder.build()).setPreview(preview).addAllInterestConfig(interestConfigs).build());
        return response;
    }

    public List<UserLevelConfigPO> listUserLevelConfigs(long orgId, long levelConfigId) {
        ListUserLevelConfigsRequest request = ListUserLevelConfigsRequest.newBuilder()
                .setOrgId(orgId)
                .setLevelConfigId(levelConfigId)
                .build();
        List<UserLevelConfigObj> objs = userLevelClient.listUserLevelConfigs(request);
        List<UserLevelConfigPO> configPOS = objs.stream().map(o -> {
            UserLevelConfigPO po = new UserLevelConfigPO();
            BeanCopyUtils.copyPropertiesIgnoreNull(o, po);
            po.setConditions(JsonUtil.defaultGson().fromJson(o.getLevelCondition(),
                    new TypeToken<List<List<UserLevelConfigPO.Condition>>>() {
                    }.getType()));
            po.setLocaleDetail(JsonUtil.defaultGson().fromJson(o.getLocaleDetail(),
                    new TypeToken<List<UserLevelConfigPO.LocaleDetail>>() {
                    }.getType()));
            po.setCancelOtc24hWithdrawLimit(o.getCancelOtc24HWithdrawLimit());
            po.setIsBaseLevel(o.getIsBaseLevel() == 1);
            return po;
        }).collect(Collectors.toList());
        return configPOS;
    }

    public DeleteUserLevelConfigResponse deleteUserLevelConfig(long orgId, long levelConfigId) {
        DeleteUserLevelConfigRequest request = DeleteUserLevelConfigRequest.newBuilder()
                .setOrgId(orgId).setLevelConfigId(levelConfigId)
                .build();
        return userLevelClient.deleteUserLevelConfig(request);
    }

    public List<Long> queryLevelConfigUsers(long orgId, long levelConfigId, long lastId, int pageSize, boolean queryWhiteList) {
        QueryLevelConfigUsersRequest request = QueryLevelConfigUsersRequest.newBuilder()
                .setOrgId(orgId).setLevelConfigId(levelConfigId)
                .setLastId(lastId).setPageSize(pageSize)
                .setQueryWhiteList(queryWhiteList)
                .build();
        return userLevelClient.queryLevelConfigUsers(request);
    }

    public AddWhiteListUsersResponse addWhiteListUsers(long orgId, long levelConfigId, List<Long> userIds) {
        AddWhiteListUsersRequest request = AddWhiteListUsersRequest.newBuilder()
                .setOrgId(orgId)
                .addAllUserId(userIds)
                .setLevelConfigId(levelConfigId)
                .build();
        return userLevelClient.addWhiteListUsers(request);
    }


    public UserLevelInfoDTO getUserLevelInfo(long orgId, Long userId) {
        QueryMyLevelConfigRequest request = QueryMyLevelConfigRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).setUserId(userId).build())
                .setWithTradeData(true)
                .setWithTradeFee(true)
                .build();
        QueryMyLevelConfigResponse response = userLevelClient.queryMyLevelConfig(request);
        UserLevelInfoDTO levelInfoDTO = new UserLevelInfoDTO();
        levelInfoDTO.setMonthTradeAmountInBtc(response.getMonthlyTradeAmountInBtcMap());
        levelInfoDTO.setTradeFeeInUsdt(response.getTradeFeeInUsdtMap());
        if (response.getLevelConfigIdCount() > 0) {
            List<UserLevelConfigPO> levelConfigs = listUserLevelConfigs(orgId, response.getLevelConfigId(0));
            String language = LocaleUtil.getLanguage();
            if (!CollectionUtils.isEmpty(levelConfigs)) {
                Optional<UserLevelConfigPO.LocaleDetail> optional = levelConfigs.get(0).getLocaleDetail()
                        .stream().filter(l -> l.getLanguage().equals(language)).findFirst();
                if (optional.isPresent()) {
                    levelInfoDTO.setLevelName(optional.get().getLevelName());
                }
            }
        }
        return levelInfoDTO;
    }

    public void addHaveTokenDiscount(long orgId, UserLevelHaveTokenDiscountDTO po, AdminUserReply adminUser) {
        BaseConfigPO configPO = new BaseConfigPO();
        configPO.setGroup("user.level.config");
        configPO.setKey("have.token.config");
        configPO.setValue(JsonUtil.defaultGson().toJson(po));
        configPO.setOpPlatform(BaseConfigPO.OP_PLATFORM_BROKER);
        configPO.setLanguage(null);
        configPO.setWithLanguage(false);
        EditReply editReply = baseConfigService.editConfig(orgId, configPO, adminUser);
    }

    public UserLevelHaveTokenDiscountDTO queryHaveTokenDiscount(long orgId) {
        String configStr = baseConfigService.getBrokerConfig(orgId, "user.level.config", "have.token.config", "");
        if (StringUtils.isEmpty(configStr)) {
            return new UserLevelHaveTokenDiscountDTO();
        }
        UserLevelHaveTokenDiscountDTO dto = JsonUtil.defaultGson().fromJson(configStr, UserLevelHaveTokenDiscountDTO.class);
        return dto;
    }
}
