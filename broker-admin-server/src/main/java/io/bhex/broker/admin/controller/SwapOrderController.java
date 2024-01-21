package io.bhex.broker.admin.controller;

import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.util.Combo2;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.*;
import io.bhex.broker.admin.controller.param.*;
import io.bhex.broker.admin.grpc.client.OrderClient;
import io.bhex.broker.admin.service.impl.SwapOrderService;
import io.bhex.broker.admin.util.FuturesUtil;
import io.bhex.broker.admin.util.TimeRangeUtil;
import io.bhex.broker.grpc.common.Header;
import io.bhex.broker.grpc.common.QueryTradeDataType;
import io.bhex.broker.grpc.order.OrderSide;
import io.bhex.broker.grpc.order.OrderType;
import io.bhex.broker.grpc.statistics.QueryOrgTradeDetailRequest;
import io.bhex.broker.grpc.statistics.QueryOrgTradeDetailResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.controller
 * @Author: ming.xu
 * @CreateDate: 2019/9/18 5:26 PM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
@RestController
@RequestMapping("/api/v1/swap/order")
public class SwapOrderController extends BrokerBaseController {

    @Autowired
    SwapOrderService swapOrderService;
    @Autowired
    OrderClient orderClient;

    /**
     * 期货未成交委托
     */
    @RequestMapping(value = "/open_orders")
    public ResultModel querySwapOpenOrders(@RequestBody QuerySwapOrderPO po, AdminUserReply adminUser) {
        Combo2<Long, Long> combo2 = getUserIdAndAccountId(po, getOrgId());
        if (combo2 == null) {
            return ResultModel.ok(new ArrayList<>());
        }
        Long userId = combo2.getV1();

        TimeRange range = TimeRangeUtil.getRange(po.getTimeRange());
        Long startTime = (range != null ? range.getStartTime() : 0L);
        Long endTime = (range != null ? range.getEndTime() : 0L);

        List<SwapOrderDTO> orderResults = swapOrderService.queryCurrentOrders(getOrgId(), userId, po.getFuturesOrderType(),
                po.getSymbolId(), po.getFromOrderId(), po.getEndOrderId(), startTime, endTime, po.getLimit(), po.getProfitLossOrder());
        orderResults = orderResults.stream().filter(o -> adminUser.getOrgId() != 7007 || o.getTime() > System.currentTimeMillis() - 7*24*3600_000).collect(Collectors.toList());

        return ResultModel.ok(orderResults);
    }

    /**
     * 期货当前持仓
     */
    @RequestMapping(value = "/current_orders")
    public ResultModel querySwapPosition(@RequestBody @Valid QuerySwapPositionPO po, AdminUserReply adminUser) {
        Combo2<Long, Long> combo2 = getUserIdAndAccountId(po, getOrgId());
        if (combo2 == null) {
            return ResultModel.ok(new ArrayList<>());
        }
        Long userId = combo2.getV1();
        List<FuturesPositionDTO> list = swapOrderService.getFuturesPositions(getOrgId(), userId, po.getSymbolId(),
                po.getFromPositionId(), po.getEndPositionId(), po.getLimit());

        return ResultModel.ok(list);
    }

    /**
     * 期货历史委托
     */
    @RequestMapping(value = "/trade_orders")
    public ResultModel querySwapTradeOrders(@RequestBody QuerySwapOrderPO po, AdminUserReply adminUser) {
        Combo2<Long, Long> combo2 = getUserIdAndAccountId(po, getOrgId());
        if (combo2 == null) {
            return ResultModel.ok(new ArrayList<>());
        }
        Long userId = combo2.getV1();

        TimeRange range = TimeRangeUtil.getRange(po.getTimeRange());
        Long startTime = (range != null ? range.getStartTime() : 0L);
        Long endTime = (range != null ? range.getEndTime() : 0L);

        List<SwapOrderDTO> orderResults = swapOrderService.queryHistoryOrders(getOrgId(), userId, po.getFuturesOrderType(),
                po.getSymbolId(), po.getFromOrderId(), po.getEndOrderId(), startTime, endTime, po.getLimit(), po.getProfitLossOrder());

        orderResults = orderResults.stream().filter(o -> adminUser.getOrgId() != 7007 || o.getTime() > System.currentTimeMillis() - 7*24*3600_000).collect(Collectors.toList());

        return ResultModel.ok(orderResults);
    }

    /**
     * 期货历史成交
     */

    @RequestMapping(value = "/my_trades")
    public ResultModel queryMySwapTrades(@RequestBody QuerySwapTradePO po, AdminUserReply adminUser) {
        Combo2<Long, Long> combo2 = getUserIdAndAccountId(po, adminUser.getOrgId());

        Long userId = combo2 != null ? combo2.getV1() : 0L;

        QueryOrgTradeDetailRequest.Builder builder = QueryOrgTradeDetailRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(adminUser.getOrgId()).setUserId(userId).build())
                .setQueryDataType(QueryTradeDataType.FUTURES_TRADE);

        if (!StringUtils.isEmpty(po.getSymbolId())) {
            builder.setSymbolId(po.getSymbolId());
        }

        builder.setFromId(po.getFromTradeId());
        builder.setLastId(po.getEndTradeId());
        builder.setLimit(po.getLimit());


        List<QueryOrgTradeDetailResponse.TradeDetail> tradeDetails = orderClient.getContractTrades(builder.build());
        List<SwapMatchDTO> matchResultList = tradeDetails.stream().map(matchInfo -> {
            return SwapMatchDTO.builder()
                    .userId(matchInfo.getUserId())
                    .accountId(matchInfo.getAccountId())
                    .orderId(matchInfo.getOrderId())
                    .tradeId(matchInfo.getTradeId())
                    .symbolId(matchInfo.getSymbolId())
                    .baseTokenId(matchInfo.getBaseTokenId())
                    .quoteTokenId(matchInfo.getQuoteTokenId())
                    .price(matchInfo.getPrice())
                    .quantity(matchInfo.getQuantity())
                    .feeTokenId(matchInfo.getFeeToken())
                    .feeTokenName(matchInfo.getFeeToken())
                    .fee(matchInfo.getFee())
                    // .type(matchInfo.getOrderType() == OrderType.MARKET ? OrderType.MARKET.name() : OrderType.LIMIT.name())
                    .side(FuturesUtil.getFuturesSide(OrderSide.valueOf(matchInfo.getOrderSide()), matchInfo.getIsClose() == 1).name())
                    .time(matchInfo.getMatchTime())
                    .executedAmount((new BigDecimal(matchInfo.getPrice())
                            .multiply(new BigDecimal(matchInfo.getQuantity()))
                            .toPlainString()))
                    // .priceType(matchInfo.getFuturesPriceType())
                    .unit(swapOrderService.getUnit(matchInfo.getSymbolId(), adminUser.getOrgId()))
                    .pnl(matchInfo.getPnl())

                    .build();
        }).sorted(Comparator.comparing(SwapMatchDTO::getTradeId).reversed()).collect(Collectors.toList());
        matchResultList = matchResultList.stream().filter(o -> adminUser.getOrgId() != 7007 || o.getTime() > System.currentTimeMillis() - 7*24*3600_000).collect(Collectors.toList());

        return ResultModel.ok(matchResultList);
    }

//    @RequestMapping(value = "/my_trades")
//    public ResultModel queryMySwapTrades(@RequestBody QuerySwapTradePO po) {
//        Combo2<Long, Long> combo2 = getUserIdAndAccountId(po, getOrgId());
//        if (combo2 == null) {
//            return ResultModel.ok(new ArrayList<>());
//        }
//        Long userId = combo2.getV1();
//
//        TimeRange range = TimeRangeUtil.getRange(po.getTimeRange());
//        Long startTime = (range != null ? range.getStartTime() : 0L);
//        Long endTime = (range != null ? range.getEndTime() : 0L);
//        List<SwapMatchDTO> matchResultList = swapOrderService.queryMatchInfo(getOrgId(), userId, po.getSymbolId(),
//                po.getFromTradeId(), po.getEndTradeId(), startTime, endTime, po.getLimit());
//        return ResultModel.ok(matchResultList);
//    }



    /**
     * 期货成交详情
     */
    @RequestMapping(value = "/match_info")
    public ResultModel querySwapOrderMatchInfo(@RequestBody QuerySwapMatchPO po) {
        Combo2<Long, Long> combo2 = getUserIdAndAccountId(po, getOrgId());
        if (combo2 == null) {
            return ResultModel.ok(new ArrayList<>());
        }
        Long userId = combo2.getV1();

        List<SwapMatchDTO> matchResultList = swapOrderService.getOrderMatchInfo(getOrgId(), userId, po.getOrderId(),
                po.getFromTradeId(), po.getLimit());
        return ResultModel.ok(matchResultList);
    }

    /**
     * 期货订单详情
     */
    @RequestMapping(value = "/order_info")
    public ResultModel querySwapOrderInfo(@RequestBody QuerySwapMatchPO po) {
        Combo2<Long, Long> combo2 = getUserIdAndAccountId(po, getOrgId());
        if (combo2 == null) {
            return ResultModel.ok(new ArrayList<>());
        }
        Long userId = combo2.getV1();

        SwapOrderDTO dto = swapOrderService.queryFutureOrder(getOrgId(), userId, po.getOrderId());
        return ResultModel.ok(dto);
    }

    /**
     * 查询爆仓单的持仓信息快照
     */
    @RequestMapping(value = "/query_liquidation_position")
    public ResultModel queryLiquidationPosition(@RequestBody QueryLiquidationPositionPO po) {
        Combo2<Long, Long> combo2 = getUserIdAndAccountId(po, getOrgId());
        if (combo2 == null) {
            return ResultModel.ok(FuturesPositionDTO.builder());
        }
        Long userId = combo2.getV1();
        SwapOrderDTO dto = swapOrderService.queryFutureOrder(getOrgId(), userId, po.getOrderId());
        FuturesPositionDTO position = swapOrderService.getLiquidationPosition(getOrgId(), userId, dto.getAccountId(), po.getOrderId());
        if (position != null) {
            position.setTotal(dto.getExecutedQty());
        }
        return ResultModel.ok(position);
    }
}
