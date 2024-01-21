package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.bhop.common.exception.BizException;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.common.exception.BrokerErrorCode;
import io.bhex.broker.common.exception.BrokerException;
import io.bhex.broker.grpc.order.FuturesOrderServiceGrpc;
import io.bhex.broker.grpc.order.FuturesPosition;
import io.bhex.broker.grpc.order.FuturesPositionsRequest;
import io.bhex.broker.grpc.order.FuturesPositionsResponse;
import io.bhex.broker.grpc.order.GetOrderMatchRequest;
import io.bhex.broker.grpc.order.GetOrderMatchResponse;
import io.bhex.broker.grpc.order.GetOrderRequest;
import io.bhex.broker.grpc.order.GetOrderResponse;
import io.bhex.broker.grpc.order.QueryFuturesOrdersRequest;
import io.bhex.broker.grpc.order.QueryFuturesOrdersResponse;
import io.bhex.broker.grpc.order.QueryLiquidationPositionRequest;
import io.bhex.broker.grpc.order.QueryLiquidationPositionResponse;
import io.bhex.broker.grpc.order.QueryMatchRequest;
import io.bhex.broker.grpc.order.QueryMatchResponse;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ProjectName: broker
 * @Package: io.bhex.bhop.common.grpc.client.impl
 * @Author: ming.xu
 * @CreateDate: 2019/9/18 5:47 PM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
@Slf4j
@Service
public class GrpcFuturesOrderService {

    @Resource
    GrpcClientConfig grpcConfig;

    private FuturesOrderServiceGrpc.FuturesOrderServiceBlockingStub getFuturesOrderStub() {
        return grpcConfig.futuresOrderServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    public QueryFuturesOrdersResponse queryFuturesOrders(QueryFuturesOrdersRequest request) {
        try {
            QueryFuturesOrdersResponse response = getFuturesOrderStub().queryFuturesOrders(request);
            if (response.getRet() != 0) {
                throw new BrokerException(BrokerErrorCode.fromCode(response.getRet()));
            }
            return response;
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
            throw new BizException(ErrorCode.ERROR);
        }
    }

    public FuturesPositionsResponse getFuturesPositions(FuturesPositionsRequest request) {
        try {
            FuturesPositionsResponse response = getFuturesOrderStub().getFuturesPositions(request);
            if (response.getRet() != 0) {
                throw new BrokerException(BrokerErrorCode.fromCode(response.getRet()));
            }
            return response;
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
            throw new BizException(ErrorCode.ERROR);
        }
    }

    public QueryMatchResponse queryFuturesMatchInfo(QueryMatchRequest request) {
        try {
            QueryMatchResponse response = getFuturesOrderStub().queryFuturesMatch(request);
            if (response.getRet() != 0) {
                throw new BrokerException(BrokerErrorCode.fromCode(response.getRet()));
            }
            return response;
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
            throw new BizException(ErrorCode.ERROR);
        }
    }

    public GetOrderMatchResponse getFuturesOrderMatchInfo(GetOrderMatchRequest request) {
        try {
            GetOrderMatchResponse response = getFuturesOrderStub().getFuturesOrderMatch(request);
            if (response.getRet() != 0) {
                throw new BrokerException(BrokerErrorCode.fromCode(response.getRet()));
            }
            return response;
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
            throw new BizException(ErrorCode.ERROR);
        }
    }


    public GetOrderResponse getFuturesOrder(GetOrderRequest request) {
        try {
            GetOrderResponse response = getFuturesOrderStub().getFuturesOrder(request);
            if (response.getRet() != 0) {
                throw new BrokerException(BrokerErrorCode.fromCode(response.getRet()));
            }
            return response;
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
            throw new BizException(ErrorCode.ERROR);
        }
    }


    public FuturesPosition getLiquidationPosition(QueryLiquidationPositionRequest request) {
        try {
            QueryLiquidationPositionResponse response = getFuturesOrderStub().queryLiquidationPosition(request);
            if (response.getRet() != 0) {
                throw new BrokerException(BrokerErrorCode.fromCode(response.getRet()));
            }
            return response.getFuturesPosition();
        } catch (StatusRuntimeException e) {
            log.error("{}", e);
            throw new BizException(ErrorCode.ERROR);
        }
    }
}
