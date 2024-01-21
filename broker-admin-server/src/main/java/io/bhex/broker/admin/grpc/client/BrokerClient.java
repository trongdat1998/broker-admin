package io.bhex.broker.admin.grpc.client;

import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.broker.admin.controller.dto.BrokerInfoDTO;
import io.bhex.broker.admin.controller.param.BrokerInfoPO;
import io.bhex.broker.grpc.admin.BrokerDetail;
import io.bhex.broker.grpc.admin.CreateBrokerRequest;

/**
 * @ProjectName: broker-admin
 * @Package: io.bhex.broker.admin.grpc.client
 * @Author: ming.xu
 * @CreateDate: 16/08/2018 5:02 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface BrokerClient {

    boolean createBroker(CreateBrokerRequest request);

    BrokerDetail getByBrokerId(Long brokerId);

    /**
     * @param brokerId
     * @param enabled  true-enable false-disabled
     * @return
     */
    boolean enableBroker(Long brokerId, boolean enabled);

    boolean updateBroker(Long brokerId, String brokerName, String apiDomain, String privateKey,
                         String publicKey, boolean enabled);

    boolean updateBrokerSignName(Long brokerId, String sign);

    boolean updateBrokerFunctionAndLanguage(Long brokerId, BrokerInfoPO brokerInfoPO, AdminUserReply adminUser);

    BrokerInfoDTO queryBrokerInfoById(Long brokerId);

    /**
     * 更新broker实时间隔
     */
    boolean updateRealtimeInterval(Long brokerId, String realtimeInterval);

    /**
     * 更新broker实时间隔
     */
    boolean updateFilterTopBaseToken(Long brokerId, Boolean filterTopBaseToken);
}
