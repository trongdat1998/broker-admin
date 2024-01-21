package io.bhex.broker.admin.grpc.client;

import io.bhex.broker.grpc.admin.*;
import io.bhex.broker.grpc.basic.QueryCountryResponse;

import java.util.List;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.grpc.client
 * @Author: ming.xu
 * @CreateDate: 29/09/2018 3:48 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface BrokerConfigClient {

    AdminBrokerConfigDetail getBrokerWholeConfig(GetBrokerConfigRequest request);

    Boolean saveBrokerWholeConfig(SaveBrokerConfigRequest request);

    SaveConfigReply editIndexCustomerConfig(EditIndexCustomerConfigRequest request);

    List<IndexCustomerConfig> getIndexCustomerConfig(GetIndexCustomerConfigRequest  request);

    SaveConfigReply switchIndexCustomerConfig(SwitchIndexCustomerConfigRequest request);

    QueryCountryResponse queryCountries();
}
