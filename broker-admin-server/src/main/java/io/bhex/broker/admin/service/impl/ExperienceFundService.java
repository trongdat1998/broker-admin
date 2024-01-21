package io.bhex.broker.admin.service.impl;

import com.google.common.base.Strings;
import io.bhex.base.account.AccountType;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.dto.param.BalanceDetailDTO;
import io.bhex.bhop.common.exception.BizException;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.bhop.common.grpc.client.AccountAssetClient;
import io.bhex.bhop.common.service.AdminUserNameService;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.constants.BizConstant;
import io.bhex.broker.admin.controller.dto.ExperienceFundInfoDTO;
import io.bhex.broker.admin.controller.dto.ExperienceFundTransferRecordDTO;
import io.bhex.broker.admin.controller.param.QueryExperienceFundPO;
import io.bhex.broker.admin.controller.param.SaveExperienceFundPO;
import io.bhex.broker.admin.grpc.client.SymbolClient;
import io.bhex.broker.admin.grpc.client.impl.ExperienceFundClient;
import io.bhex.broker.admin.service.BaseConfigService;
import io.bhex.broker.admin.service.OrgAccountService;
import io.bhex.broker.admin.util.BeanCopyUtils;
import io.bhex.broker.grpc.activity.experiencefund.*;
import io.bhex.broker.grpc.admin.QuerySymbolReply;
import io.bhex.broker.grpc.admin.SymbolDetail;
import io.bhex.broker.grpc.admin.UserAccountMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ExperienceFundService {
    @Autowired
    private ExperienceFundClient experienceFundClient;
    @Autowired
    private OrgAccountService orgAccountService;
    @Autowired
    private AccountAssetClient accountAssetClient;
    @Autowired
    private RateService rateService;
    @Autowired
    private SymbolClient symbolClient;
    @Autowired
    @Qualifier(value = "baseConfigService")
    private BaseConfigService baseConfigService;
    @Autowired
    private AdminUserNameService adminUserNameService;

    // 单个用户空投的USDT上限
    private static final BigDecimal USDT_LIMIT = new BigDecimal(30);

    public ResultModel saveExperienceFundInfo(SaveExperienceFundPO po, AdminUserReply adminUser, List<UserAccountMap> accountMaps, boolean submit) {


        CheckAccountJoinedActivityRequest.Builder checkBuilder = CheckAccountJoinedActivityRequest.newBuilder()
                .setBrokerId(adminUser.getOrgId()).setType(po.getType());
        Map<Long, Long> userMap = new HashMap<>();
        for (UserAccountMap accountMap : accountMaps) {
            userMap.put(accountMap.getUserId(), accountMap.getAccountId());
            checkBuilder.addAccountId(accountMap.getAccountId());
        }

        //检查此uid是否参加过其它活动
        if (adminUser.getOrgId() == 6002) {
            Map<Long, Boolean> joinedMap = experienceFundClient.checkAccountJoinedActivity(checkBuilder.build());
            List<String> joinedUserids = new ArrayList<>();
            for (Long accountId : joinedMap.keySet()) {
                if (joinedMap.get(accountId)) {
                    for (Long userId : userMap.keySet()) {
                        if (userMap.get(userId).equals(accountId)) {
                            joinedUserids.add(userId.toString());
                        }
                    }
                }
            }
            if (!CollectionUtils.isEmpty(joinedUserids)) {
                return ResultModel.error(ErrorCode.USER_JOINED_ACTIVITY.getCode(), ErrorCode.USER_JOINED_ACTIVITY.getDesc(), joinedUserids);
            }
        }

        //检查运营账户资产
        Long orgAccountId = orgAccountService.getOrgAccountIdByType(adminUser.getOrgId(), AccountType.OPERATION_ACCOUNT);
        if (orgAccountId == null) {
            throw new BizException("airdrop.org.account.not.exist");
        }
        List<BalanceDetailDTO> list = accountAssetClient.getBalances(adminUser.getOrgId(), orgAccountId);
        BigDecimal totalTokenAmount = po.getTokenAmount().multiply(new BigDecimal(accountMaps.size()));
        Optional<BalanceDetailDTO> balanceDetailDTOOptional = list.stream().filter(d -> d.getTokenId().equals(po.getTokenId())).findFirst();
        if (balanceDetailDTOOptional.isPresent()) {
            BigDecimal orgAvailable = balanceDetailDTOOptional.get().getAvailable();
            if (orgAvailable.compareTo(totalTokenAmount) < 0) {
                throw new BizException(ErrorCode.ORG_OPERATION_INSUFFICIENT);
            }
        }

        //检查是否超过空投usdt上限
        BigDecimal usdtLimitValue;
        String usdtLimitConfigValue = baseConfigService.getBrokerConfig(adminUser.getOrgId(), BizConstant.CUSTOM_CONFIG_GROUP,
                "experience.fund.usdtlimit", null);
        if (StringUtils.isNotEmpty(usdtLimitConfigValue)) {
            usdtLimitValue = new BigDecimal(usdtLimitConfigValue);
        } else {
            usdtLimitValue = USDT_LIMIT;
        }
        BigDecimal usdtRate = rateService.convertUSDTRate(adminUser.getOrgId(), po.getTokenId());
        if (usdtRate.multiply(po.getTokenAmount()).compareTo(usdtLimitValue) > 0) {
            throw new BizException(ErrorCode.OVER_AMOUNT_LIMIT);
        }

        if (!submit) {
            return ResultModel.ok();
        }

        SaveExperienceFundInfoRequest.Builder builder = SaveExperienceFundInfoRequest.newBuilder();
        BeanCopyUtils.copyPropertiesIgnoreNull(po, builder);
        builder.setAdminUserName(adminUser.getUsername());
        builder.setBrokerId(adminUser.getOrgId());
        builder.setTokenAmount(po.getTokenAmount().toPlainString());
        builder.setOrgAccountType(AccountType.OPERATION_ACCOUNT_VALUE);
        builder.putAllUserInfo(userMap);
        builder.setOrgAccountId(orgAccountId);
        if (po.getRedeemType() == 0) { //不回收
            long redeemTime =  System.currentTimeMillis() + 14 * 24 * 3600_000L;
            builder.setRedeemTime(redeemTime);
            builder.setDeadline(redeemTime);
        } else {
            builder.setDeadline(po.getRedeemTime() + 14 * 24 * 3600_000L);
        }
        experienceFundClient.saveExperienceFundInfo(builder.build());
        return ResultModel.ok();
    }

    public List<ExperienceFundInfoDTO> queryExperienceFunds(QueryExperienceFundPO po, long brokerId) {
        QueryExperienceFundsRequest request = QueryExperienceFundsRequest.newBuilder()
                .setBrokerId(brokerId)
                .setFromId(po.getLastId())
                .setPageSize(po.getPageSize())
                .setType(po.getType())
                .setTitle(Strings.nullToEmpty(po.getTitle()))
                .build();
        List<ExperienceFundInfo> list = experienceFundClient.queryExperienceFunds(request);
        List<ExperienceFundInfoDTO> result = list.stream().map(i -> {
            ExperienceFundInfoDTO dto = new ExperienceFundInfoDTO();
            BeanCopyUtils.copyPropertiesIgnoreNull(i, dto);
            dto.setTokenAmount(new BigDecimal(i.getTokenAmount()));
            dto.setRedeemAmount(new BigDecimal(i.getRedeemAmount()));
            dto.setUserCount(i.getUserCount());
            dto.setAdminUserName(adminUserNameService.getAdminName(brokerId, i.getAdminUserName()));
            return dto;
        }).collect(Collectors.toList());
        return result;
    }

    public List<ExperienceFundTransferRecordDTO> queryTransferRecords(QueryExperienceFundPO po, long brokerId) {
        QueryTransferRecordsRequest request = QueryTransferRecordsRequest.newBuilder()
                .setBrokerId(brokerId)
                .setFromId(po.getLastId())
                .setPageSize(po.getPageSize())
                .setStatus(po.getStatus())
                .setActivityId(po.getId())
                .build();
        List<ExperienceFundTransferRecord> list = experienceFundClient.queryTransferList(request);
        List<ExperienceFundTransferRecordDTO> result = list.stream().map(i -> {
            ExperienceFundTransferRecordDTO dto = new ExperienceFundTransferRecordDTO();
            BeanCopyUtils.copyPropertiesIgnoreNull(i, dto);
            dto.setTokenAmount(new BigDecimal(i.getTokenAmount()));
            dto.setRedeemAmount(new BigDecimal(i.getRedeemAmount()));
            dto.setStatus(i.getStatusValue());
            return dto;
        }).collect(Collectors.toList());
        return result;
    }

    public ExperienceFundInfoDTO queryExperienceFundDetail(long id, long brokerId) {
        QueryExperienceFundDetailRequest request = QueryExperienceFundDetailRequest.newBuilder()
                .setBrokerId(brokerId).setId(id)
                .build();
        ExperienceFundInfo info = experienceFundClient.queryExperienceFundDetail(request);
        ExperienceFundInfoDTO dto = new ExperienceFundInfoDTO();
        if (info.getId() == 0) {
            return dto;
        }
        BeanCopyUtils.copyPropertiesIgnoreNull(info, dto);
        dto.setTokenAmount(new BigDecimal(info.getTokenAmount()));
        dto.setRedeemAmount(new BigDecimal(info.getRedeemAmount()));
        dto.setAdminUserName(adminUserNameService.getAdminName(brokerId, info.getAdminUserName()));
        return dto;
    }



    public List<String> queryFuturesCoinToken(long orgId) {
        List<String> list = symbolClient.queryFuturesCoinToken(orgId);
        return list;
    }
}
