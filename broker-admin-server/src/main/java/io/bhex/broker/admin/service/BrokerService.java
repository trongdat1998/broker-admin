package io.bhex.broker.admin.service;

import io.bhex.broker.admin.model.Broker;
import io.bhex.broker.grpc.admin.CreateBrokerRequest;

/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.service
 * @Author: ming.xu
 * @CreateDate: 16/08/2018 4:26 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface BrokerService {

    boolean createBroker(CreateBrokerRequest request);

    Broker getById(Long id);

    Broker getByBrokerId(Long brokerId);

    /**
     * @param brokerId
     * @param enabled true-enable false-disabled
     * @return
     */
    boolean enableBroker(Long brokerId, boolean enabled);

    boolean updateBroker(Long brokerId, String brokerName, String apiDomain, String privateKey, String publicKey, boolean enabled);

}
