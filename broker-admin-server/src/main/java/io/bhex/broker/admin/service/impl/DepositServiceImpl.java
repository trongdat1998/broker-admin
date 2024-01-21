package io.bhex.broker.admin.service.impl;

import com.google.common.base.Strings;
import io.bhex.base.account.*;
import io.bhex.base.proto.DecimalUtil;
import io.bhex.base.token.TokenDetail;
import io.bhex.bhop.common.util.BaseReqUtil;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.DepositDTO;
import io.bhex.broker.admin.grpc.client.DepositClient;
import io.bhex.broker.admin.grpc.client.TokenClient;
import io.bhex.broker.admin.service.DepositService;
import io.bhex.broker.grpc.common.Header;
import io.bhex.broker.grpc.statistics.QueryOrgDepositOrderRequest;
import io.bhex.broker.grpc.statistics.QueryOrgDepositOrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.service.impl
 * @Author: ming.xu
 * @CreateDate: 20/11/2018 5:55 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Service
@Slf4j
public class DepositServiceImpl implements DepositService {

    @Autowired
    private DepositClient depositClient;
    @Autowired
    private TokenClient tokenClient;

    /**
     * 对未入账订单执行入账
     */
    @Override
    public ResultModel execReceipt(Long orgId, Long accountId, Long orderId) {

        ReceiptRequest request = ReceiptRequest.newBuilder()
                .setOrgId(orgId)
                .setAccountId(accountId)
                .setOrderId(orderId)
                .build();

        try {
            ReceiptReply reply = depositClient.receipt(request);

        } catch (Exception e) {
            log.warn("exec receipt invoke bh error. {}", orderId, e);
            return ResultModel.error("receipt error");
        }

        return ResultModel.ok();
    }

    @Override
    public List<DepositDTO> queryUnReceipts(Long accountId, Long userId, Long fromId, Long endId,
                                            Integer pageSize, String tokenId, Long startTime, Long endTime, String txId, Long orgId) {

        List<String> tokenIds = new ArrayList<>();
        if (StringUtils.isNotEmpty(tokenId)) {
            tokenIds.add(tokenId);
        }

        GetDepositRecordsRequest request = GetDepositRecordsRequest.newBuilder()
                .setBaseRequest(BaseReqUtil.getBaseRequest(orgId))
                .setAccountId(Optional.ofNullable(accountId).orElse(0L))
                .addAllTokenId(tokenIds)
                .setFromDepositRecordId(Optional.ofNullable(fromId).orElse(0L))
                .setEndDepositRecordId(Optional.ofNullable(endId).orElse(0L))
                .setReceiptType(2)
                .setTxId(txId)
                .setStartTime(Optional.ofNullable(startTime).orElse(0L))
                .setEndTime(Optional.ofNullable(endTime).orElse(0L))
                .setLimit(Optional.ofNullable(pageSize).orElse(0))
                .build();

        DepositRecordList reply = depositClient.queryUnReceiptOrder(request);

        List<DepositRecord> records = reply.getDepositRecordsList();

        List<DepositDTO> orderDtos = records.stream().map(record -> {
            DepositDTO dto = new DepositDTO();
            dto.setOrderId(record.getDepositRecordId());
            dto.setUserId(Strings.isNullOrEmpty(record.getBrokerUserId()) ? 0L : Long.parseLong(record.getBrokerUserId()));
            dto.setAccountId(record.getAccountId());
            dto.setTokenId(record.getTokenId());
            dto.setTokenName(record.getToken().getTokenName());
            dto.setAddress(record.getAddress());
            dto.setFromAddress(record.getFromAddress());
            dto.setTokenQuantity(DecimalUtil.toBigDecimal(record.getQuantity()).toPlainString());
            dto.setTxid(record.getTxId());
            dto.setStatusCode(DepositOrderStatus.forNumber(record.getStatusValue()).name());
            dto.setTime(record.getDepositTime());
            dto.setAddressExt(record.getAddressExt());
            dto.setReceiptType(record.getReceiptType());
            dto.setCannotReceiptReason(record.getCannotReceiptReason());

            return dto;

        }).collect(Collectors.toList());

        return orderDtos;
    }

    @Override
    public List<DepositDTO> queryDepositOrders(Long brokerId, Long userId, Long fromId, Long endId,
                                               Integer pageSize, String tokenId, Long startTime, Long endTime, String address, String txId) {
        Header header = Header.newBuilder()
                .setOrgId(brokerId)
                .setUserId(userId == null ? 0 : userId)
                .build();

        QueryOrgDepositOrderRequest request = QueryOrgDepositOrderRequest.newBuilder()
                .setHeader(header)
                .setLastId(endId)
                .setFromId(fromId)
                .setLimit(pageSize)
                .setTokenId(Strings.nullToEmpty(tokenId))
                .setStartTime(startTime != null ? startTime : 0L)
                .setEndTime(endTime != null ? endTime : 0L)
                .setAddress(Strings.nullToEmpty(address))
                .setTxId(Strings.nullToEmpty(txId))
                .build();

        QueryOrgDepositOrderResponse reply = depositClient.queryOrgDepositOrder(request);
        List<QueryOrgDepositOrderResponse.OrgDepositOrder> ordersList = reply.getDepositOrderList();

        Map<String, Boolean> baasMap = new HashMap<>();
        List<String> tokenIds = ordersList.stream().map(o -> o.getTokenId()).distinct().collect(Collectors.toList());
        List<TokenDetail> tokenDetails = tokenClient.getTokenListByIds(brokerId, tokenIds);
        tokenDetails.forEach(t -> baasMap.put(t.getTokenId(), t.getIsBaas()));

        List<DepositDTO> orderDtos = ordersList.stream().map(order -> {
            DepositDTO dto = new DepositDTO();
            BeanUtils.copyProperties(order, dto);
            dto.setTokenQuantity(order.getQuantity());
            dto.setTime(order.getCreateTime());
            dto.setCreateTime(order.getCreateTime());
            dto.setStatusCode(DepositOrderStatus.forNumber(order.getStatus()).name());
            dto.setIsBaas(baasMap.getOrDefault(order.getTokenId(), false));

            int originReceiptValue = dto.getReceiptType();
            int originReason = dto.getCannotReceiptReason();
            dto.setReceiptType(convertReceiptType(originReceiptValue));
            dto.setCannotReceiptReason(convertCannotReceiptReason(originReceiptValue, originReason));
            return dto;
        }).collect(Collectors.toList());
        return orderDtos;
    }

    /**
     * 充值列表读的是统计库，需要按照平台的接口兼容方式做兼容
     * 参考平台的兼容代码
     */
    private int convertReceiptType(int originReceiptValue) {
        if (originReceiptValue == 0 || originReceiptValue == 1) {
            return originReceiptValue;
        }
        return 2;
    }

    /**
     * 充值列表读的是统计库，需要按照平台的接口兼容方式做兼容
     * 参考平台的兼容代码
     */
    private int convertCannotReceiptReason(int originReceiptValue, int originReason) {
        int reason;
        switch (originReceiptValue) {
            case 2:
                reason = originReason;
                break;
            case 3:
                reason = 4;
                break;
            case 4:
                reason = 3;
                break;
            case 5:
                reason = 2;
                break;
            case 6:
                reason = 1;
                break;
            default:
                reason = 0;
        }
        return reason;
    }

}
