//package io.bhex.broker.admin.http;
//
//import feign.Headers;
//import feign.RequestLine;
//import io.bhex.broker.admin.http.param.BrokerInstanceRes;
//import io.bhex.broker.admin.http.param.ExchangeInstanceRes;
//import io.bhex.broker.admin.http.param.GetExchangeInstanceInfoRes;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * @ProjectName: broker
// * @Package: io.bhex.broker.admin.http
// * @Author: ming.xu
// * @CreateDate: 03/09/2018 6:46 PM
// * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
// */
//public interface SaasHttpClient {
//
//    @RequestLine("POST /api/v1/instance/exchange/host_info")
//    @Headers("Content-Type: application/json")
//    public SaasResultBean<Map<String, String>> getExchangeInstanceInfo(GetExchangeInstanceInfoRes param);
//
//    @RequestLine("GET /api/v1/instance/exchange_list")
//    @Headers("Content-Type: application/json")
//    SaasResultBean<List<ExchangeInstanceRes>> getExchangeInstanceList();
//
//    @RequestLine("GET /api/v1/instance/broker_list")
//    @Headers("Content-Type: application/json")
//    SaasResultBean<List<BrokerInstanceRes>> getBrokerInstanceList();
//}
