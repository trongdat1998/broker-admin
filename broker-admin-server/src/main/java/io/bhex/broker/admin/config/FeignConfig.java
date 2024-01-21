package io.bhex.broker.admin.config;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import io.bhex.bhop.common.config.OrgInstanceConfig;
import io.bhex.bhop.common.dto.param.ExchangeInstanceRes;
import io.bhex.broker.admin.http.ExchangeHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FeignConfig {


    @Autowired
    private OrgInstanceConfig orgInstanceConfig;


    public ExchangeHttpClient getExchangeClient(Long exchangeId) {
        ExchangeInstanceRes res = orgInstanceConfig.getExchangeInstance(exchangeId);
        if(res == null) {
            return null;
        }
        return Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder())
                .target(ExchangeHttpClient.class, res.getAdminInternalApiUrl());
    }



}
