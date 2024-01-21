package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.base.account.*;
import io.bhex.base.proto.BaseRequest;
import io.bhex.base.proto.DecimalUtil;
import io.bhex.base.proto.OrderSideEnum;
import io.bhex.base.proto.OrderStatusEnum;
import io.bhex.base.proto.OrderTypeEnum;
import io.bhex.bhop.common.config.GrpcConfig;
import io.bhex.broker.admin.controller.dto.OrderDetailDTO;
import io.bhex.broker.admin.controller.dto.PlanSpotOrderDTO;
import io.bhex.broker.admin.controller.dto.PlanSpotOrderDetailDTO;
import io.bhex.broker.admin.controller.dto.TradeDetailDTO;
import io.bhex.broker.admin.controller.dto.TradeDetailInfoDTO;
import io.bhex.broker.admin.controller.param.SimpleOrderPO;
import io.bhex.broker.admin.grpc.client.OrderClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.common.Header;
import io.bhex.broker.grpc.common.QueryTradeDataType;
import io.bhex.broker.grpc.order.BatchCancelPlanSpotOrderRequest;
import io.bhex.broker.grpc.order.BatchCancelPlanSpotOrderResponse;
import io.bhex.broker.grpc.order.CancelOrderResponse;
import io.bhex.broker.grpc.order.CancelPlanSpotOrderRequest;
import io.bhex.broker.grpc.order.CancelPlanSpotOrderResponse;
import io.bhex.broker.grpc.order.GetPlanSpotOrderResponse;
import io.bhex.broker.grpc.order.OrderSide;
import io.bhex.broker.grpc.order.OrderType;
import io.bhex.broker.grpc.order.PlanSpotOrder;
import io.bhex.broker.grpc.order.QueryPlanSpotOrdersResponse;
import io.bhex.broker.grpc.statistics.QueryOrgTradeDetailRequest;
import io.bhex.broker.grpc.statistics.QueryOrgTradeDetailResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderClientImpl implements OrderClient {

    @Resource
    GrpcClientConfig grpcConfig;

    @Override
    public List<OrderDetailDTO> getNewOrders(Long orgId, Long accountId, String symbolId, Long startOrderId, Integer pageSize) {
        List<OrderStatusEnum> orderStatusList = Arrays.asList(OrderStatusEnum.NEW, OrderStatusEnum.PARTIALLY_FILLED);
        return getOrders(orgId, accountId, symbolId, startOrderId, pageSize, orderStatusList);

    }

    private List<OrderDetailDTO> getOrders(Long orgId, Long accountId, String symbolId, Long startOrderId, Integer pageSize, List<OrderStatusEnum> orderStatusList) {
        OrderServiceGrpc.OrderServiceBlockingStub stub = grpcConfig.orderServiceBlockingStub(GrpcConfig.SAAS_ADMIN_GRPC_CHANNEL_NAME);
        GetOrdersRequest.Builder builder = GetOrdersRequest.newBuilder()
                .setBaseRequest(BaseRequest.newBuilder().setOrganizationId(orgId).build())
                .setAccountId(accountId)
                .setOrderId(startOrderId)
                .setLimit(pageSize);
        builder.addAllOrderStatusList(orderStatusList);
        if (!StringUtils.isEmpty(symbolId)) {
            builder.setSymbolId(symbolId);
        }
        GetOrdersReply reply = stub.getOrders(builder.build());
        List<Order> orders = reply.getOrdersList();
        if (CollectionUtils.isEmpty(orders)) {
            return new ArrayList<>();
        }

        List<OrderDetailDTO> dtos = new ArrayList<>();
        for (Order order : orders) {
            OrderDetailDTO dto = new OrderDetailDTO();
            BeanUtils.copyProperties(order, dto);
            dtos.add(dto);
            dto.setStatus(order.getStatusValue());
            dto.setExecutedAmount(DecimalUtil.toBigDecimal(order.getExecutedAmount()));
            dto.setPrice(DecimalUtil.toBigDecimal(order.getPrice()));
            dto.setExecutedQuantity(DecimalUtil.toBigDecimal(order.getExecutedQuantity()));
            dto.setAmount(DecimalUtil.toBigDecimal(order.getAmount()));
            dto.setSide(order.getSideValue());
            dto.setOrderType(order.getTypeValue());
            dto.setLocked(DecimalUtil.toBigDecimal(order.getLocked()));
            dto.setQuantity(DecimalUtil.toBigDecimal(order.getQuantity()));
            dto.setAveragePrice(DecimalUtil.toBigDecimal(order.getAveragePrice()));
            //dto.setStopPrice(decimal2String(order.getStopPrice()));
            dto.setCreated(order.getCreatedTime());
            //dto.setSymbolId(order.getBaseTokenId()+"/"+order.getQuoteTokenId());
            dto.setSymbolId(order.getSymbol().getBaseToken().getTokenName() + "/" + order.getSymbol().getQuoteToken().getTokenName());
            dto.setUpdated(order.getUpdatedTime());

        }
        return dtos;
    }

    @Override
    public List<OrderDetailDTO> getHistoryOrders(Long orgId, Long accountId, String symbolId, Long startOrderId, Integer pageSize) {
        List<OrderStatusEnum> orderStatusList = Arrays.asList(OrderStatusEnum.FILLED, OrderStatusEnum.CANCELED, OrderStatusEnum.REJECTED);
        return getOrders(orgId, accountId, symbolId, startOrderId, pageSize, orderStatusList);
    }


    private List<TradeDetailDTO> getOrderTrades(long orgId, Long accountId, Long orderId,
                                                String symbolId, Long lastTradeId, Integer pageSize) {
        QueryOrgTradeDetailRequest.Builder builder = QueryOrgTradeDetailRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build()).setQueryDataType(QueryTradeDataType.COIN_TRADE);


        if (!StringUtils.isEmpty(symbolId)) {
            builder.setSymbolId(symbolId);
        }
        if (lastTradeId != null || lastTradeId > 0) {
            builder.setFromId(lastTradeId);
        }
        if (pageSize != null && pageSize > 0) {
            builder.setLimit(pageSize);
        }

        if (orderId != null && orderId > 0) {
            builder.setOrderId(orderId);
        }
        if (accountId != null && accountId > 0) {
            builder.setAccountId(accountId);
        }

        QueryOrgTradeDetailResponse response = grpcConfig.statisticsServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).queryOrgTradeDetail(builder.build());

        List<QueryOrgTradeDetailResponse.TradeDetail> trades = response.getTradeDetailList();

        if (CollectionUtils.isEmpty(trades)) {
            return new ArrayList<>();
        }

        List<TradeDetailDTO> dtos = new ArrayList<>();
        for (QueryOrgTradeDetailResponse.TradeDetail trade : trades) {
            TradeDetailDTO dto = new TradeDetailDTO();
            BeanUtils.copyProperties(trade, dto);
            dtos.add(dto);
            dto.setAmount(new BigDecimal(trade.getAmount()));
            dto.setPrice(new BigDecimal(trade.getPrice()));
            dto.setQuantity(new BigDecimal(trade.getQuantity()));
            dto.setSide(trade.getSide());
            dto.setTokenFee(new BigDecimal(trade.getFee()));
            dto.setCreatedAt(trade.getMatchTime());
            dto.setOrderType(trade.getOrderType());
            dto.setFeeTokenId(trade.getFeeToken());
            dto.setSymbolId(trade.getSymbolId());
        }

        return dtos;
    }

    @Override
    public List<QueryOrgTradeDetailResponse.TradeDetail> getContractTrades(QueryOrgTradeDetailRequest request) {
        return grpcConfig.statisticsServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).queryOrgTradeDetail(request).getTradeDetailList();
    }

    @Override
    public List<TradeDetailDTO> getCoidOrderTrades(long orgId, Long accountId, Long orderId, String symbolId) {
        return getOrderTrades(orgId, accountId, orderId, symbolId, 0L, 0);
    }

    @Override
    public List<TradeDetailDTO> getCoinTrades(long orgId, Long accountId, String symbolId, Long lastTradeId, Integer pageSize) {
        return getOrderTrades(orgId, accountId, null, symbolId, lastTradeId, pageSize);
    }

    @Override
    public CancelOrderResponse cancelOrder(io.bhex.broker.grpc.order.CancelOrderRequest request) {
        io.bhex.broker.grpc.order.OrderServiceGrpc.OrderServiceBlockingStub stub = grpcConfig.brokerOrderServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
        CancelOrderResponse reply = stub.cancelOrder(request);
        return reply;
    }

    @Override
    public List<TradeDetailInfoDTO> getTradesDetailDesc(Long brokerId,
                                                        Long lastTradeId,
                                                        String symbolId,
                                                        Long accountId,
                                                        Long orderId,
                                                        Long startTime,
                                                        Long endTime,
                                                        Integer limit) {
        log.info("getTradesDetailDesc brokerId{} lastTradeId {} symbolId {} startTime {} endTime {} limit {}",
                brokerId, lastTradeId, startTime, startTime, endTime, limit);
        GetOrderTradeDetailListRequest.Builder builder = GetOrderTradeDetailListRequest.newBuilder();
        if (orderId != null && orderId > 0) {
            builder.setOrderId(orderId);
        }
        if (!StringUtils.isEmpty(symbolId)) {
            builder.setSymbolId(symbolId);
        }
        if (lastTradeId != null || lastTradeId > 0) {
            builder.setFromTradeId(lastTradeId);
        }
        if (limit != null && limit > 0) {
            builder.setLimit(limit);
        }

        if (brokerId != null && brokerId > 0) {
            //可以为null或为0,独立部署时,该部分不通过
            builder.setBrokerId(brokerId);
        }

        if (accountId != null && accountId > 0) {
            builder.setAccountId(accountId);
        }

        if (startTime != null && startTime > 0) {
            builder.setStartTime(startTime);
        }

        if (endTime != null && endTime > 0) {
            builder.setEndTime(endTime);
        }

        OrderServiceGrpc.OrderServiceBlockingStub stub = grpcConfig.orderServiceBlockingStub(GrpcConfig.SAAS_ADMIN_GRPC_CHANNEL_NAME);
        GetOrderTradeDetailReply reply = stub.getTradesDetailDesc(builder.build());

        List<Trade> trades = reply.getTradesList();
        log.info("trades size {}", trades.size());
        if (CollectionUtils.isEmpty(trades)) {
            return new ArrayList<>();
        }
        return buildTradeDetailInfoDtoList(trades);
    }


    private List<TradeDetailDTO> buildTradeDetailDtoList(List<Trade> trades) {
        List<TradeDetailDTO> dtos = new ArrayList<>();
        for (Trade trade : trades) {
            TradeDetailDTO dto = new TradeDetailDTO();
            BeanUtils.copyProperties(trade, dto);
            dtos.add(dto);
            //dto.setBrokerId(trade.getOrgId());
            dto.setAmount(DecimalUtil.toBigDecimal(trade.getAmount()));
            //dto.setFeeRate(decimal2String(trade.getFee()));
            dto.setPrice(DecimalUtil.toBigDecimal(trade.getPrice()));
            dto.setQuantity(DecimalUtil.toBigDecimal(trade.getQuantity()));
            dto.setSide(trade.getSideValue());
            dto.setTokenFee(DecimalUtil.toBigDecimal(trade.getTokenFee()));
            dto.setCreatedAt(trade.getMatchTime());
            // dto.setSysTokenFee(decimal2String(trade.getSysTokenFee()));
            dto.setOrderType(trade.getOrderTypeValue());
            dto.setFeeTokenId(trade.getFeeTokenId());
            dto.setSymbolId(trade.getSymbol().getBaseToken().getTokenName() + "/" + trade.getSymbol().getQuoteToken().getTokenName());
        }
        return dtos;
    }

    private List<TradeDetailInfoDTO> buildTradeDetailInfoDtoList(List<Trade> trades) {
        List<TradeDetailInfoDTO> dtos = new ArrayList<>();
        for (Trade trade : trades) {
            TradeDetailInfoDTO dto = new TradeDetailInfoDTO();
            BeanUtils.copyProperties(trade, dto);
            dtos.add(dto);
            dto.setAccountId(trade.getAccountId());
            dto.setAmount(DecimalUtil.toBigDecimal(trade.getAmount()));
            dto.setPrice(DecimalUtil.toBigDecimal(trade.getPrice()));
            dto.setQuantity(DecimalUtil.toBigDecimal(trade.getQuantity()));
            dto.setSide(trade.getSideValue());
            dto.setTokenFee(DecimalUtil.toBigDecimal(trade.getTokenFee()));
            dto.setCreatedAt(trade.getMatchTime());
            dto.setUserId(trade.getBrokerUserId());
            dto.setSymbolId(trade.getSymbol().getBaseToken().getTokenName() + "/" + trade.getSymbol().getQuoteToken().getTokenName());
        }
        return dtos;
    }

    @Override
    public GetBookOrdersResponse getBookOrders(GetBookOrdersRequest request) {
        return  grpcConfig.orderServiceBlockingStub(GrpcClientConfig.BH_SERVER_CHANNEL_NAME).getBookOrders(request);
    }

    @Override
    public CancelMatchOrderReply cancelBrokerOrders(Long brokerId, long exchangeId, String symbolId) {
        CancelMatchOrderRequest request = CancelMatchOrderRequest .newBuilder()
                .setBrokerId(brokerId).setSymbolId(symbolId).setExchangeId(exchangeId)
                .build();
        CancelMatchOrderReply reply = grpcConfig.orderServiceBlockingStub(GrpcClientConfig.BH_SERVER_CHANNEL_NAME).cancelBrokerOrders(request);
        log.info("cancelBrokerOrders req:{} res:{}", request, reply);
        return reply;
    }

    @Override
    public CancelMatchOrderReply cancelAccountOrders(Long brokerId, long accountId, String symbolId) {
        CancelMatchOrderRequest request = CancelMatchOrderRequest .newBuilder()
                .setBrokerId(brokerId).setAccountId(accountId).setSymbolId(symbolId)
                .build();
        CancelMatchOrderReply reply = grpcConfig.orderServiceBlockingStub(GrpcClientConfig.BH_SERVER_CHANNEL_NAME).cancelBrokerOrders(request);
        log.info("cancelAccountOrders req:{} res:{}", request, reply);
        return reply;
    }

    @Override
    public PlanSpotOrderDetailDTO getPlanSpotOrder(io.bhex.broker.grpc.order.GetPlanSpotOrderRequest request) {
        io.bhex.broker.grpc.order.OrderServiceGrpc.OrderServiceBlockingStub stub = grpcConfig.brokerOrderServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
        GetPlanSpotOrderResponse response = stub.adminGetPlanSpotOrder(request);
        PlanSpotOrderDTO planSpotOrderDTO = getPlanSpotOrderDTO(request.getHeader().getUserId(), response.getPlanOrder());
        PlanSpotOrderDetailDTO detailDTO = new PlanSpotOrderDetailDTO();
        detailDTO.setPlanOrder(planSpotOrderDTO);
        if (planSpotOrderDTO.getExecutedOrderId() > 0 && response.hasOrder()) {
            OrderDetailDTO orderDetailDTO = getOrderDetailByBrokerOrder(request.getHeader().getUserId(), response.getOrder());
            detailDTO.setOrder(orderDetailDTO);
        }
        return detailDTO;
    }

    /**
     * 反向操作，再把broker的订单转回bhex的订单
     * @param order
     * @return
     */
    private OrderDetailDTO getOrderDetailByBrokerOrder(Long userId, io.bhex.broker.grpc.order.Order order) {
        OrderSideEnum orderSideEnum = OrderSideEnum.valueOf(order.getOrderSide().name());
        BigDecimal amount = new BigDecimal(0);
        BigDecimal quantity = new BigDecimal(order.getOrigQty());
        OrderTypeEnum orderTypeEnum;
        switch (order.getOrderType()) {
            case LIMIT_MAKER:
                orderTypeEnum = OrderTypeEnum.LIMIT_MAKER;
                break;
            case LIMIT:
                orderTypeEnum = OrderTypeEnum.LIMIT;
                break;
            case LIMIT_FREE:
                orderTypeEnum = OrderTypeEnum.LIMIT_FREE;
                break;
            case LIMIT_MAKER_FREE:
                orderTypeEnum = OrderTypeEnum.LIMIT_MAKER_FREE;
                break;
            default:
            case MARKET:
                if (orderSideEnum == OrderSideEnum.BUY) {
                    orderTypeEnum = OrderTypeEnum.MARKET_OF_QUOTE;
                    amount = new BigDecimal(order.getOrigQty());
                    quantity = new BigDecimal(0);
                } else {
                    orderTypeEnum = OrderTypeEnum.MARKET_OF_BASE;
                    amount = new BigDecimal(0);
                    quantity = new BigDecimal(order.getOrigQty());
                }
                break;
        }
        OrderDetailDTO orderDetailDTO = new OrderDetailDTO();
        orderDetailDTO.setOrderId(order.getOrderId());
        orderDetailDTO.setOrderType(orderTypeEnum.getNumber());
        orderDetailDTO.setPrice(new BigDecimal(order.getPrice()));
        orderDetailDTO.setQuantity(quantity);
        orderDetailDTO.setAmount(amount);
        orderDetailDTO.setSide(orderSideEnum.getNumber());
        orderDetailDTO.setStatus(OrderStatusEnum.valueOf(order.getStatusCode()).getNumber());
        orderDetailDTO.setUserId(userId);
        orderDetailDTO.setAveragePrice(new BigDecimal(order.getAvgPrice()));
        orderDetailDTO.setExecutedAmount(new BigDecimal(order.getExecutedAmount()));
        orderDetailDTO.setExecutedQuantity(new BigDecimal(order.getExecutedQty()));
        orderDetailDTO.setCreated(order.getTime());
        orderDetailDTO.setUpdated(order.getLastUpdated());
        orderDetailDTO.setSymbolId(order.getBaseTokenName() + "/" + order.getQuoteTokenName());
        return orderDetailDTO;
    }


    @Override
    public List<PlanSpotOrderDTO> getNewPlanSpotOrders(Long orgId, Long accountId, SimpleOrderPO po) {
        List<PlanSpotOrder.PlanOrderStatusEnum> orderStatusList = Collections.singletonList(PlanSpotOrder.PlanOrderStatusEnum.ORDER_NEW);
        return getPlanSpotOrders(orgId, accountId, po, orderStatusList);
    }

    @Override
    public CancelPlanSpotOrderResponse cancelPlanSpotOrder(CancelPlanSpotOrderRequest request) {
        io.bhex.broker.grpc.order.OrderServiceGrpc.OrderServiceBlockingStub stub = grpcConfig.brokerOrderServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
        return stub.cancelPlanSpotOrder(request);
    }

    @Override
    public BatchCancelPlanSpotOrderResponse batchCancelPlanSpotOrder(BatchCancelPlanSpotOrderRequest request) {
        io.bhex.broker.grpc.order.OrderServiceGrpc.OrderServiceBlockingStub stub = grpcConfig.brokerOrderServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
        return stub.batchCancelPlanSpotOrder(request);
    }

    @Override
    public List<PlanSpotOrderDTO> getHistoryPlanSpotOrders(Long orgId, Long accountId, SimpleOrderPO po) {
        List<PlanSpotOrder.PlanOrderStatusEnum> orderStatusList = Arrays.asList(PlanSpotOrder.PlanOrderStatusEnum.ORDER_FILLED, PlanSpotOrder.PlanOrderStatusEnum.ORDER_CANCELED, PlanSpotOrder.PlanOrderStatusEnum.ORDER_REJECTED);
        return getPlanSpotOrders(orgId, accountId, po, orderStatusList);
    }

    private List<PlanSpotOrderDTO> getPlanSpotOrders(Long orgId, Long accountId, SimpleOrderPO po, List<PlanSpotOrder.PlanOrderStatusEnum> orderStatusList) {
        io.bhex.broker.grpc.order.OrderServiceGrpc.OrderServiceBlockingStub stub = grpcConfig.brokerOrderServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
        String symbolId = org.apache.commons.lang3.StringUtils.isNotEmpty(po.getSymbolId()) ? po.getSymbolId() : org.apache.commons.lang3.StringUtils.isAnyEmpty(po.getQuoteTokenId(), po.getBaseTokenId())
                ? null : po.getBaseTokenId() + po.getQuoteTokenId();
        long lastOrderId = po.getLastId() == null || po.getLastId() < 0 ? 0 : po.getLastId();
        long startTime = po.getStartTime() == null ? 0L : po.getStartTime();
        long endTime = po.getEndTime() == null ? 0L : po.getEndTime();
        int limit = po.getPageSize() == null ? 20 : po.getPageSize();
        io.bhex.broker.grpc.order.QueryPlanSpotOrdersRequest.Builder builder = io.bhex.broker.grpc.order.QueryPlanSpotOrdersRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setAccountId(accountId)
                .setFromOrderId(lastOrderId)
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setLimit(limit);
        builder.addAllOrderStatus(orderStatusList);
        if (!StringUtils.isEmpty(symbolId)) {
            builder.setSymbolId(symbolId.replaceFirst("/", ""));
        }
        QueryPlanSpotOrdersResponse reply = stub.adminQueryPlanSpotOrders(builder.build());
        if (reply.getRet() == 0) {
            return reply.getOrdersList().stream()
                    .map(order -> getPlanSpotOrderDTO(po.getUserId(), order))
                    .collect(Collectors.toList());
        } else {
            log.error("getPlanSpotOrders error!{}", reply.getRet());
            return new ArrayList<>();
        }
    }

    private PlanSpotOrderDTO getPlanSpotOrderDTO(Long userId, PlanSpotOrder order) {
        PlanSpotOrderDTO dto = new PlanSpotOrderDTO();
        dto.setTime(order.getTime());
        dto.setOrderId(order.getOrderId());
        dto.setExecutedOrderId(order.getExecutedOrderId());
        dto.setAccountId(order.getAccountId());
        dto.setClientOrderId(order.getClientOrderId());
        //dto.setSymbolId(order.getSymbolId());
        dto.setSymbolName(order.getSymbolName());
        dto.setBaseTokenId(order.getBaseTokenId());
        dto.setBaseTokenName(order.getBaseTokenName());
        dto.setQuoteTokenId(order.getQuoteTokenId());
        dto.setQuoteTokenName(order.getQuoteTokenName());
        dto.setPrice(new BigDecimal(order.getPrice()));
        if (OrderType.MARKET.equals(order.getOrderType()) && OrderSide.BUY.equals(order.getSide())) {
            dto.setAmount(new BigDecimal(order.getOrigQty()));
            dto.setQuantity(BigDecimal.ZERO);
        } else {
            dto.setQuantity(new BigDecimal(order.getOrigQty()));
            dto.setAmount(BigDecimal.ZERO);
        }
        dto.setSymbolId(order.getBaseTokenName() + "/" + order.getQuoteTokenName());
        dto.setTriggerPrice(new BigDecimal(order.getTriggerPrice()));
        dto.setTriggerTime(order.getTriggerTime());
        dto.setQuotePrice(new BigDecimal(order.getQuotePrice()));
        dto.setExecutedPrice(new BigDecimal(order.getExecutedPrice()));
        dto.setExecutedQty(new BigDecimal(order.getExecutedQuantity()));
        //dto.getExecutedAmount(null);
        //成交均价 avgPrice
        dto.setOrderType(order.getOrderTypeValue());
        dto.setSide(order.getSideValue());
        dto.setStatus(order.getStatusValue());
        dto.setUserId(userId);
        //dto.setStatusDesc();
        //dto.setLastExecutedPrice();
        //dto.setLastExecutedQuantity;
        //下单总金额
        dto.setExchangeId(order.getExchangeId());
        dto.setOrgId(order.getOrgId());
        dto.setUpdated(order.getLastUpdated());
        return dto;
    }
}
