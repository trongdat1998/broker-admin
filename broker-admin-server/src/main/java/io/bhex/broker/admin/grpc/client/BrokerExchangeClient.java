package io.bhex.broker.admin.grpc.client;

import io.bhex.broker.grpc.admin.ChangeBrokerExchangeRequest;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.grpc.client
 * @Author: ming.xu
 * @CreateDate: 22/10/2018 3:05 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface BrokerExchangeClient {

    Boolean enableContract(ChangeBrokerExchangeRequest request);

    Boolean disableContract(ChangeBrokerExchangeRequest request);
}
