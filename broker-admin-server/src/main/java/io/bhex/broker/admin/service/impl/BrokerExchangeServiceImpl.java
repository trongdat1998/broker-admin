package io.bhex.broker.admin.service.impl;

import io.bhex.broker.admin.grpc.client.BrokerExchangeClient;
import io.bhex.broker.admin.service.BrokerExchangeService;
import io.bhex.broker.grpc.admin.ChangeBrokerExchangeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.service.impl
 * @Author: ming.xu
 * @CreateDate: 22/10/2018 3:13 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Service
public class BrokerExchangeServiceImpl implements BrokerExchangeService {

    @Autowired
    BrokerExchangeClient brokerExchangeClient;

    @Override
    public Boolean enableContract(Long brokerId, Long exchangeId, String exchangeName) {
        ChangeBrokerExchangeRequest request = ChangeBrokerExchangeRequest.newBuilder()
                .setBrokerId(brokerId)
                .setExchangeId(exchangeId)
                .setExchangeName(exchangeName)
                .build();

        return brokerExchangeClient.enableContract(request);
    }

    @Override
    public Boolean disableContract(Long brokerId, Long exchangeId, String exchangeName) {
        ChangeBrokerExchangeRequest request = ChangeBrokerExchangeRequest.newBuilder()
                .setBrokerId(brokerId)
                .setExchangeId(exchangeId)
                .setExchangeName(exchangeName)
                .build();

        return brokerExchangeClient.disableContract(request);
    }
}
