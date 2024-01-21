package io.bhex.broker.admin.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.protobuf.TextFormat;
import io.bhex.base.account.*;
import io.bhex.base.admin.common.BusinessLog;
import io.bhex.base.proto.Decimal;
import io.bhex.base.proto.DecimalUtil;
import io.bhex.base.quote.Rate;
import io.bhex.base.token.TokenDetail;
import io.bhex.bhop.common.config.LocaleMessageService;
import io.bhex.bhop.common.grpc.client.BusinessLogClient;
import io.bhex.bhop.common.grpc.client.MessagePushClient;
import io.bhex.bhop.common.service.AdminUserNameService;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.aspect.WithdrawHandleAnnotation;
import io.bhex.broker.admin.constants.OpTypeConstant;
import io.bhex.broker.admin.controller.dto.AskWalletAddressDTO;
import io.bhex.broker.admin.controller.dto.WithdrawOrderDTO;
import io.bhex.broker.admin.controller.param.WithdrawOrderUnverifyListRes;
import io.bhex.broker.admin.controller.param.WithdrawOrderVerifyListRes;
import io.bhex.broker.admin.grpc.client.*;
import io.bhex.broker.admin.service.WithdrawOrderService;
import io.bhex.broker.grpc.admin.QueryVerfiedOrdersResponse;
import io.bhex.broker.grpc.admin.QueryWithdrawOrderResponse;
import io.bhex.broker.grpc.admin.VerfiedWithdrawOrder;
import io.bhex.broker.grpc.admin.VerifyOrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Date: 2018/9/20 下午4:22
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Slf4j
@Service
public class WithdrawOrderServiceImpl implements WithdrawOrderService {
    @Autowired
    private AdminUserNameService adminUserNameService;
    @Autowired
    private WithdrawOrderClient withdrawOrderClient;
    @Autowired
    private TokenClient tokenClient;
    @Autowired
    private MessagePushClient messagePushClient;
    @Autowired
    private BrokerUserClient brokerUserClient;
    @Autowired
    private BrokerClient brokerClient;
    @Autowired
    private LocaleMessageService localeMessageService;
    @Autowired
    private DepositClient depositClient;
    @Resource
    private BusinessLogClient businessLogClient;
    @Resource
    private RateService rateService;

    @WithdrawHandleAnnotation
    @Override
    public List<WithdrawOrderUnverifyListRes> queryUnverfiedOrders(Long brokerId, Long accountId, Long fromId, Long endId, Integer pageSize) {
        GetBrokerAuditingResponse response = withdrawOrderClient
                .queryUnverfiedOrdersFromBh(brokerId, accountId, fromId, endId, pageSize);
        List<WithdrawalOrderDetail> orders = response.getWithdrawOrderDetailsList();
        if (CollectionUtils.isEmpty(orders)) {
            return new ArrayList<>();
        }
        List<String> tokens = orders.stream().map(t -> t.getToken().getTokenId()).distinct().collect(Collectors.toList());
        Map<String, Rate> ratesMap = rateService.getRate(brokerId, tokens);

        List<WithdrawOrderUnverifyListRes> resList = orders.stream().map(order -> {
            WithdrawOrderUnverifyListRes res = new WithdrawOrderUnverifyListRes();
            BeanUtils.copyProperties(order, res);
            res.setCreated(order.getWithdrawalTime());
            res.setTokenId(order.getToken().getTokenId());
            res.setUserId(StringUtils.isEmpty(order.getBrokerUserId()) ? 0L : Long.parseLong(order.getBrokerUserId()));
            res.setQuantity(new BigDecimal(order.getTotalQuantity()));
            if (res.getTokenId().equals("USDT")) {
                res.setQuantityInUsdt(res.getQuantity());
            } else if (ratesMap != null && ratesMap.containsKey(res.getTokenId())) {
                Rate rate = ratesMap.get(res.getTokenId());
                Decimal usdtRate = rate.getRatesMap().get("USDT");
                if (Objects.nonNull(usdtRate)) {
                    res.setQuantityInUsdt(res.getQuantity().multiply(DecimalUtil.toBigDecimal(usdtRate)));
                }
            } else {
                res.setQuantityInUsdt(BigDecimal.ZERO);
            }

            res.setArrivalQuantity(DecimalUtil.toBigDecimal(order.getArriveQuantity()));
            res.setWithdrawOrderId(order.getWithdrawalOrderId());
            res.setFailedReason(order.getFailedReason());
            res.setFailedReasonDesc(getFailedReasonDesc(order.getStatusValue(), order.getFailedReason(), ""));
            return res;
        }).collect(Collectors.toList());


        return resList;
    }

    private String getFailedReasonDesc(Integer orderStatus, Integer failedreason, String refuseReason) {
        if (orderStatus == WithdrawalStatus.AUDIT_REJECT_STATUS_VALUE
                || orderStatus == WithdrawalStatus.BROKER_REJECT_STATUS_VALUE) {
            if (failedreason.equals(106)) {
                // 自定义审批拒绝原因
                return refuseReason;
            }
            return localeMessageService.getMessage("withdraw.failedreason." + failedreason, null);
        } else {
            return "";
        }
    }


//    public List<WithdrawOrderUnverifyListRes> queryUnverfiedOrders(Long brokerId, Long accountId, Long fromId, Long endId, Integer pageSize) {
//        QueryUnverfiedOrdersResponse response = withdrawOrderClient
//                .queryUnverfiedOrders(brokerId, accountId, fromId, endId, pageSize);
//        List<UnverfiedWithdrawOrder> orders = response.getOrdersList();
//        if (CollectionUtils.isEmpty(orders)) {
//            return new ArrayList<>();
//        }
//
//        List<WithdrawOrderUnverifyListRes> resList = orders.stream().map(order -> {
//            WithdrawOrderUnverifyListRes res = new WithdrawOrderUnverifyListRes();
//            BeanUtils.copyProperties(order, res);
//            res.setWithdrawOrderId(order.getId());
//            return res;
//        }).collect(Collectors.toList());
//
//
//        return resList;
//    }


    @Override
    public ResultModel verify(Long brokerId, Long userId, Long accountId, Long withdrawOrderId, boolean verifyPassed
            , String remark, String adminUserName, Integer failedReason, String refuseReason) {

        failedReason = failedReason == null ? 0 : failedReason;
        remark = StringUtils.isEmpty(remark) ? "" : remark;
        refuseReason = StringUtils.isEmpty(refuseReason) ? "" : refuseReason;

        QueryWithdrawOrderResponse orderResponse = withdrawOrderClient.queryWithdrawOrder(brokerId, withdrawOrderId);
        log.info("order response:{}", TextFormat.shortDebugString(orderResponse));
        if (orderResponse.getId() == 0) {
            return ResultModel.validateFail("not.found", "withdraw.verify.info");
        }

        if (orderResponse.getVerifyStatus() == WithdrawalBrokerAuditEnum.PASS_VALUE
                || orderResponse.getVerifyStatus() == WithdrawalBrokerAuditEnum.NO_PASS_VALUE) {
            return ResultModel.error("withdraw.already.verified");
        }
        if (orderResponse.getVerifyStatus() == WithdrawalBrokerAuditEnum.NO_NEED_VALUE) {
            return ResultModel.error("withdraw.verify.no.need");
        }

        SetWithdrawalAuditStatusResponse auditResponse = withdrawOrderClient
                .setWithdrawalAuditStatus(brokerId, accountId, withdrawOrderId, verifyPassed, failedReason);
        log.info("call bh.auditStatus response:{}", auditResponse);

        if (auditResponse.getWithdrawalOrderId() < 1) {
            return ResultModel.error("");
        }
        if (auditResponse.getResult() == WithdrawalResult.COLD_WALLET_BALANCE_INSUFFICIENT) {
            return ResultModel.error("withdraw.verify.cold_wallet_balance_insufficient");
        }

        VerifyOrderResponse response = withdrawOrderClient.verify(brokerId, userId, withdrawOrderId, verifyPassed,
                remark, adminUserName, failedReason, refuseReason);
        log.info("VerifyOrderRequest result = {}", response);
        if (response.getRet() != 0) {
            return ResultModel.validateFail(response.getMsg(), "withdraw.verify.info");
        }

        if (verifyPassed) {
            return ResultModel.ok();
        }

        //提现审核拒绝要发送通知
//        BrokerUserDTO dto = brokerUserClient.getBorkerUser(brokerId, userId);
//        if (dto == null) {
//            return ResultModel.ok();
//        }
//
//        String lang = orderResponse.getLanguage();
//        Locale locale = LocaleContextHolder.getLocale();
//        if (!StringUtils.isEmpty(lang) && lang.contains("_")) {
//            locale = new Locale(lang.split("_")[0], lang.split("_")[1]);
//        }
//
//        if (!StringUtils.isEmpty(dto.getEmail())) {
//            String content = localeMessageService.getMessage(locale, "email.withdraw.verify.rejected.content", null);
//
//            String senderName = orgInstanceConfig.getBrokerInstance(brokerId).getBrokerName();
//            String subject = senderName;
//            messagePushClient.sendMailDirectly(brokerId, dto.getRealEmail(), subject, senderName, content, locale.toString());
//            log.info("send email(user verify notify)");
//        }
//
//        if (!StringUtils.isEmpty(dto.getNationalCode()) && !StringUtils.isEmpty(dto.getMobile())) {
//            String content = localeMessageService.getMessage(locale, "sms.withdraw.verify.rejected.content", null);
//            BrokerDetail brokerDetail = brokerClient.getByBrokerId(brokerId);
//            messagePushClient.sendSmsDirectly(brokerId, dto.getNationalCode(), dto.getRealMobile(), content, brokerDetail.getSignName(), locale.toString());
//            log.info("send sms(withdraw verify rejected)");
//        }


        return ResultModel.ok();
    }

    @Override
    public List<WithdrawOrderVerifyListRes> queryVerfiedOrders(Long brokerId, Long accountId, Long fromId, Long endId,
                                                               Integer pageSize) {
        QueryVerfiedOrdersResponse response = withdrawOrderClient.queryVerfiedOrders(brokerId, accountId, fromId, endId, pageSize);
        List<VerfiedWithdrawOrder> orders = response.getOrdersList();
        if (CollectionUtils.isEmpty(orders)) {
            return new ArrayList<>();
        }

        io.bhex.base.admin.common.QueryLogsRequest.Builder builder = io.bhex.base.admin.common.QueryLogsRequest.newBuilder()
                .setOpType(OpTypeConstant.WITHDRAW_ORDER_VERIFY)
                .setOrgId(brokerId)
                .setWithRequestInfo(true)
                .addAllEntityIds(orders.stream().map(o -> o.getBhOrderId() + "").collect(Collectors.toList()));
        List<BusinessLog> logs = businessLogClient.queryLogs(builder.build());
        Map<String, String> verifyTimeMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(logs)) {
            logs.forEach(l -> {
                JSONObject jo = JSONObject.parseObject(l.getRequestInfo());
                verifyTimeMap.put(l.getEntityId(), jo.containsKey("remark") ? Strings.nullToEmpty(jo.get("remark").toString()) : "");
            });
        }

        List<WithdrawOrderVerifyListRes> resList = orders.stream().map(order -> {
            WithdrawOrderVerifyListRes res = new WithdrawOrderVerifyListRes();
            BeanUtils.copyProperties(order, res);
            res.setWithdrawOrderId(order.getId());
            res.setAdminUserName(adminUserNameService.getAdminName(brokerId, order.getAdminUserName()));
            res.setRemark(verifyTimeMap.getOrDefault(order.getBhOrderId() + "", ""));
            res.setFailedReason(order.getFailedReason());
            res.setFailedReasonDesc(getFailedReasonDesc(order.getVerifyStatus(), order.getFailedReason(), order.getRefuseReason()));
            return res;
        }).collect(Collectors.toList());
        return resList;
    }

    @Override
    public List<WithdrawOrderDTO> queryWithdrawOrder(Long brokerId, Long accountId, Long fromId, Long endId,
                                                     Integer pageSize, String tokenId, Long startTime, Long endTime, String address, String txId) {


        io.bhex.base.account.QueryOrdersRequest request = io.bhex.base.account.QueryOrdersRequest.newBuilder()
                .setAccountId(accountId)
                .setOrgId(brokerId)
                .setFromOrderId(fromId)
                .setLimit(pageSize)
                .setTokenId(Strings.nullToEmpty(tokenId))
                .setStartTime(startTime != null ? startTime : 0L)
                .setEndTime(endTime != null ? endTime : 0L)
                .setAddress(Strings.nullToEmpty(address))
                .setTxId(Strings.nullToEmpty(txId))
                .build();


        QueryOrdersReply reply = withdrawOrderClient.queryWithdrawOrderFromBh(request);
        List<WithdrawalOrderDetail> ordersList = reply.getOrdersList();

        Map<String, Boolean> baasMap = new HashMap<>();
        Map<String, Long> verifyTimeMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(ordersList)) {
            io.bhex.base.admin.common.QueryLogsRequest.Builder builder = io.bhex.base.admin.common.QueryLogsRequest.newBuilder()
                    .setOpType(OpTypeConstant.WITHDRAW_ORDER_VERIFY)
                    .setOrgId(brokerId)
                    .setWithRequestInfo(false)
                    .addAllEntityIds(ordersList.stream().map(o -> o.getWithdrawalOrderId() + "").collect(Collectors.toList()));
            List<BusinessLog> logs = businessLogClient.queryLogs(builder.build());
            if (!CollectionUtils.isEmpty(logs)) {
                logs.forEach(l -> verifyTimeMap.put(l.getEntityId(), l.getCreated()));
            }

            List<String> tokenIds = ordersList.stream().map(o -> o.getToken().getTokenId()).distinct().collect(Collectors.toList());
            List<TokenDetail> tokenDetails = tokenClient.getTokenListByIds(brokerId, tokenIds);
            tokenDetails.forEach(t -> baasMap.put(t.getTokenId(), t.getIsBaas()));
        }


        List<WithdrawOrderDTO> orderDtos = ordersList.stream().map(order -> {
            WithdrawOrderDTO dto = new WithdrawOrderDTO();
            dto.setOrderId(order.getWithdrawalOrderId());
            dto.setUserId(StringUtils.isEmpty(order.getBrokerUserId()) ? 0L : Long.parseLong(order.getBrokerUserId()));
            dto.setAccountId(order.getAccountId());
            dto.setTokenId(order.getToken().getTokenId());
            dto.setTokenName(order.getToken().getTokenName());
            dto.setAddress(order.getAddress());
            dto.setTokenQuantity(order.getTotalQuantity());
            dto.setArriveQuantity(DecimalUtil.toTrimString(order.getArriveQuantity()));
            dto.setStatusCode(order.getStatus().name());
            dto.setStatusDesc(order.getStatus().name());
            dto.setTime(order.getWithdrawalTime());
            dto.setVerifyTime(verifyTimeMap.getOrDefault(order.getWithdrawalOrderId() + "", 0L));
            dto.setCreateTime(order.getWithdrawalTime());
            dto.setWalletHandleTime(order.getWalletHandleTime());
            dto.setAddressExt(order.getAddressExt());
            dto.setTxid(order.getTxId());
            dto.setIsChainWithdraw(order.getIsChainWithdraw());
            dto.setIsBaas(baasMap.getOrDefault(order.getToken().getTokenId(), false));
            dto.setChainType(order.getChainType());
            return dto;
        }).collect(Collectors.toList());
        return orderDtos;
    }


    @Override
    public AskWalletAddressDTO askWalletAddress(String tokenId, String address, String memo, Long orgId) {

        try {
            AskWalletAddressReply reply = depositClient.askWalletAddress(
                    AskWalletAddressRequest.newBuilder()
                            .setOrgId(orgId)
                            .setTokenId(tokenId)
                            .setAddress(address)
                            .setMemo(memo)
                            .build()
            );

            log.info("askWalletAddress reply={}", TextFormat.shortDebugString(reply));

            if (!orgId.equals(reply.getAddressOrgId())) {
                return new AskWalletAddressDTO(reply.getIsWalletAddress(), "", 0L);
            }

            return new AskWalletAddressDTO(reply.getIsWalletAddress(), reply.getBrokerUserId(), reply.getAccountId());
        } catch (Exception e) {
            return null;
        }
    }
}
