/*
 ************************************
 * @项目名称: api-parent
 * @文件名称: GrpcOptionOrderService
 * @Date 2019/01/09
 * @Author will.zhao@bhex.io
 * @Copyright（C）: 2018 BlueHelix Inc.   All rights reserved.
 * 注意：本内容仅限于内部传阅，禁止外泄以及用于其他的商业目的。
 **************************************
 */
package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.order.GetOrderMatchRequest;
import io.bhex.broker.grpc.order.GetOrderMatchResponse;
import io.bhex.broker.grpc.order.GetOrderRequest;
import io.bhex.broker.grpc.order.GetOrderResponse;
import io.bhex.broker.grpc.order.HistoryOptionRequest;
import io.bhex.broker.grpc.order.HistoryOptionsResponse;
import io.bhex.broker.grpc.order.OptionPositionsRequest;
import io.bhex.broker.grpc.order.OptionPositionsResponse;
import io.bhex.broker.grpc.order.OptionSettlementRequest;
import io.bhex.broker.grpc.order.OptionSettlementResponse;
import io.bhex.broker.grpc.order.OrderServiceGrpc;
import io.bhex.broker.grpc.order.QueryMatchRequest;
import io.bhex.broker.grpc.order.QueryMatchResponse;
import io.bhex.broker.grpc.order.QueryOrdersRequest;
import io.bhex.broker.grpc.order.QueryOrdersResponse;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
public class GrpcOptionOrderService {

    @Resource
    GrpcClientConfig grpcConfig;

    private OrderServiceGrpc.OrderServiceBlockingStub getOptionOrderStub() {
        return grpcConfig.brokerOrderServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    public GetOrderResponse getOptionOrder(GetOrderRequest request) {
        try {
            GetOrderResponse response = getOptionOrderStub().getOptionOrder(request);
            if (response.getRet() != 0) {
                log.error("getOptionOrder error. error code: {}", response.getRet());
                response = GetOrderResponse.newBuilder().build();
            }
            return response;
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        }
        return null;
    }

    public QueryOrdersResponse queryOptionOrders(QueryOrdersRequest request) {
        try {
            QueryOrdersResponse response = getOptionOrderStub().queryOptionOrders(request);
            if (response.getRet() != 0) {
                log.error("queryOptionOrders error. error code: {}", response.getRet());
                response = QueryOrdersResponse.newBuilder().build();
            }
            return response;
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        }
        return null;
    }

    public QueryMatchResponse queryOptionMatchInfo(QueryMatchRequest request) {
        try {
            QueryMatchResponse response = getOptionOrderStub().queryOptionMatch(request);
            if (response.getRet() != 0) {
                log.error("queryOptionMatchInfo error. error code: {}", response.getRet());
                response = QueryMatchResponse.newBuilder().build();
            }
            return response;
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        }
        return null;
    }

    public GetOrderMatchResponse getOptionOrderMatchInfo(GetOrderMatchRequest request) {
        try {
            GetOrderMatchResponse response = getOptionOrderStub().getOptionOrderMatch(request);
            if (response.getRet() != 0) {
                log.error("getOptionOrderMatchInfo error. error code: {}", response.getRet());
                response = GetOrderMatchResponse.newBuilder().build();
            }
            return response;
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        }
        return null;
    }

    public OptionPositionsResponse getOptionPositions(OptionPositionsRequest request) {
        try {
            OptionPositionsResponse response = getOptionOrderStub().getOptionPositions(request);
            return response;
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        }
        return null;
    }

    public OptionSettlementResponse getOptionSettlement(OptionSettlementRequest request) {
        try {
            OptionSettlementResponse response = getOptionOrderStub().getOptionSettlement(request);
            return response;
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        }
        return null;
    }

    public HistoryOptionsResponse getHistoryOptions(HistoryOptionRequest request) {
        try {
            HistoryOptionsResponse response = getOptionOrderStub().getHistoryOptions(request);
            if (response.getRet() != 0) {
                log.error("getHistoryOptions error. error code: {}", response.getRet());
                response = HistoryOptionsResponse.newBuilder().build();
            }
            return response;
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
        }
        return null;
    }
}
