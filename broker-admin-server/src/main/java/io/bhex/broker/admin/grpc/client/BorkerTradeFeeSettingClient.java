package io.bhex.broker.admin.grpc.client;

import io.bhex.base.account.GetBrokerTradeMinFeeRequest;
import io.bhex.base.account.GetBrokerTradeMinFeeResponse;
import io.bhex.base.admin.common.*;
import io.bhex.broker.admin.controller.param.UpdateBrokerTradeFeePO;

/**
 * @Description:
 * @Date: 2018/10/31 下午4:53
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
public interface BorkerTradeFeeSettingClient {


    BrokerTradeFeeRateReply getLatestBrokerTradeFeeSetting(Long brokerId, Long exchangeId, String symbolId);

    UpdateBrokerTradeFeeReply updateBrokerTradeFee(Long brokerId, UpdateBrokerTradeFeePO po);

    /**
     * 获取最小交易费率（独立部署--补全baseRequest）
     *
     * @param request
     * @return
     */
    GetBrokerTradeMinFeeResponse getBrokerTradeMinFee(GetBrokerTradeMinFeeRequest request);


}
