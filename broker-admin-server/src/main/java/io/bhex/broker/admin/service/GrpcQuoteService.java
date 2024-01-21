package io.bhex.broker.admin.service;

import io.bhex.base.quote.*;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.common.grpc.client.annotation.PrometheusMetrics;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.service
 * @Author: ming.xu
 * @CreateDate: 2019/12/26 2:52 PM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
@Service
@Slf4j
@PrometheusMetrics
public class GrpcQuoteService {

    @Resource
    GrpcClientConfig grpcConfig;

    private QuoteServiceGrpc.QuoteServiceBlockingStub getStub() {
        return grpcConfig.quoteServiceBlockingStub(GrpcClientConfig.QUOTE_CHANNEL_NAME);
    }

    public GetLegalCoinRatesReply getRates(GetRatesRequest request) {
        try {
            return getStub().getRatesV2(request);
        } catch (StatusRuntimeException e) {
            log.error(" getRates exception:{}", e);
            return null;
        }
    }
}
