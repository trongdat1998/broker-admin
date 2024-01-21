package io.bhex.broker.admin.grpc.client;

import io.bhex.broker.grpc.account.*;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.grpc.client
 * @Author: ming.xu
 * @CreateDate: 10/11/2018 12:16 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface BrokerAccountClient {

    GetBrokerAccountCountResponse getBrokerAccountCount(GetBrokerAccountCountRequest request);

    VerifyBrokerAccountResponse verifyBrokerAccount(VerifyBrokerAccountRequest request);

    GetBrokerAccountListResponse getBrokerAccountList(GetBrokerAccountListRequest request);
}
