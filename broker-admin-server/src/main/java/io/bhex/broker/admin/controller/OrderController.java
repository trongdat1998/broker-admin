package io.bhex.broker.admin.controller;

import com.google.common.base.Strings;
import io.bhex.base.account.BookOrderStruct;
import io.bhex.base.account.GetBookOrdersRequest;
import io.bhex.base.account.GetBookOrdersResponse;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.base.proto.OrderSideEnum;
import io.bhex.bhop.common.bizlog.BussinessLogAnnotation;
import io.bhex.bhop.common.jwt.filter.AccessAnnotation;
import io.bhex.bhop.common.util.BaseReqUtil;
import io.bhex.bhop.common.util.Combo2;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.BookOrderDTO;
import io.bhex.broker.admin.controller.dto.ContractExchangeInfo;
import io.bhex.broker.admin.controller.dto.OrderDetailDTO;
import io.bhex.broker.admin.controller.dto.PlanSpotOrderDTO;
import io.bhex.broker.admin.controller.dto.PlanSpotOrderDetailDTO;
import io.bhex.broker.admin.controller.dto.TradeDetailDTO;
import io.bhex.broker.admin.controller.param.BatchCancelPlanSpotOrderPO;
import io.bhex.broker.admin.controller.param.QueryBrokerOrderPO;
import io.bhex.broker.admin.controller.param.SimpleOrderPO;
import io.bhex.broker.admin.grpc.client.BrokerUserClient;
import io.bhex.broker.admin.grpc.client.OrderClient;
import io.bhex.broker.admin.service.ExchangeContractService;
import io.bhex.broker.admin.service.TokenService;
import io.bhex.broker.grpc.common.AccountTypeEnum;
import io.bhex.broker.grpc.common.Header;
import io.bhex.broker.grpc.common.Platform;
import io.bhex.broker.grpc.order.BatchCancelPlanSpotOrderRequest;
import io.bhex.broker.grpc.order.BatchCancelPlanSpotOrderResponse;
import io.bhex.broker.grpc.order.CancelOrderRequest;
import io.bhex.broker.grpc.order.CancelOrderResponse;
import io.bhex.broker.grpc.order.CancelPlanSpotOrderRequest;
import io.bhex.broker.grpc.order.CancelPlanSpotOrderResponse;
import io.bhex.broker.grpc.order.GetPlanSpotOrderRequest;
import io.bhex.broker.grpc.order.OrderResponseType;
import io.bhex.broker.grpc.order.OrderSide;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: broker user 下单管理controller
 * @Date: 2018/8/29 下午6:09
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */

@Slf4j
@RestController
@RequestMapping("/api/v1/order")
public class OrderController extends BrokerBaseController {

    @Autowired
    private BrokerUserClient brokerUserClient;

    @Autowired
    private OrderClient orderClient;

    @Autowired
    private TokenService tokenService;
    @Autowired
    private ExchangeContractService exchangeContractService;

    @AccessAnnotation(verifyGaOrPhone = false, verifyAuth = false)
    @RequestMapping(value = "/query_quote_tokens", method = RequestMethod.POST)
    public ResultModel queryMyQuoteTokens() {
        AdminUserReply requestUser = getRequestUser();
        long brokerId = requestUser.getOrgId();
        List<String> vo = tokenService.queryQuoteTokens(brokerId);
        return ResultModel.ok(vo);
    }

    @RequestMapping(value = "/current_orders", method = RequestMethod.POST)
    public ResultModel<List<OrderDetailDTO>> getNewOrders(@RequestBody @Valid SimpleOrderPO po, AdminUserReply adminUser) {
        Combo2<Long, Long> combo2 = null;
        if(po.getAccountType() != null && po.getAccountType() == AccountTypeEnum.MARGIN_VALUE){
            combo2 = getUserIdAndMarginAccountId(po, getOrgId());
        }else{
            combo2 = getUserIdAndAccountId(po, getOrgId());
        }
        if (combo2 == null) {
            return ResultModel.ok(new ArrayList<>());
        }
        Long accountId = combo2.getV2();
        Long userId = combo2.getV1();
        String symbolId = StringUtils.isNotEmpty(po.getSymbolId()) ? po.getSymbolId() : StringUtils.isAnyEmpty(po.getQuoteTokenId(), po.getBaseTokenId())
                ? null
                : po.getBaseTokenId() + po.getQuoteTokenId();

        Long lastOrderId = po.getLastId() == null || po.getLastId() < 0 ? 0 : po.getLastId();
        List<OrderDetailDTO> list = orderClient.getNewOrders(getOrgId(), accountId, symbolId, lastOrderId, po.getPageSize());
        if (CollectionUtils.isEmpty(list)) {
            return ResultModel.ok(new ArrayList<>());
        }
        list = list.stream().filter(o -> adminUser.getOrgId() != 7007 || o.getCreated() > System.currentTimeMillis() - 7*24*3600_000).collect(Collectors.toList());

        for (OrderDetailDTO orderDetailDTO : list) {
            orderDetailDTO.setUserId(userId);
        }

        return ResultModel.ok(list);
    }

    @BussinessLogAnnotation(opContent = "Cancel Order OrderId:{#po.orderId} ")
    @RequestMapping(value = "/cancel_order", method = RequestMethod.POST)
    public ResultModel<Boolean> cancelOrder(@RequestBody @Valid SimpleOrderPO po) {
        Combo2<Long, Long> combo2 = getUserIdAndAccountId(po, getOrgId());
        Long accountId = combo2.getV2();
        Header header = Header.newBuilder()
                .setOrgId(getOrgId())
                .setUserId(combo2.getV1())
                .setAdminUid(getRequestUserId())
                .setPlatform(Platform.PC)
                .setSource("broker-admin")
                .build();
        CancelOrderRequest request = CancelOrderRequest.newBuilder()
                .setHeader(header)
                .setAccountId(accountId)
                .setAccountType(AccountTypeEnum.COIN)
                .setOrderId(po.getOrderId())
                .setOrderResponseType(OrderResponseType.FULL)
                .build();
        CancelOrderResponse response = orderClient.cancelOrder(request);
        log.info("cancelOrder req:{} res:{}", request, response);
        if (response.getRet() == 0) {
            return ResultModel.ok(true);
        }
        return ResultModel.error("cancel order failed!");
    }

    @RequestMapping(value = "/history_orders", method = RequestMethod.POST)
    public ResultModel<List<OrderDetailDTO>> getHistoryOrders(@RequestBody @Valid SimpleOrderPO po, AdminUserReply adminUser) {
        Combo2<Long, Long> combo2 = null;
        if(po.getAccountType() != null && po.getAccountType() == AccountTypeEnum.MARGIN_VALUE){
            combo2 = getUserIdAndMarginAccountId(po, adminUser.getOrgId());
        }else{
            combo2 = getUserIdAndAccountId(po, adminUser.getOrgId());
        }
        if (combo2 == null) {
            return ResultModel.ok(new ArrayList<>());
        }
        Long accountId = combo2.getV2();
        Long userId = combo2.getV1();
        Long lastOrderId = po.getLastId() == null || po.getLastId() < 0 ? 0 : po.getLastId();
        List<OrderDetailDTO> list = orderClient.getHistoryOrders(adminUser.getOrgId(), accountId, Strings.nullToEmpty(po.getSymbolId()),
                lastOrderId, po.getPageSize());
        if (CollectionUtils.isEmpty(list)) {
            return ResultModel.ok(new ArrayList<>());
        }
        list = list.stream().filter(o -> adminUser.getOrgId() != 7007 || o.getCreated() > System.currentTimeMillis() - 7*24*3600_000).collect(Collectors.toList());
        for (OrderDetailDTO orderDetailDTO : list) {
            orderDetailDTO.setUserId(userId);
        }

        return ResultModel.ok(list);
    }

    @RequestMapping(value = "/order_trades", method = RequestMethod.POST)
    public ResultModel<List<TradeDetailDTO>> getOrderTrades(@RequestBody @Valid SimpleOrderPO po, AdminUserReply adminUser) {
        Long accountId = null;
        if (hasUserQueryCondition(po)) {
            Combo2<Long, Long> combo2 = null;
            if(po.getAccountType() != null && po.getAccountType() == AccountTypeEnum.MARGIN_VALUE){
                combo2 = getUserIdAndMarginAccountId(po, adminUser.getOrgId());
            }else{
                combo2 = getUserIdAndAccountId(po, adminUser.getOrgId());
            }
            if (combo2 == null) {
                return ResultModel.ok(new ArrayList<>());
            }
            accountId = combo2.getV2();
        }
        List<TradeDetailDTO> trades = orderClient.getCoidOrderTrades(adminUser.getOrgId(), accountId, po.getOrderId(), Strings.nullToEmpty(po.getSymbolId()));
        trades = trades.stream().filter(o -> adminUser.getOrgId() != 7007 || o.getCreatedAt() > System.currentTimeMillis() - 7*24*3600_000).collect(Collectors.toList());

        return ResultModel.ok(trades);
    }

    @RequestMapping(value = "/trades", method = RequestMethod.POST)
    public ResultModel<List<TradeDetailDTO>> getTrades(@RequestBody @Valid SimpleOrderPO po, AdminUserReply adminUser) {
        Long accountId = null;
        if (hasUserQueryCondition(po)) {
            Combo2<Long, Long> combo2 = null;
            if(po.getAccountType() != null && po.getAccountType() == AccountTypeEnum.MARGIN_VALUE){
                combo2 = getUserIdAndMarginAccountId(po, adminUser.getOrgId());
            }else{
                combo2 = getUserIdAndAccountId(po, adminUser.getOrgId());
            }
            if (combo2 == null) {
                return ResultModel.ok(new ArrayList<>());
            }
            accountId = combo2.getV2();
        }

        Long lastTradeId = po.getLastId() == null || po.getLastId() < 0 ? 0 : po.getLastId();
        List<TradeDetailDTO> trades = orderClient.getCoinTrades(adminUser.getOrgId(), accountId,
                Strings.nullToEmpty(po.getSymbolId()), lastTradeId, po.getPageSize());
        if (CollectionUtils.isEmpty(trades)) {
            return ResultModel.ok(new ArrayList<>());
        }
        trades = trades.stream().filter(o -> adminUser.getOrgId() != 7007 || o.getCreatedAt() > System.currentTimeMillis() - 7*24*3600_000).collect(Collectors.toList());

        return ResultModel.ok(trades);
    }

    @RequestMapping(value = "/book_orders")
    public ResultModel getBookOrders(@RequestBody @Valid QueryBrokerOrderPO param) {
        Long orgId = getOrgId();

        List<ContractExchangeInfo> contractExchangeInfos = exchangeContractService.listALlExchangeContractInfo(orgId);
        if (CollectionUtils.isEmpty(contractExchangeInfos)) {
            return ResultModel.error("no contract exchange");
        }
        List<Long> exchangeIds = contractExchangeInfos.stream().map(info -> info.getExchangeId()).collect(Collectors.toList());

        GetBookOrdersRequest request = GetBookOrdersRequest.newBuilder()
                .setBaseRequest(BaseReqUtil.getBaseRequest(orgId))
                .setExchangeId(exchangeIds.get(0))
                .setSymbolId(param.getSymbolId().replace("/", ""))
                .setSide(OrderSideEnum.forNumber(param.getSide()))
                .build();

        GetBookOrdersResponse response = orderClient.getBookOrders(request);
        log.info("response:{}", response.getResponseCode());

        Map<String, Object> result = new HashMap<>();
        if (response.getResponseCode() != GetBookOrdersResponse.GetBookOrdersResponseEnum.SUCCESS) {
            result.put("result", response.getResponseCodeValue());
            return ResultModel.ok(result);
        }
        result.put("result", 0);

        List<BookOrderStruct> list = response.getBookOrdersList();
        if (CollectionUtils.isEmpty(list)) {
            return ResultModel.ok(result);
        }

        List<BookOrderDTO> resultList = list.stream()
                .filter(o -> o.getBrokerId() == orgId)
                .map(o -> {
                    BookOrderDTO dto = new BookOrderDTO();
                    BeanUtils.copyProperties(o, dto);
                    dto.setAmount(new BigDecimal(o.getAmount()));
                    dto.setPrice(new BigDecimal(o.getPrice()));
                    dto.setQuantity(new BigDecimal(o.getQuantity()));
                    return dto;
                }).collect(Collectors.toList());

        if (param.getType() == 1) {
            result.put("list", resultList);
            return ResultModel.ok(result);
        }

        //盘口聚合模式
        List<BookOrderDTO> groupResult = new ArrayList<>();
        Map<BigDecimal, List<BookOrderDTO>> groups = resultList.stream().collect(Collectors.groupingBy(BookOrderDTO::getPrice));
        for (BigDecimal price : groups.keySet()) {
            List<BookOrderDTO> orders = groups.get(price);
            BookOrderDTO dto = new BookOrderDTO();
            dto.setPrice(price);
            dto.setQuantity(new BigDecimal(orders.stream().mapToDouble(BookOrderDTO::getQuantityDouble).sum()));
            dto.setAmount(new BigDecimal(orders.stream().mapToDouble(BookOrderDTO::getAmountDouble).sum()));
            groupResult.add(dto);
        }

        Comparator<BookOrderDTO> comparator = Comparator.comparing(BookOrderDTO::getPrice);
        if (param.getSide() == OrderSideEnum.SELL_VALUE) {
            comparator = comparator.reversed();
        }
        groupResult = groupResult.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
        result.put("list", groupResult);
        return ResultModel.ok(result);
    }

    @RequestMapping(value = "/plan_spot/get", method = RequestMethod.POST)
    public ResultModel<PlanSpotOrderDetailDTO> getPlanSpotOrder(@RequestBody @Valid SimpleOrderPO po) {
        long orgId = getOrgId();
        Combo2<Long, Long> combo2;
        if (po.getAccountType() != null && po.getAccountType() == AccountTypeEnum.MARGIN_VALUE) {
            combo2 = getUserIdAndMarginAccountId(po, orgId);
        } else {
            combo2 = getUserIdAndAccountId(po, orgId);
        }
        if (combo2 == null) {
            return ResultModel.error("no account");
        }
        Long accountId = combo2.getV2();
        Header header = Header.newBuilder()
                .setOrgId(orgId)
                .setUserId(combo2.getV1())
                .setAdminUid(getRequestUserId())
                .setPlatform(Platform.PC)
                .setSource("broker-admin")
                .build();
        GetPlanSpotOrderRequest request = GetPlanSpotOrderRequest.newBuilder()
                .setHeader(header)
                .setAccountId(accountId)
                .setOrderId(po.getOrderId())
                .setOrderResponseType(OrderResponseType.FULL)
                .build();
        PlanSpotOrderDetailDTO detailDTO = orderClient.getPlanSpotOrder(request);
        return ResultModel.ok(detailDTO);
    }

    @RequestMapping(value = "/plan_spot/current_orders", method = RequestMethod.POST)
    public ResultModel<List<PlanSpotOrderDTO>> getNewPlanSpotOrders(@RequestBody @Valid SimpleOrderPO po) {
        Combo2<Long, Long> combo2;
        Long orgId = getOrgId();
        if (po.getAccountType() != null && po.getAccountType() == AccountTypeEnum.MARGIN_VALUE) {
            combo2 = getUserIdAndMarginAccountId(po, orgId);
        } else {
            combo2 = getUserIdAndAccountId(po, orgId);
        }
        if (combo2 == null) {
            return ResultModel.ok(new ArrayList<>());
        }
        Long accountId = combo2.getV2();
        po.setUserId(combo2.getV1());
        List<PlanSpotOrderDTO> list = orderClient.getNewPlanSpotOrders(orgId, accountId, po);
        if (CollectionUtils.isEmpty(list)) {
            return ResultModel.ok(new ArrayList<>());
        }

        return ResultModel.ok(list);
    }

    @RequestMapping(value = "/plan_spot/history_orders", method = RequestMethod.POST)
    public ResultModel<List<PlanSpotOrderDTO>> getHistoryPlanOrders(@RequestBody @Valid SimpleOrderPO po, AdminUserReply adminUser) {
        long orgId = adminUser.getOrgId();
        Combo2<Long, Long> combo2;
        if (po.getAccountType() != null && po.getAccountType() == AccountTypeEnum.MARGIN_VALUE) {
            combo2 = getUserIdAndMarginAccountId(po, orgId);
        } else {
            combo2 = getUserIdAndAccountId(po, orgId);
        }
        if (combo2 == null) {
            return ResultModel.ok(new ArrayList<>());
        }
        Long accountId = combo2.getV2();
        po.setUserId(combo2.getV1());
        List<PlanSpotOrderDTO> list = orderClient.getHistoryPlanSpotOrders(adminUser.getOrgId(), accountId, po);
        if (CollectionUtils.isEmpty(list)) {
            return ResultModel.ok(new ArrayList<>());
        }
        return ResultModel.ok(list);
    }

    @BussinessLogAnnotation(opContent = "Cancel Plan Spot Order OrderId:{#po.orderId} ")
    @RequestMapping(value = "/plan_spot/cancel", method = RequestMethod.POST)
    public ResultModel<Boolean> cancelPlanSpotOrder(@RequestBody @Valid SimpleOrderPO po) {
        long orgId = getOrgId();
        Long userId = getUserId(po, orgId);
        if (userId == null) {
            return ResultModel.error("no account");
        }
        AccountTypeEnum accountTypeEnum;
        if (po.getAccountType() != null && po.getAccountType() == AccountTypeEnum.MARGIN_VALUE) {
            accountTypeEnum = AccountTypeEnum.MARGIN;
        } else {
            accountTypeEnum = AccountTypeEnum.COIN;
        }
        Header header = Header.newBuilder()
                .setOrgId(orgId)
                .setUserId(userId)
                .setAdminUid(getRequestUserId())
                .setPlatform(Platform.PC)
                .setSource("broker-admin")
                .build();
        CancelPlanSpotOrderRequest request = CancelPlanSpotOrderRequest.newBuilder()
                .setHeader(header)
                .setAccountType(accountTypeEnum)
                .setOrderId(po.getOrderId())
                .setOrderResponseType(OrderResponseType.FULL)
                .build();
        CancelPlanSpotOrderResponse response = orderClient.cancelPlanSpotOrder(request);
        log.info("cancelPlanSpotOrder req:{} res:{}", request, response);
        if (response.getRet() == 0) {
            return ResultModel.ok(true);
        }
        return ResultModel.error("cancel plan spot order failed!");
    }

    @PostMapping(value = "/plan_spot/batch_cancel")
    public ResultModel<Boolean> batchCancelPlanSpotOrder(@RequestBody BatchCancelPlanSpotOrderPO po) {
        long orgId = getOrgId();
        Long userId = getUserId(po, orgId);
        if (userId == null) {
            return ResultModel.error("no account");
        }
        AccountTypeEnum accountTypeEnum;
        if (po.getAccountType() != null && po.getAccountType() == AccountTypeEnum.MARGIN_VALUE) {
            accountTypeEnum = AccountTypeEnum.MARGIN;
        } else {
            accountTypeEnum = AccountTypeEnum.COIN;
        }
        Header header = Header.newBuilder()
                .setOrgId(orgId)
                .setUserId(userId)
                .setAdminUid(getRequestUserId())
                .setPlatform(Platform.PC)
                .setSource("broker-admin")
                .build();
        OrderSide orderSide;
        if (StringUtils.isBlank(po.getOrderSide())) {
            orderSide = OrderSide.UNKNOWN_ORDER_SIDE;
        } else if ("BUY".equals(po.getOrderSide())) {
            orderSide = OrderSide.BUY;
        } else {
            orderSide = OrderSide.SELL;
        }
        BatchCancelPlanSpotOrderRequest request = BatchCancelPlanSpotOrderRequest.newBuilder()
                .setHeader(header)
                .setAccountType(accountTypeEnum)
                .setSymbolId(po.getSymbolId().replaceFirst("/", ""))
                .setOrderSide(orderSide)
                .build();
        BatchCancelPlanSpotOrderResponse response = orderClient.batchCancelPlanSpotOrder(request);
        log.info("batchCancelPlanSpotOrder req:{} res:{}", request, response);
        if (response.getRet() == 0) {
            return ResultModel.ok(true);
        }
        return ResultModel.error("batch cancel plan spot order failed!");
    }
}
