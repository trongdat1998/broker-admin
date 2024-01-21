//package io.bhex.broker.admin.util;
//
//import feign.Feign;
//import feign.jackson.JacksonDecoder;
//import feign.jackson.JacksonEncoder;
//import io.bhex.broker.admin.http.ExchangeHttpClient;
//import io.bhex.broker.admin.http.SaasHttpClient;
//import io.bhex.broker.admin.http.SaasResultBean;
//import io.bhex.broker.admin.http.param.GetExchangeInstanceInfoRes;
//
//import java.util.Map;
//
///**
// * @ProjectName: broker
// * @Package: io.bhex.broker.admin.util
// * @Author: ming.xu
// * @CreateDate: 03/09/2018 6:45 PM
// * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
// */
//public class HttpClientUtil {
//
//    public static SaasHttpClient getSaasClient() {
//        String SAAS_HOST = "saas-admin-server.bhop";
//        Integer SAAS_PORT = 7501;
//        String saasUrl = String.format("http://%s:%d/", SAAS_HOST, SAAS_PORT);
//        return Feign.builder()
//                .encoder(new JacksonEncoder())
//                .decoder(new JacksonDecoder())
//                .target(SaasHttpClient.class, saasUrl);
//
//    }
//
//    public static ExchangeHttpClient getExchangeClient(Long exchangeId) {
//        SaasHttpClient saasHttpClient = getSaasClient();
//
//        GetExchangeInstanceInfoRes httpParam = GetExchangeInstanceInfoRes.builder()
//                .exchangeId(exchangeId)
//                .build();
//        SaasResultBean<Map<String, String>> result = saasHttpClient.getExchangeInstanceInfo(httpParam);
//        if(result.getCode() == 0) {
//            String host = result.getData().get("host");
//            Long port = Long.parseLong(result.getData().get("port"));
//
//            String brokerUrl = String.format("http://%s:%d/", host, port);
//            return Feign.builder()
//                    .encoder(new JacksonEncoder())
//                    .decoder(new JacksonDecoder())
//                    .target(ExchangeHttpClient.class, brokerUrl);
//        }
//        return null;
//    }
//}
