package io.bhex.broker.admin.service.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import io.bhex.broker.admin.controller.dto.MatchDTO;
import io.bhex.broker.admin.controller.dto.OrderDTO;
import io.bhex.broker.admin.controller.dto.OrderMatchFeeDTO;
import io.bhex.broker.admin.controller.dto.PositionDto;
import io.bhex.broker.admin.controller.dto.SettlementDto;
import io.bhex.broker.admin.grpc.client.impl.GrpcOptionOrderService;
import io.bhex.broker.admin.service.OptionOrderService;
import io.bhex.broker.grpc.order.MatchInfo;
import io.bhex.broker.grpc.order.OptionPosition;
import io.bhex.broker.grpc.order.OptionPositionsRequest;
import io.bhex.broker.grpc.order.OptionPositionsResponse;
import io.bhex.broker.grpc.order.OptionSettlement;
import io.bhex.broker.grpc.order.OptionSettlementRequest;
import io.bhex.broker.grpc.order.OptionSettlementResponse;
import io.bhex.broker.grpc.order.Order;
import io.bhex.broker.grpc.order.OrderQueryType;
import io.bhex.broker.grpc.order.OrderSide;
import io.bhex.broker.grpc.order.OrderStatus;
import io.bhex.broker.grpc.order.OrderType;
import io.bhex.broker.grpc.order.QueryMatchRequest;
import io.bhex.broker.grpc.order.QueryMatchResponse;
import io.bhex.broker.grpc.order.QueryOrdersRequest;
import io.bhex.broker.grpc.order.QueryOrdersResponse;

/**
 * @Author: yuehao  <hao.yue@bhex.com>
 * @CreateDate: 2019-01-31 17:29
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Service
public class OptionOrderServiceImpl implements OptionOrderService {

    @Resource
    private MessageSource messageSource;

    @Resource
    private GrpcOptionOrderService grpcOptionOrderService;

    /**
     * 获取用户期权委托
     *
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
    public List<OrderDTO> queryCurrentOrders(Long orgId, Long userId,
                                             String symbolId, Long fromOrderId, Long endOrderId, Long startTime, Long endTime,
                                             String baseTokenId, String quoteTokenId, String orderType, String orderSide,
                                             Integer limit) {
        QueryOrdersRequest.Builder builder = QueryOrdersRequest.newBuilder()
                .setHeader(io.bhex.broker.grpc.common.Header.newBuilder().setOrgId(orgId).setUserId(userId).build())
                .setAccountId(0L)
                .setSymbolId(Strings.nullToEmpty(symbolId))
                .setFromId(longNullToZero(fromOrderId))
                .setEndId(longNullToZero(endOrderId))
                .setStartTime(longNullToZero(startTime))
                .setEndTime(longNullToZero(endTime))
                .setLimit(limit)
                .setBaseTokenId(Strings.nullToEmpty(baseTokenId))
                .setQuoteTokenId(Strings.nullToEmpty(quoteTokenId))
                .setQueryType(OrderQueryType.CURRENT);
        if (!Strings.isNullOrEmpty(orderType)) {
            builder.setOrderType(OrderType.valueOf(orderType.toUpperCase()));
        }
        if (!Strings.isNullOrEmpty(orderSide)) {
            builder.setOrderSide(OrderSide.valueOf(orderSide.toUpperCase()));
        }
        QueryOrdersResponse response = grpcOptionOrderService.queryOptionOrders(builder.build());
        return Lists.newArrayList(response.getOrdersList()).stream().map(this::getOptionOrderResult).collect(Collectors.toList());
    }


    /**
     * 期权历史委托
     *
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
    public List<OrderDTO> queryHistoryOrders(Long orgId, Long userId,
                                             String symbolId, Long fromOrderId, Long endOrderId, Long startTime, Long endTime,
                                             String baseTokenId, String quoteTokenId, String orderType, String orderSide,
                                             Integer limit, String orderStatus) {
        QueryOrdersRequest.Builder builder = QueryOrdersRequest.newBuilder()
                .setHeader(io.bhex.broker.grpc.common.Header.newBuilder().setOrgId(orgId).setUserId(userId).build())
                .setAccountId(0L)
                .setSymbolId(Strings.nullToEmpty(symbolId))
                .setFromId(longNullToZero(fromOrderId))
                .setEndId(longNullToZero(endOrderId))
                .setStartTime(longNullToZero(startTime))
                .setEndTime(longNullToZero(endTime))
                .setBaseTokenId(Strings.nullToEmpty(baseTokenId))
                .setQuoteTokenId(Strings.nullToEmpty(quoteTokenId))
                .setLimit(limit)
                .setQueryType(OrderQueryType.HISTORY);
        if (!Strings.isNullOrEmpty(orderType)) {
            builder.setOrderType(OrderType.valueOf(orderType.toUpperCase()));
        }
        if (!Strings.isNullOrEmpty(orderSide)) {
            builder.setOrderSide(OrderSide.valueOf(orderSide.toUpperCase()));
        }
        if (!Strings.isNullOrEmpty(orderStatus)) {
            builder.addAllOrderStatus(Arrays.asList(OrderStatus.valueOf(orderStatus.toUpperCase())));
        } else {
            builder.addAllOrderStatus(Arrays.asList(OrderStatus.CANCELED, OrderStatus.FILLED));
        }
        QueryOrdersResponse response = grpcOptionOrderService.queryOptionOrders(builder.build());
        return Lists.newArrayList(response.getOrdersList()).stream().map(this::getOptionOrderResult).collect(Collectors.toList());
    }


    /**
     * 期权历史成交
     *
     * @param symbolId     symbolId
     * @param fromTraderId fromTraderId
     * @param endTradeId   endTradeId
     * @param startTime    startTime
     * @param endTime      endTime
     * @param limit        limit
     * @return list
     */
    public List<MatchDTO> queryMatchInfo(Long orgId, Long userId, String symbolId, Long fromTraderId, Long endTradeId,
                                         Long startTime, Long endTime, Integer limit, String side) {
        side = Strings.nullToEmpty(side);
        QueryMatchRequest request = QueryMatchRequest.newBuilder()
                .setHeader(io.bhex.broker.grpc.common.Header.newBuilder().setOrgId(orgId).setUserId(userId).build())
                .setAccountId(0L)
                .setSymbolId(Strings.nullToEmpty(symbolId))
                .setFromId(longNullToZero(fromTraderId))
                .setEndId(longNullToZero(endTradeId))
                .setStartTime(longNullToZero(startTime))
                .setEndTime(longNullToZero(endTime))
                .setLimit(limit)
                .setOrderSide(StringUtils.isNotEmpty(side.toUpperCase()) ? OrderSide.valueOf(side.toUpperCase()) : OrderSide.UNKNOWN_ORDER_SIDE)
                .build();
        QueryMatchResponse response = grpcOptionOrderService.queryOptionMatchInfo(request);
        return Lists.newArrayList(response.getMatchList()).stream().map(this::getMatchResult).collect(Collectors.toList());
    }


    /**
     * 获取持仓数据
     *
     * @param tokenIds tokenIds
     * @return list
     */
    public List<PositionDto> getOptionPositions(Long orgId, Long userId, String tokenIds, Integer exchangeId,
                                                Long fromBalanceId, Long endBalanceId, Integer limit) {
        OptionPositionsRequest positionsRequest = OptionPositionsRequest
                .newBuilder()
                .setHeader(io.bhex.broker.grpc.common.Header.newBuilder().setOrgId(orgId).setUserId(userId).build())
                .setTokenIds(tokenIds != null ? tokenIds : "")
                .setExchangeId(exchangeId != null ? exchangeId : 0)
                .setFromBalanceId(fromBalanceId != null ? fromBalanceId : 0)
                .setEndBalanceId(endBalanceId != null ? endBalanceId : 0)
                .setLimit(limit)
                .build();
        OptionPositionsResponse response = grpcOptionOrderService.getOptionPositions(positionsRequest);
        List<PositionDto> positionResults = new ArrayList<>();
        response.getOptionPositionList().forEach(s -> positionResults.add(buildPositionResult(s)));
        return positionResults;
    }

    /**
     * 构建positionResult
     *
     * @param optionPositions optionPositions
     * @return positionResult
     */
    private PositionDto buildPositionResult(OptionPosition optionPositions) {
        String symbolName = messageSource.getMessage(optionPositions.getSymbolId(),
                null, "", LocaleContextHolder.getLocale());
        return PositionDto
                .builder()
                .accountId(optionPositions.getAccountId())
                .settlementTime(optionPositions.getSettlementTime())
                .strikePrice(optionPositions.getStrikePrice())
                .margin(optionPositions.getMargin())
                .position(optionPositions.getTotal())
                .averagePrice(optionPositions.getAveragePrice())
                .balanceId(optionPositions.getBalanceId())
                .changedRate(optionPositions.getChangedRate())
                .costPrice(optionPositions.getCostPrice())
                .price(optionPositions.getPrice())
                .availPosition(optionPositions.getAvailPosition())
                .symbolName(symbolName)
                .symbolId(optionPositions.getSymbolId())
                .changed(optionPositions.getChanged())
                .indices(StringUtils.isNotEmpty(optionPositions.getIndices())
                        ? new BigDecimal(optionPositions.getIndices()).toPlainString()
                        : "0.00")
                .baseTokenId(optionPositions.getBaseTokenId())
                .baseTokenName(optionPositions.getBaseTokenName())
                .quoteTokenId(optionPositions.getQuoteTokenId())
                .quoteTokenName(optionPositions.getQuoteTokenName())
                .build();
    }


    /**
     * 获取交割数据
     *
     * @param side             side
     * @param fromSettlementId fromSettlementId
     * @param endSettlementId  endSettlementId
     * @param startTime        startTime
     * @param endTime          endTime
     * @param limit            limit
     * @return list
     */
    public List<SettlementDto> getOptionSettlement(Long orgId, Long userId, String side, Long fromSettlementId,
                                                   Long endSettlementId, Long startTime, Long endTime, Integer limit) {
        OptionSettlementRequest settlementRequest = OptionSettlementRequest
                .newBuilder()
                .setHeader(io.bhex.broker.grpc.common.Header.newBuilder().setOrgId(orgId).setUserId(userId).build())
                .setSide(StringUtils.isNotEmpty(side) ? side : "")
                .setFromSettlementId(fromSettlementId != null ? fromSettlementId : 0L)
                .setEndSettlementId(endSettlementId != null ? endSettlementId : 0L)
                .setStartTime(startTime != null ? startTime : 0L)
                .setEndTime(endTime != null ? endTime : 0L)
                .setLimit(limit)
                .build();

        OptionSettlementResponse response = grpcOptionOrderService.getOptionSettlement(settlementRequest);
        List<SettlementDto> settlementResults = new ArrayList<>();
        response.getOptionSettlementList().forEach(s -> settlementResults.add(buildSettlementResult(s)));
        return settlementResults;
    }

    /**
     * 构造交割数据
     *
     * @param optionSettlements 参数
     * @return SettlementResult
     */
    private SettlementDto buildSettlementResult(OptionSettlement optionSettlements) {
        String symbolName = messageSource.getMessage(optionSettlements.getSymbolId(),
                null, "", LocaleContextHolder.getLocale());
        return SettlementDto
                .builder()
                .accountId(optionSettlements.getAccountId())
                .symbolId(optionSettlements.getSymbolId())
                .symbolName(symbolName)
                .available(optionSettlements.getAvailable())
                .averagePrice(optionSettlements.getAveragePrice())
                .changed(optionSettlements.getChanged())
                .changedRate(optionSettlements.getChangedRate())
                .costPrice(optionSettlements.getCostPrice())
                .margin(optionSettlements.getMargin())
                .maxPayOff(optionSettlements.getMaxPayOff())
                .settlementId(optionSettlements.getSettlementId())
                .settlementTime(optionSettlements.getSettlementTime())
                .settlementPrice(optionSettlements.getSettlementPrice())
                .strikePrice(optionSettlements.getStrikePrice())
                .changed(optionSettlements.getChanged())
                .baseTokenId(optionSettlements.getBaseTokenId())
                .baseTokenName(optionSettlements.getBaseTokenName())
                .quoteTokenId(optionSettlements.getQuoteTokenId())
                .quoteTokenName(optionSettlements.getQuoteTokenName())
                .build();
    }


    private OrderDTO getOptionOrderResult(Order order) {
        String statusCode = order.getStatusCode();
        List<OrderMatchFeeDTO> fees = order.getFeesList().stream()
                .map(fee -> OrderMatchFeeDTO.builder()
                        .feeTokenId(fee.getFeeTokenId())
                        .feeTokenName(fee.getFeeTokenName())
                        .fee(fee.getFee())
                        .build())
                .collect(Collectors.toList());
        String symbolName = messageSource.getMessage(order.getSymbolId(),
                null, "", LocaleContextHolder.getLocale());
        return OrderDTO.builder()
                .accountId(order.getAccountId())
                .orderId(order.getOrderId())
                .clientOrderId(order.getClientOrderId())
                .symbolId(order.getSymbolId())
                .symbolName(symbolName)
                .baseTokenId(order.getBaseTokenId())
                .baseTokenName(order.getBaseTokenName())
                .quoteTokenId(order.getQuoteTokenId())
                .quoteTokenName(order.getQuoteTokenName())
                .price(order.getPrice())
                .origQty(order.getOrigQty())
                .executedQty(order.getExecutedQty())
                .executedAmount(order.getExecutedAmount())
                .avgPrice(order.getAvgPrice())
                .type(order.getOrderType() == OrderType.MARKET ? OrderType.MARKET.name() : OrderType.LIMIT.name())
                .side(order.getOrderSide().name())
                .fees(fees)
                .status(statusCode)
                .time(order.getTime())
                .noExecutedQty(new BigDecimal(order.getOrigQty())
                        .subtract(new BigDecimal(order.getExecutedQty())).toPlainString())
                .amount(new BigDecimal(order.getOrigQty())
                        .multiply(new BigDecimal(order.getPrice())).toPlainString())
                .build();
    }

    private MatchDTO getMatchResult(MatchInfo matchInfo) {
        String symbolName = messageSource.getMessage(matchInfo.getSymbolId(),
                null, "", LocaleContextHolder.getLocale());
        return MatchDTO.builder()
                .accountId(matchInfo.getAccountId())
                .orderId(matchInfo.getOrderId())
                .tradeId(matchInfo.getTradeId())
                .symbolId(matchInfo.getSymbolId())
                .symbolName(symbolName)
                .baseTokenId(matchInfo.getBaseTokenId())
                .baseTokenName(matchInfo.getBaseTokenName())
                .quoteTokenId(matchInfo.getQuoteTokenId())
                .quoteTokenName(matchInfo.getQuoteTokenName())
                .price(matchInfo.getPrice())
                .quantity(matchInfo.getQuantity())
                .feeTokenId(matchInfo.getFee().getFeeTokenId())
                .feeTokenName(matchInfo.getFee().getFeeTokenName())
                .fee(matchInfo.getFee().getFee())
                .side(matchInfo.getOrderSide().name())
                .type(matchInfo.getOrderType() == OrderType.MARKET ? OrderType.MARKET.name() : OrderType.LIMIT.name())
                .time(matchInfo.getTime())
                .executedAmount((new BigDecimal(matchInfo.getPrice())
                        .multiply(new BigDecimal(matchInfo.getQuantity()))
                        .toPlainString()))
                .build();
    }

    private long longNullToZero(Long l) {
        return l == null ? 0L : l;
    }
}
