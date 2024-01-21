package io.bhex.broker.admin.service.impl;

import io.bhex.broker.admin.grpc.client.BrokerClient;
import io.bhex.broker.admin.model.Broker;
import io.bhex.broker.admin.service.BrokerService;
import io.bhex.broker.grpc.admin.CreateBrokerRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.service.impl
 * @Author: ming.xu
 * @CreateDate: 16/08/2018 5:02 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Service
public class BrokerServiceImpl implements BrokerService {

    @Autowired
    private BrokerClient brokerClient;

    @Override
    public boolean createBroker(CreateBrokerRequest request) {
         return brokerClient.createBroker(request);
    }

    @Override
    public Broker getById(Long id) {
        return null;
    }

    @Override
    public Broker getByBrokerId(Long brokerId) {
        return null;
    }

    @Override
    public boolean enableBroker(Long brokerId, boolean enabled) {
        return brokerClient.enableBroker(brokerId, enabled);
    }

    @Override
    public boolean updateBroker(Long brokerId, String brokerName, String apiDomain,
                                String privateKey, String publicKey, boolean enabled) {
        return brokerClient.updateBroker(brokerId, brokerName, apiDomain, privateKey, publicKey, enabled);
    }
}
