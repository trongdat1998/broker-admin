package io.bhex.broker.admin.grpc.client;

import io.bhex.broker.grpc.admin.AddApplicationRequest;
import io.bhex.broker.grpc.admin.ListApplicationReply;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.grpc.client
 * @Author: ming.xu
 * @CreateDate: 31/08/2018 11:56 AM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface ContractApplicationClient {

    ListApplicationReply listApplication(Long brokerId, Integer current, Integer pageSize);

    Boolean enableContractApplication(Long brokerId, Long applicationId);

    Boolean rejectContractApplication(Long brokerId, Long applicationId);

    Boolean addContractApplication(AddApplicationRequest request);
}
