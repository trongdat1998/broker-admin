package io.bhex.broker.admin.http;

import feign.Headers;
import feign.RequestLine;
import io.bhex.broker.admin.http.param.ChangeExchangeContractRes;

import java.util.Map;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.http
 * @Author: ming.xu
 * @CreateDate: 03/09/2018 6:50 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface ExchangeHttpClient {

    @RequestLine("POST /api/v1/internal/contract/reopen")
    @Headers("Content-Type: application/json")
    ExchangeResultBean reopenContract(ChangeExchangeContractRes param);

    @RequestLine("POST /api/v1/internal/contract/close")
    @Headers("Content-Type: application/json")
    ExchangeResultBean closeContract(ChangeExchangeContractRes param);

    @RequestLine("POST /api/v1/internal/contract/reject")
    @Headers("Content-Type: application/json")
    ExchangeResultBean rejectApplication(ChangeExchangeContractRes param);

    @RequestLine("POST /api/v1/internal/contract/enable")
    @Headers("Content-Type: application/json")
    ExchangeResultBean enableApplication(ChangeExchangeContractRes param);


//    @RequestLine("POST /api/v1/internal/trade_fee_setting/sync_broker_trade_fee")
//    @Headers("Content-Type: application/json")
//    ExchangeResultBean syncBrokerTradeFee(SyncBrokerTradeFeePO param);

}
