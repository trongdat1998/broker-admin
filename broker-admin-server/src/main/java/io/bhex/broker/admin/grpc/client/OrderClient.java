package io.bhex.broker.admin.grpc.client;

import io.bhex.base.account.CancelMatchOrderReply;
import io.bhex.base.account.GetBookOrdersRequest;
import io.bhex.base.account.GetBookOrdersResponse;
import io.bhex.broker.admin.controller.dto.OrderDetailDTO;
import io.bhex.broker.admin.controller.dto.PlanSpotOrderDTO;
import io.bhex.broker.admin.controller.dto.PlanSpotOrderDetailDTO;
import io.bhex.broker.admin.controller.dto.TradeDetailDTO;
import io.bhex.broker.admin.controller.dto.TradeDetailInfoDTO;
import io.bhex.broker.admin.controller.param.SimpleOrderPO;
import io.bhex.broker.grpc.order.BatchCancelPlanSpotOrderRequest;
import io.bhex.broker.grpc.order.BatchCancelPlanSpotOrderResponse;
import io.bhex.broker.grpc.order.CancelOrderRequest;
import io.bhex.broker.grpc.order.CancelOrderResponse;
import io.bhex.broker.grpc.order.CancelPlanSpotOrderRequest;
import io.bhex.broker.grpc.order.CancelPlanSpotOrderResponse;
import io.bhex.broker.grpc.order.GetPlanSpotOrderRequest;
import io.bhex.broker.grpc.statistics.QueryOrgTradeDetailRequest;
import io.bhex.broker.grpc.statistics.QueryOrgTradeDetailResponse;

import java.util.List;

public interface OrderClient {

    List<OrderDetailDTO> getNewOrders(Long orgId, Long accountId, String symbolId, Long startOrderId, Integer pageSize);

    List<PlanSpotOrderDTO> getNewPlanSpotOrders(Long orgId, Long accountId, SimpleOrderPO po);

    PlanSpotOrderDetailDTO getPlanSpotOrder(GetPlanSpotOrderRequest request);

    CancelOrderResponse cancelOrder(CancelOrderRequest request);

    CancelPlanSpotOrderResponse cancelPlanSpotOrder(CancelPlanSpotOrderRequest request);

    BatchCancelPlanSpotOrderResponse batchCancelPlanSpotOrder(BatchCancelPlanSpotOrderRequest request);

    List<OrderDetailDTO> getHistoryOrders(Long orgId, Long accountId, String symbolId, Long startOrderId, Integer pageSize);

    List<PlanSpotOrderDTO> getHistoryPlanSpotOrders(Long orgId, Long accountId, SimpleOrderPO po);

    List<TradeDetailDTO> getCoidOrderTrades(long orgId, Long accountId, Long orderId, String symbolId);

    List<TradeDetailDTO> getCoinTrades(long orgId, Long accountId, String symbolId, Long lastTradeId, Integer pageSize);

    List<QueryOrgTradeDetailResponse.TradeDetail> getContractTrades(QueryOrgTradeDetailRequest request);

    List<TradeDetailInfoDTO> getTradesDetailDesc(Long brokerId,
                                                 Long lastTradeId,
                                                 String symbolId,
                                                 Long accountId,
                                                 Long orderId,
                                                 Long startTime,
                                                 Long endTime,
                                                 Integer limit);

    /**
     * 获取订单薄（独立部署--需要补全baseRequest）
     *
     * @param request
     * @return
     */
    GetBookOrdersResponse getBookOrders(GetBookOrdersRequest request);

    CancelMatchOrderReply cancelBrokerOrders(Long brokerId, long exchangeId, String symbolId);

    CancelMatchOrderReply cancelAccountOrders(Long brokerId, long accountId, String symbolId);


}
