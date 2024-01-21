package io.bhex.broker.admin.service;

import java.util.List;

import io.bhex.broker.admin.controller.dto.MatchDTO;
import io.bhex.broker.admin.controller.dto.OrderDTO;
import io.bhex.broker.admin.controller.dto.PositionDto;
import io.bhex.broker.admin.controller.dto.SettlementDto;

/**
 * @Author: yuehao  <hao.yue@bhex.com>
 * @CreateDate: 2019-01-31 17:28
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface OptionOrderService {

    /**
     * 获取当前委托
     *
     * @param orgId        orgId
     * @param userId       userId
     * @param symbolId     symbolId
     * @param fromOrderId  fromOrderId
     * @param endOrderId   endOrderId
     * @param startTime    startTime
     * @param endTime      endTime
     * @param baseTokenId  baseTokenId
     * @param quoteTokenId quoteTokenId
     * @param orderType    orderType
     * @param orderSide    orderSide
     * @param limit        limit
     * @return list
     */
    List<OrderDTO> queryCurrentOrders(Long orgId, Long userId,
                                      String symbolId, Long fromOrderId, Long endOrderId, Long startTime, Long endTime,
                                      String baseTokenId, String quoteTokenId, String orderType, String orderSide,
                                      Integer limit);


    /**
     * 获取历史委托
     *
     * @param orgId        orgId
     * @param userId       userId
     * @param symbolId     symbolId
     * @param fromOrderId  fromOrderId
     * @param endOrderId   endOrderId
     * @param startTime    startTime
     * @param endTime      endTime
     * @param baseTokenId  baseTokenId
     * @param quoteTokenId quoteTokenId
     * @param orderType    orderType
     * @param orderSide    orderSide
     * @param limit        limit
     * @param orderStatus  orderStatus
     * @return list
     */
    List<OrderDTO> queryHistoryOrders(Long orgId, Long userId,
                                      String symbolId, Long fromOrderId, Long endOrderId, Long startTime, Long endTime,
                                      String baseTokenId, String quoteTokenId, String orderType, String orderSide,
                                      Integer limit, String orderStatus);

    /**
     * 获取历史成交
     *
     * @param orgId        orgId
     * @param userId       userId
     * @param symbolId     symbolId
     * @param fromTraderId fromTraderId
     * @param endTradeId   endTradeId
     * @param startTime    startTime
     * @param endTime      endTime
     * @param limit        limit
     * @param side         side
     * @return list
     */
    List<MatchDTO> queryMatchInfo(Long orgId, Long userId, String symbolId, Long fromTraderId, Long endTradeId,
                                  Long startTime, Long endTime, Integer limit, String side);


    /**
     * 获取持仓
     *
     * @param orgId         orgId
     * @param userId        userId
     * @param tokenIds      tokenIds
     * @param exchangeId    exchangeId
     * @param fromBalanceId fromBalanceId
     * @param endBalanceId  endBalanceId
     * @param limit         limit
     * @return list
     */
    List<PositionDto> getOptionPositions(Long orgId, Long userId, String tokenIds, Integer exchangeId,
                                         Long fromBalanceId, Long endBalanceId, Integer limit);


    /**
     * 获取交割记录
     *
     * @param orgId            orgId
     * @param userId           userId
     * @param side             side
     * @param fromSettlementId fromSettlementId
     * @param endSettlementId  endSettlementId
     * @param startTime        startTime
     * @param endTime          endTime
     * @param limit            limit
     * @return list
     */
    List<SettlementDto> getOptionSettlement(Long orgId, Long userId, String side, Long fromSettlementId,
                                            Long endSettlementId, Long startTime, Long endTime, Integer limit);
}
