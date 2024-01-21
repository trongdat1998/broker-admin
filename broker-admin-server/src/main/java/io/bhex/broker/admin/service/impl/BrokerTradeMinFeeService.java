package io.bhex.broker.admin.service.impl;

import io.bhex.base.account.GetBrokerTradeMinFeeRequest;
import io.bhex.base.account.GetBrokerTradeMinFeeResponse;
import io.bhex.base.account.UpdateBrokerTradeMinFeeRequest;
import io.bhex.base.account.UpdateBrokerTradeMinFeeResponse;
import io.bhex.base.proto.DecimalUtil;
import io.bhex.broker.admin.controller.dto.BrokerTradeMinFeeDTO;
import io.bhex.broker.admin.controller.param.BrokerTradeMinFeePO;
import io.bhex.broker.admin.grpc.client.impl.ExFeeClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ProjectName: exchange
 * @Package: io.bhex.ex.admin.service
 * @Author: ming.xu
 * @CreateDate: 16/11/2018 10:23 AM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Slf4j
@Service
public class BrokerTradeMinFeeService {

    @Autowired
    private ExFeeClient exFeeClient;

    /**
     * 获取 - 券商费率最小值限制信息
     *
     * @param exchangeId
     * @return
     */
    public BrokerTradeMinFeeDTO getBrokerTradeMinFee(Long exchangeId) {
        GetBrokerTradeMinFeeRequest request = GetBrokerTradeMinFeeRequest.newBuilder()
                .setExchangeId(exchangeId)
                .build();
        GetBrokerTradeMinFeeResponse brokerTradeMinFee = exFeeClient.getBrokerTradeMinFee(request);
        return BrokerTradeMinFeeDTO.builder()
                .exchangeId(exchangeId)
                .makerFeeRate(DecimalUtil.toBigDecimal(brokerTradeMinFee.getMakerFeeRate()))
                .takerFeeRate(DecimalUtil.toBigDecimal(brokerTradeMinFee.getTakerFeeRate()))
                .makerBonusRate(DecimalUtil.toBigDecimal(brokerTradeMinFee.getMakerBonusRate()))
                .build();
    }


    /**
     * 更新 - 券商费率最小值限制信息
     *
     * @param param
     * @return
     */
    public int updateBrokerTradeMinfee(BrokerTradeMinFeePO param) {
        UpdateBrokerTradeMinFeeRequest request = UpdateBrokerTradeMinFeeRequest.newBuilder()
                .setExchangeId(param.getExchangeId())
                .setMakerFeeRate(DecimalUtil.fromBigDecimal(param.getMakerFeeRate()))
                .setTakerFeeRate(DecimalUtil.fromBigDecimal(param.getTakerFeeRate()))
                .setMakerBonusRate(DecimalUtil.fromBigDecimal(param.getMakerBonusRate()))
                .build();

        UpdateBrokerTradeMinFeeResponse response = exFeeClient.updateBrokerTradeMinFee(request);
        return response.getErrCode();

    }
}
