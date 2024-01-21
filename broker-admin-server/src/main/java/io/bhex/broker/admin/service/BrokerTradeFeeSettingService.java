package io.bhex.broker.admin.service;

import io.bhex.base.admin.common.BrokerTradeFeeRateReply;
import io.bhex.bhop.common.util.Combo2;
import io.bhex.broker.admin.controller.dto.BrokerTradeMinFeeDTO;
import io.bhex.broker.admin.controller.param.BrokerTradeFeeRes;
import io.bhex.broker.admin.controller.param.UpdateBrokerTradeFeePO;

import java.math.BigDecimal;

/**
 * @Description:
 * @Date: 2018/9/28 上午11:27
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
public interface BrokerTradeFeeSettingService {

    /**
     * 初始化币对费率
     * @param brokerId
     * @param exchangeId
     * @param symbolId
     */
    void addTradeFeeIfNotExisted(Long brokerId, Long exchangeId, String symbolId, Integer category);

    BrokerTradeFeeRes addDefaultTradeFee(Long brokerId, Long exchangeId, String symbolId, Integer category);

    Combo2<Boolean, String> editTradeFee(Long brokerId, Long exchangeId, String symbolId,
                                         BigDecimal makerFeeRate, BigDecimal takerFeeRate, BigDecimal takerRewardToMakerRate, Integer category);

    BrokerTradeFeeRateReply getLatestBrokerTradeFeeSetting(Long brokerId, Long exchangeId, String symbolId);


    BrokerTradeMinFeeDTO getBrokerTradeMinFee(Long exchangeId, Long orgId);
}
