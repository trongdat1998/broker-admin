package io.bhex.broker.admin.service;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.service
 * @Author: ming.xu
 * @CreateDate: 22/10/2018 3:13 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface BrokerExchangeService {

    Boolean enableContract(Long brokerId, Long exchangeId, String exchangeName);

    Boolean disableContract(Long brokerId, Long exchangeId, String exchangeName);
}
