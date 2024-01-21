package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.base.account.FeeServiceGrpc;
import io.bhex.base.account.GetBrokerTradeMinFeeRequest;
import io.bhex.base.account.GetBrokerTradeMinFeeResponse;
import io.bhex.base.admin.common.*;
import io.bhex.bhop.common.config.GrpcConfig;
import io.bhex.broker.admin.controller.param.UpdateBrokerTradeFeePO;
import io.bhex.broker.admin.grpc.client.BorkerTradeFeeSettingClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Description:
 * @Date: 2018/10/31 下午4:47
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */

@Slf4j
@Service
public class BorkerTradeFeeSettingClientImpl implements BorkerTradeFeeSettingClient {

    @Resource
    GrpcClientConfig grpcConfig;


    @Override
    public BrokerTradeFeeRateReply getLatestBrokerTradeFeeSetting(Long brokerId, Long exchangeId, String symbolId){
        BrokerTradeFeeSettingServiceGrpc.BrokerTradeFeeSettingServiceBlockingStub stub =
                grpcConfig.brokerTradeFeeSettingServiceBlockingStub(GrpcConfig.SAAS_ADMIN_GRPC_CHANNEL_NAME);
        GetLatestBrokerTradeFeeRequest request = GetLatestBrokerTradeFeeRequest.newBuilder()
                .setBrokerId(brokerId)
                .setExchangeId(exchangeId)
                .setSymbolId(symbolId)
                .build();

        BrokerTradeFeeRateReply reply = stub.getLatestBrokerTradeFee(request);

        return reply;
    }


    @Override
    public UpdateBrokerTradeFeeReply updateBrokerTradeFee(Long brokerId, UpdateBrokerTradeFeePO po){
        BrokerTradeFeeSettingServiceGrpc.BrokerTradeFeeSettingServiceBlockingStub stub =
                grpcConfig.brokerTradeFeeSettingServiceBlockingStub(GrpcClientConfig.ADMIN_COMMON_GRPC_CHANNEL_NAME);

        BrokerTradeFeeSettingServiceGrpc.BrokerTradeFeeSettingServiceBlockingStub bhStub =
                grpcConfig.brokerTradeFeeSettingServiceBlockingStub(GrpcClientConfig.SAAS_ADMIN_GRPC_CHANNEL_NAME);

        UpdateBrokerTradeFeeRequest request = UpdateBrokerTradeFeeRequest.newBuilder()
                .setBrokerId(brokerId)
                .setExchangeId(po.getExchangeId())
                .setSymbolId(po.getSymbolId())
                .setMakerFeeRate(po.getMakerFeeRate().toPlainString())
                .setMakerRewardToTakerRate("0")
                .setTakerFeeRate(po.getTakerFeeRate().toPlainString())
                .setTakerRewardToMakerRate(po.getTakerRewardToMakerRate().toPlainString())
                .build();

        UpdateBrokerTradeFeeReply bhReply = bhStub.updateBrokerTradeFee(request);
        log.info("bh-update-reply:{}", bhReply);
        if (!bhReply.getResult()) {
            return bhReply;
        }

        return stub.updateBrokerTradeFee(request);
    }

    @Override
    public GetBrokerTradeMinFeeResponse getBrokerTradeMinFee(GetBrokerTradeMinFeeRequest request) {
        FeeServiceGrpc.FeeServiceBlockingStub stub = grpcConfig.saasFeeServiceBlockingStub(GrpcConfig.SAAS_ADMIN_GRPC_CHANNEL_NAME);

        return stub.getBrokerTradeMinFee(request);
    }


}
