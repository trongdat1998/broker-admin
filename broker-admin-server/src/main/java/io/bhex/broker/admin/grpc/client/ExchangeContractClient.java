package io.bhex.broker.admin.grpc.client;

import io.bhex.base.admin.AddContractRequest;
import io.bhex.base.admin.ListContractReply;
import io.bhex.base.admin.UpdateContactInfoRequest;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.grpc.client
 * @Author: ming.xu
 * @CreateDate: 31/08/2018 11:39 AM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface ExchangeContractClient {

    ListContractReply listExchangeContract(Long brokerId, Integer current, Integer pageSize);

    Boolean reopenExchangeContract(Long brokerId, Long cotractId);

//    Boolean reapplyContract(Long brokerId, Long cotractId);

    Boolean closeExchangeContract(Long brokerId, Long cotractId);

//    Boolean addExchangeContract(AddContractRequest request);

    ListContractReply listApplication(Long brokerId, Integer current, Integer pageSize);

    Boolean enableApplication(Long brokerId, Long cotractId);

    Boolean rejectApplication(Long brokerId, Long cotractId);

    Boolean addApplication(AddContractRequest request);

    Boolean editContactInfo(UpdateContactInfoRequest request);

    ListContractReply listAllExchangeContractInfo(Long brokerId);
}
