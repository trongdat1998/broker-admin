package io.bhex.broker.admin.service.impl;

import io.bhex.base.account.GetBrokerTradeMinFeeRequest;
import io.bhex.base.account.GetBrokerTradeMinFeeResponse;
import io.bhex.base.admin.common.*;
import io.bhex.base.proto.BaseRequest;
import io.bhex.base.proto.DecimalUtil;
import io.bhex.bhop.common.util.BaseReqUtil;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.BrokerAccountTradeFeeGroupDTO;
import io.bhex.broker.admin.controller.param.BrokerAccountTradeFeeGroupPO;
import io.bhex.broker.admin.grpc.client.BorkerTradeFeeSettingClient;
import io.bhex.broker.admin.grpc.client.BrokerAccountClient;
import io.bhex.broker.admin.grpc.client.BrokerAccountTradeFeeSettingClient;
import io.bhex.broker.admin.service.BrokerAccountTradeFeeSettingService;
import io.bhex.broker.admin.service.ExchangeContractService;
import io.bhex.broker.grpc.account.SimpleAccount;
import io.bhex.broker.grpc.account.VerifyBrokerAccountRequest;
import io.bhex.broker.grpc.account.VerifyBrokerAccountResponse;
import io.bhex.broker.grpc.common.Header;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Date: 2018/11/23 下午3:53
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Slf4j
@Service
public class BrokerAccountTradeFeeSettingServiceImpl implements BrokerAccountTradeFeeSettingService {
    @Autowired
    private BrokerAccountTradeFeeSettingClient feeClient;
    @Autowired
    private BrokerAccountClient brokerAccountClient;
    @Autowired
    private BorkerTradeFeeSettingClient brokerTradeFeeSettingClient;
    @Autowired
    private ExchangeContractService exchangeContractService;

    @Override
    public BrokerAccountTradeFeeGroupDTO getBrokerAccountTradeFeeGroup(Long brokerId, Long groupId) {
        GetBrokerAccountTradeFeeGroupRequest request = GetBrokerAccountTradeFeeGroupRequest.newBuilder()
                .setBrokerId(brokerId).setGroupId(groupId).build();
        GetBrokerAccountTradeFeeGroupResponse response = feeClient.getBrokerAccountTradeFeeGroup(request);

        BrokerAccountTradeFeeGroup group = response.getBrokerAccountTradeFeeGroup();
        if (group.getId() == 0) {
            return null;
        }
        BrokerAccountTradeFeeGroupDTO dto = new BrokerAccountTradeFeeGroupDTO();
        BeanUtils.copyProperties(group, dto);
        dto.setStatus(group.getStatus() == 1 ? true : false);
        dto.setMakerFeeRateAdjust(new BigDecimal(group.getMakerFeeRateAdjust()));
        dto.setTakerRewardToMakerRateAdjust(new BigDecimal(group.getTakerRewardToMakerRateAdjust()));
        dto.setTakerFeeRateAdjust(new BigDecimal(group.getTakerFeeRateAdjust()));
        dto.setAccountIds(String.join(",", group.getAccountIdList().stream().map(a -> a + "")
                .collect(Collectors.toList())));
        dto.setGroupId(group.getId());
        return dto;
    }

    @Override
    public List<BrokerAccountTradeFeeGroupDTO> getBrokerAccountTradeFeeGroups(Long brokerId) {
        GetBrokerAccountTradeFeeGroupsRequest request = GetBrokerAccountTradeFeeGroupsRequest.newBuilder().setBrokerId(brokerId).build();
        GetBrokerAccountTradeFeeGroupsResponse response = feeClient.getBrokerAccountTradeFeeGroups(request);
        List<BrokerAccountTradeFeeGroup> list = response.getBrokerAccountTradeFeeGroupList();
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        List<BrokerAccountTradeFeeGroupDTO> dtos = list.stream().map(group -> {
            BrokerAccountTradeFeeGroupDTO dto = new BrokerAccountTradeFeeGroupDTO();
            BeanUtils.copyProperties(group, dto);
            dto.setStatus(group.getStatus() == 1 ? true : false);
            dto.setMakerFeeRateAdjust(new BigDecimal(group.getMakerFeeRateAdjust()));
            dto.setTakerRewardToMakerRateAdjust(new BigDecimal(group.getTakerRewardToMakerRateAdjust()));
            dto.setTakerFeeRateAdjust(new BigDecimal(group.getTakerFeeRateAdjust()));
            dto.setGroupId(group.getId());
            return dto;
        }).collect(Collectors.toList());
        return dtos;
    }

    @Override
    public BigDecimal getMinMakerBonusRate(Long brokerId, List<Long> exchangeIds) {
        BigDecimal min = new BigDecimal(100);
        BaseRequest baseRequest = BaseReqUtil.getBaseRequest(brokerId);
        for (Long exchangeId : exchangeIds) {
            GetBrokerTradeMinFeeRequest request = GetBrokerTradeMinFeeRequest.newBuilder()
                    .setBaseRequest(baseRequest)
                    .setExchangeId(exchangeId).build();
            GetBrokerTradeMinFeeResponse response = brokerTradeFeeSettingClient.getBrokerTradeMinFee(request);
            BigDecimal bonusRate = DecimalUtil.toBigDecimal(response.getMakerBonusRate());
            if (bonusRate.compareTo(min) < 0) {
                min = bonusRate;
            }
        }
        return min;
    }

    @Override
    public ResultModel editBrokerAccountTradeFeeGroup(Long brokerId, BrokerAccountTradeFeeGroupPO po) {

        List<Long> accountIds = Arrays.stream(
                po.getAccountIds().replaceAll(" ", "")
                        .replaceAll("\r\n", "")
                        .replaceAll("\n", "")
                        .split(","))
                .map(str -> Long.parseLong(str.trim())).distinct().collect(Collectors.toList());

        VerifyBrokerAccountRequest verifyBrokerAccountRequest = VerifyBrokerAccountRequest
                .newBuilder().setHeader(Header.newBuilder().setOrgId(brokerId).build())
                .addAllAccountIds(accountIds)
                .build();
        VerifyBrokerAccountResponse verifyResponse = brokerAccountClient.verifyBrokerAccount(verifyBrokerAccountRequest);
        List<SimpleAccount> brokerAccounts = verifyResponse.getAccountsList();
        if (CollectionUtils.isEmpty(brokerAccounts)) {
            String error = String.join(",", accountIds.stream()
                    .map(accountId -> accountId + "")
                    .collect(Collectors.toList()));
            return ResultModel.validateFail("account.trade.fee.accountid.error", error);
        }
        if (brokerAccounts.size() != accountIds.size()) {
            List<Long> brokerAccountIds = brokerAccounts
                    .stream().map(simpleAccount -> simpleAccount.getAccountId()).collect(Collectors.toList());
            List<String> wrongIds = accountIds.stream().filter(accountId -> !brokerAccountIds.contains(accountId))
                    .map(accountId -> accountId + "")
                    .collect(Collectors.toList());
            String error = String.join(",", wrongIds);
            return ResultModel.validateFail("account.trade.fee.accountid.error", error);
        }

        BrokerAccountTradeFeeGroup.Builder group = BrokerAccountTradeFeeGroup.newBuilder();
        if (po.getGroupId() != null && po.getGroupId() > 0) {
            group.setId(po.getGroupId());
            group.setStatus(1);
        } else {
            group.setStatus(0);
        }
        group.setBrokerId(brokerId);
        group.setGroupName(po.getGroupName());
        group.setMakerFeeRateAdjust(po.getMakerFeeRateAdjust().toPlainString());
        group.setTakerRewardToMakerRateAdjust(po.getTakerRewardToMakerRateAdjust().toPlainString());
        group.setTakerFeeRateAdjust(po.getTakerFeeRateAdjust().toPlainString());

        group.addAllAccountId(accountIds);
        group.setAccountCount(accountIds.size());
        BrokerAccountTradeFeeGroup theGroup = group.build();
        EditBrokerAccountTradeFeeGroupRequest request = EditBrokerAccountTradeFeeGroupRequest.newBuilder()
                .setBrokerAccountTradeFeeGroup(theGroup)
                .build();
        EditBrokerAccountTradeFeeGroupResponse response = feeClient.editBrokerAccountTradeFeeGroup(request);
        EditBrokerAccountTradeFeeGroupResponse.Result result = response.getResult();
        if (result.equals(EditBrokerAccountTradeFeeGroupResponse.Result.GROUP_ID_ERROR)) {
            return ResultModel.error("account.trade.fee.group.id.error");
        }
        if (result.equals(EditBrokerAccountTradeFeeGroupResponse.Result.GROUP_EXISTED)) {
            return ResultModel.error("account.trade.fee.group.name.existed");
        }
        if (result.equals(EditBrokerAccountTradeFeeGroupResponse.Result.DISABLE_STATUS)) {
            return ResultModel.error("account.trade.fee.group.disabled");
        }
        if (result.equals(EditBrokerAccountTradeFeeGroupResponse.Result.ACCOUT_ID_EXISTED)) {
            List<String> accounts = response.getExistedInOtherGroupAccountIdList()
                    .stream().map(a -> a.toString()).collect(Collectors.toList());
            return ResultModel.validateFail("account.trade.fee.accountid.existed", String.join(",", accounts));
        }
        //TODO 对bh分组发送

        //added
        List<Long> addedList = response.getAddedAccountIdList();
        updateFeeRateAdjust(brokerId, addedList, theGroup, UpdateSendStatusRequest.OpType.EDIT);
        //modified
        List<Long> modifiedList = response.getModifiedAccountIdList();
        updateFeeRateAdjust(brokerId, modifiedList, theGroup, UpdateSendStatusRequest.OpType.EDIT);
        //deleted
        List<Long> deletedList = response.getDeletedAccountIdList();
        deleteAdjust(brokerId, deletedList, theGroup.getId(), UpdateSendStatusRequest.OpType.EDIT);

        return ResultModel.ok();
    }

    private void updateFeeRateAdjust(Long brokerId, List<Long> accountIds, BrokerAccountTradeFeeGroup group, UpdateSendStatusRequest.OpType opType) {
        if (CollectionUtils.isEmpty(accountIds)) {
            return;
        }
        List<AccountFeeRateAdjust> feeRateAdjusts = accountIds.stream().map(accountId ->
                AccountFeeRateAdjust.newBuilder()
                        .setBrokerId(brokerId)
                        .setAccountId(accountId)
                        .setMakerFeeRateAdjust(group.getMakerFeeRateAdjust())
                        .setTakerFeeRateAdjust(group.getTakerFeeRateAdjust())
                        .setTakerRewardToMakerRateAdjust(group.getTakerRewardToMakerRateAdjust())
                        .build()
        ).collect(Collectors.toList());

        UpdateAccountFeeRateAdjustRequest updateRequest = UpdateAccountFeeRateAdjustRequest
                .newBuilder()
                .setBaseRequest(BaseReqUtil.getBaseRequest(brokerId))
                .addAllAdjusts(feeRateAdjusts).build();
        UpdateAccountFeeRateAdjustResponse updateResponse = feeClient.updateAccountFeeRateAdjust(updateRequest);

        UpdateSendStatusRequest updateSendStatusRequest = UpdateSendStatusRequest
                .newBuilder().setBrokerId(brokerId).setGroupId(group.getId()).setOpType(opType).addAllAccountId(accountIds).build();
        feeClient.updateSendStatus(updateSendStatusRequest);
    }

    @Override
    public void enableBrokerAccountTradeFeeGroup(Long brokerId, Long groupId) {
        EnableBrokerAccountTradeFeeGroupRequest request = EnableBrokerAccountTradeFeeGroupRequest.newBuilder()
                .setBrokerId(brokerId)
                .setGroupId(groupId)
                .build();
        EnableBrokerAccountTradeFeeGroupResponse response = feeClient.enableBrokerAccountTradeFeeGroup(request);
        BrokerAccountTradeFeeGroup group = response.getBrokerAccountTradeFeeGroup();
        List<Long> accountIds = group.getAccountIdList();
        if (CollectionUtils.isEmpty(accountIds)) {
            return;
        }
        updateFeeRateAdjust(brokerId, accountIds, group, UpdateSendStatusRequest.OpType.ENABLE);

    }

    private void deleteAdjust(Long brokerId, List<Long> accountIds, Long groupId, UpdateSendStatusRequest.OpType opType) {
        if (CollectionUtils.isEmpty(accountIds)) {
            return;
        }
        DeleteAccountFeeRateAdjustRequest deleteRequest = DeleteAccountFeeRateAdjustRequest
                .newBuilder()
                .setBrokerId(brokerId)
                .addAllAccountId(accountIds)
                .build();
        DeleteAccountFeeRateAdjustResponse deleteResponse = feeClient.deleteAccountFeeRateAdjust(deleteRequest);

        UpdateSendStatusRequest updateSendStatusRequest = UpdateSendStatusRequest
                .newBuilder().setBrokerId(brokerId).setOpType(opType).setGroupId(groupId).addAllAccountId(accountIds).build();
        feeClient.updateSendStatus(updateSendStatusRequest);
    }

    @Override
    public void disableBrokerAccountTradeFeeGroup(Long brokerId, Long groupId) {
        DisableBrokerAccountTradeFeeGroupRequest request = DisableBrokerAccountTradeFeeGroupRequest.newBuilder()
                .setBrokerId(brokerId)
                .setGroupId(groupId)
                .build();
        DisableBrokerAccountTradeFeeGroupResponse response = feeClient.disableBrokerAccountTradeFeeGroup(request);
        List<Long> accountIds = response.getAccountIdList();
        if (CollectionUtils.isEmpty(accountIds)) {
            return;
        }
        deleteAdjust(brokerId, accountIds, groupId, UpdateSendStatusRequest.OpType.DISABLE);
    }
}
