package io.bhex.broker.admin.controller;


import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.BrokerUserDTO;
import io.bhex.broker.admin.controller.dto.TradeDetailInfoDTO;
import io.bhex.broker.admin.controller.param.*;
import io.bhex.broker.admin.grpc.client.BrokerUserClient;
import io.bhex.broker.admin.grpc.client.OrderClient;
import io.bhex.broker.admin.grpc.client.TransferClient;
import io.bhex.broker.admin.service.UserVerifyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/org_api/v1/")
public class BrokerApiController {

    @Autowired
    private OrderClient orderClient;

    @Autowired
    private TransferClient transferClient;

    @Autowired
    private BrokerUserClient brokerUserClient;

    @Autowired
    private UserVerifyService userVerifyService;

    /**
     * 批量空投
     *
     * @param clientOrderId
     * @param businessType
     * @param token
     * @param transfers
     * @return
     */
    @RequestMapping(value = "/batch/transfer", method = RequestMethod.POST)
    public ResultModel batchTransfer(HttpServletRequest request,
                                     @RequestParam(required = true) Long clientOrderId,
                                     @RequestParam(required = true) Integer businessType,
                                     @RequestParam(required = true) String token,
                                     @RequestParam(required = true) String transfers) {

        if (clientOrderId == null || clientOrderId == 0) {
            ResultModel.error("Client order id can not be empty");
        }

        if (businessType != null || businessType == 0) {
            ResultModel.error("Business type can not be empty");
        }

        if (StringUtils.isEmpty(token)) {
            ResultModel.error("Token can not be empty");
        }

        if (StringUtils.isEmpty(transfers)) {
            ResultModel.error("Transfers can not be empty");
        }
        Long orgId = (Long) request.getAttribute("orgId");
        BatchTransferPO batchTransfer = new BatchTransferPO();
        batchTransfer.setToken(token);
        batchTransfer.setClientOrderId(clientOrderId);
        batchTransfer.setBusinessType(businessType);
        batchTransfer.setTransfers(transfers);
        return ResultModel.ok(transferClient.batchTransfer(batchTransfer, orgId));
    }

    /**
     * 空投+lock
     *
     * @param request
     * @param userId
     * @param amount
     * @param clientOrderId
     * @param businessType
     * @param token
     * @param lock
     * @return
     */
    @RequestMapping(value = "/transfer", method = RequestMethod.POST)
    public ResultModel transferAddLock(
            HttpServletRequest request,
            @RequestParam(required = true) Long userId,
            @RequestParam(required = true) String amount,
            @RequestParam(required = true) Long clientOrderId,
            @RequestParam(required = true) Integer businessType,
            @RequestParam(required = true) String token,
            @RequestParam(required = true, defaultValue = "0") Integer lock) {
        if (userId == null || userId <= 0) {
            return ResultModel.error("User id can not be empty");
        }

        if (StringUtils.isEmpty(amount) || new BigDecimal(amount).compareTo(BigDecimal.ZERO) <= 0) {
            return ResultModel.error("Amount can not be empty");
        }

        if (clientOrderId == null || clientOrderId <= 0) {
            return ResultModel.error("Client order id can not be empty");
        }

        if (businessType == null || businessType <= 0) {
            return ResultModel.error("Business type can not be empty");
        }

        if (lock == null || lock < 0 || lock > 1) {
            return ResultModel.error("Lock can not be empty");
        }

        if (StringUtils.isEmpty(token)) {
            return ResultModel.error("Token can not be empty");
        }

        Long orgId = (Long) request.getAttribute("orgId");
        LockTransferPO lockTransfer = new LockTransferPO();
        lockTransfer.setAmount(amount);
        lockTransfer.setBusinessType(businessType);
        lockTransfer.setClientOrderId(clientOrderId);
        lockTransfer.setLock(lock);
        lockTransfer.setToken(token);
        lockTransfer.setUserId(userId);
        return ResultModel.ok(transferClient.transferAddLock(lockTransfer, orgId));
    }

    /**
     * 券商交易明细
     *
     * @param baseTokenId
     * @param quoteTokenId
     * @param lastTradeId
     * @param startTime
     * @param endTime
     * @param limit
     * @return
     */
    @RequestMapping(value = "/broker_trades", method = RequestMethod.POST)
    public ResultModel<List<TradeDetailInfoDTO>> getTrades(
            HttpServletRequest request,
            @RequestParam(required = true) String baseTokenId,
            @RequestParam(required = true) String quoteTokenId,
            @RequestParam(required = false) Long lastTradeId,
            @RequestParam(required = true) Long startTime,
            @RequestParam(required = true) Long endTime,
            @RequestParam(required = false, defaultValue = "20") Integer limit) {

        if (StringUtils.isEmpty(baseTokenId) || StringUtils.isEmpty(quoteTokenId)) {
            return ResultModel.error("Base token id or quote token id can not be empty");
        }

        if (startTime == null || startTime == 0) {
            return ResultModel.error("Start time can not be empty");
        }

        if (endTime == null || endTime == 0) {
            return ResultModel.error("End time or end time can not be empty");
        }

        if (limit > 500) {
            return ResultModel.error("Limit must less than 500");
        }

        Long orgId = (Long) request.getAttribute("orgId");
        String symbolId = StringUtils.isAnyEmpty(quoteTokenId, baseTokenId)
                ? null
                : baseTokenId + quoteTokenId;
        Long tradeId = lastTradeId == null || lastTradeId < 0 ? 0 : lastTradeId;
        List<TradeDetailInfoDTO> trades = orderClient.getTradesDetailDesc(
                orgId,
                tradeId,
                symbolId,
                0L,
                0L,
                startTime,
                endTime,
                limit);
        if (CollectionUtils.isEmpty(trades)) {
            return ResultModel.ok(new ArrayList<>());
        }
        return ResultModel.ok(trades);
    }

    /**
     * 映射 闪兑
     *
     * @param request
     * @param sourceToken
     * @param sourceAmount
     * @param sourceClientOrderId
     * @param targetUserId
     * @param targetToken
     * @param targetAmount
     * @param lock
     * @param targetClientOrderId
     * @param businessType
     * @return
     */
    @RequestMapping(value = "/mapping", method = RequestMethod.POST)
    public ResultModel mapping(HttpServletRequest request,
                               @RequestParam(required = true) String sourceToken,
                               @RequestParam(required = true) String sourceAmount,
                               @RequestParam(required = true) Long sourceClientOrderId,
                               @RequestParam(required = true) Long targetUserId,
                               @RequestParam(required = true) String targetToken,
                               @RequestParam(required = true) String targetAmount,
                               @RequestParam(required = true, defaultValue = "0") Integer lock,
                               @RequestParam(required = true) Long targetClientOrderId,
                               @RequestParam(required = true) Integer businessType) {
        if (StringUtils.isEmpty(sourceToken) || StringUtils.isEmpty(sourceAmount)
                || StringUtils.isEmpty(targetToken) || StringUtils.isEmpty(targetAmount)) {
            return ResultModel.error("Token or Amount can not be empty");
        }

        if(new BigDecimal(sourceAmount).compareTo(BigDecimal.ZERO) <= 0){
            return ResultModel.error("Source amount can not be empty");
        }

        if(new BigDecimal(targetAmount).compareTo(BigDecimal.ZERO) <= 0){
            return ResultModel.error("Target amount can not be empty");
        }

        if (sourceClientOrderId == null || sourceClientOrderId <= 0) {
            return ResultModel.error("Source client order id can not be empty");
        }

        if (targetClientOrderId == null || targetClientOrderId <= 0) {
            return ResultModel.error("Target client order id can not be empty");
        }

        if (targetUserId == null || targetUserId <= 0) {
            return ResultModel.error("Target user id can not be empty");
        }

        if (lock == null || lock < 0 || lock > 1) {
            return ResultModel.error("Lock can not be empty");
        }

        if (businessType == null || businessType <= 0) {
            return ResultModel.error("Business type can not be empty");
        }

        Long orgId = (Long) request.getAttribute("orgId");
        MappingPo mapping = new MappingPo();
        mapping.setSourceToken(sourceToken);
        mapping.setSourceAmount(sourceAmount);
        mapping.setSourceClientOrderId(sourceClientOrderId);
        mapping.setTargetUserId(targetUserId);
        mapping.setTargetToken(targetToken);
        mapping.setTargetAmount(targetAmount);
        mapping.setTargetClientOrderId(targetClientOrderId);
        mapping.setIsLock(lock);
        mapping.setBusinessType(businessType);
        return transferClient.mapping(mapping, orgId);
    }

    /**
     * 获取用户锁仓信息
     *
     * @param request
     * @param userId
     * @param token
     * @return
     */
    @RequestMapping(value = "/lock/position", method = RequestMethod.POST)
    public ResultModel getLockPosition(
            HttpServletRequest request,
            @RequestParam(required = true) Long userId,
            @RequestParam(required = true) String token) {

        if (StringUtils.isEmpty(token)) {
            return ResultModel.error("Token can not be empty");
        }

        if (userId == null || userId <= 0) {
            return ResultModel.error("User id can not be empty");
        }

        Long orgId = (Long) request.getAttribute("orgId");
        GetPositionPo getPositionPo = new GetPositionPo();
        getPositionPo.setTokenId(token);
        getPositionPo.setUserId(userId);
        return transferClient.getLockPosition(getPositionPo, orgId);
    }

    /**
     * 获取用户信息
     *
     * @param userId userId
     * @return resultModel
     */
    @RequestMapping(value = "/user/info", method = RequestMethod.POST)
    public ResultModel getUserInfo(HttpServletRequest request,
                                   @RequestParam(required = true) Long userId) {
        if (userId == null || userId == 0) {
            return ResultModel.error("User id can not be empty");
        }
        return brokerUserClient.getBrokerUserInfo(userId);
    }


    /**
     * 获取用户最新更新列表
     *
     * @param request
     * @param startTime
     * @param endTime
     * @param fromId
     * @param endId
     * @param limit
     * @return
     */
    @RequestMapping(value = "/brokeruser/changed", method = RequestMethod.POST)
    public ResultModel listUpdateUserByDate(HttpServletRequest request,
                                            @RequestParam(required = true) Long startTime,
                                            @RequestParam(required = true) Long endTime,
                                            @RequestParam(required = false) Long fromId,
                                            @RequestParam(required = false) Long endId,
                                            @RequestParam(required = false, defaultValue = "20") Integer limit) {
        if (startTime == null || startTime == 0) {
            return ResultModel.error("Start time  can not be empty");
        }

        if (endTime == null || endTime == 0) {
            return ResultModel.error("End time can not be empty");
        }
        Long orgId = (Long) request.getAttribute("orgId");

        log.info("orgId {}", orgId);
        ListUpdateUserByDatePO listUpdateUserByDatePO = new ListUpdateUserByDatePO();
        listUpdateUserByDatePO.setBrokerId(orgId);
        listUpdateUserByDatePO.setStartTime(startTime);
        listUpdateUserByDatePO.setEndTime(endTime);
        listUpdateUserByDatePO.setFromId(fromId);
        listUpdateUserByDatePO.setEndId(endId);
        listUpdateUserByDatePO.setLimit(limit);
        List<BrokerUserDTO> brokerUserDTOS = userVerifyService.listUpdateUserByDate(listUpdateUserByDatePO);
        return ResultModel.ok(brokerUserDTOS);
    }

    /**
     * 解锁锁仓
     *
     * @param request
     * @param clientOrderId
     * @param token
     * @param userId
     * @param unlockAmount
     * @param unlockReason
     * @return
     */
    @RequestMapping(value = "/unlock", method = RequestMethod.POST)
    public ResultModel unlockBalance(HttpServletRequest request,
                                     @RequestParam(required = true) Long clientOrderId,
                                     @RequestParam(required = true) String token,
                                     @RequestParam(required = true) Long userId,
                                     @RequestParam(required = true) String unlockAmount,
                                     @RequestParam(required = true) String unlockReason) {

        if (StringUtils.isEmpty(token)) {
            ResultModel.error("Token can not be empty");
        }

        if (userId == null || userId <= 0) {
            return ResultModel.error("User id can not be empty");
        }

        if (StringUtils.isEmpty(unlockAmount)) {
            return ResultModel.error("Unlock amount can not be empty");
        }

        if(new BigDecimal(unlockAmount).compareTo(BigDecimal.ZERO) <= 0){
            return ResultModel.error("Unlock amount can not be empty");
        }

        if (clientOrderId == null || clientOrderId <= 0) {
            return ResultModel.error("Client order id can not be empty");
        }

        if (StringUtils.isEmpty(unlockReason)) {
            return ResultModel.error("Unlock reason can not be empty");
        }

        Long orgId = (Long) request.getAttribute("orgId");
        UnlockBalancePO unlockBalance = new UnlockBalancePO();
        unlockBalance.setUserId(userId);
        unlockBalance.setClientOrderId(clientOrderId);
        unlockBalance.setToken(token);
        unlockBalance.setUnlockAmount(unlockAmount);
        unlockBalance.setUnlockReason(unlockReason);
        return transferClient.unlockBalance(unlockBalance, orgId);
    }
}


