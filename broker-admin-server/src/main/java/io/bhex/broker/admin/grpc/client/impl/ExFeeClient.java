package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.base.account.FeeServiceGrpc;
import io.bhex.base.account.GetBrokerTradeMinFeeRequest;
import io.bhex.base.account.GetBrokerTradeMinFeeResponse;
import io.bhex.base.account.GetExCommissionFeeRequest;
import io.bhex.base.account.GetExCommissionFeeResponse;
import io.bhex.base.account.GetMatchCommissionFeeRequest;
import io.bhex.base.account.GetMatchCommissionFeeResponse;
import io.bhex.base.account.UpdateBrokerTradeMinFeeRequest;
import io.bhex.base.account.UpdateBrokerTradeMinFeeResponse;
import io.bhex.base.account.UpdateExCommissionFeeRequest;
import io.bhex.base.account.UpdateExCommissionFeeResponse;
import io.bhex.base.account.UpdateMatchCommissionFeeRequest;
import io.bhex.base.account.UpdateMatchCommissionFeeResponse;
import io.bhex.bhop.common.config.GrpcConfig;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ProjectName: exchange
 * @Package: io.bhex.ex.admin.grpc.client.impl
 * @Author: ming.xu
 * @CreateDate: 16/11/2018 10:39 AM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Slf4j
@Service
public class ExFeeClient {

    @Resource
    GrpcClientConfig grpcConfig;

    private FeeServiceGrpc.FeeServiceBlockingStub getFeeStup() {
        return grpcConfig.saasFeeServiceBlockingStub(GrpcConfig.SAAS_ADMIN_GRPC_CHANNEL_NAME);
    }

    public GetExCommissionFeeResponse getExCommissionFee(GetExCommissionFeeRequest request) {
        return getFeeStup().getExCommissionFee(request);
    }

    public UpdateExCommissionFeeResponse updateExCommissionFee(UpdateExCommissionFeeRequest request){
        return getFeeStup().updateExCommissionFee(request);
    }

    public GetMatchCommissionFeeResponse getMatchCommissionFee(GetMatchCommissionFeeRequest request) {
        return getFeeStup().getMatchCommissionFee(request);
    }

    public UpdateMatchCommissionFeeResponse updateMatchCommissionFee(UpdateMatchCommissionFeeRequest request) {
        return getFeeStup().updateMatchCommissionFee(request);
    }

    public GetBrokerTradeMinFeeResponse getBrokerTradeMinFee(GetBrokerTradeMinFeeRequest request) {
        return getFeeStup().getBrokerTradeMinFee(request);
    }

    public UpdateBrokerTradeMinFeeResponse updateBrokerTradeMinFee(UpdateBrokerTradeMinFeeRequest request){
        return getFeeStup().updateBrokerTradeMinFee(request);
    }
}
