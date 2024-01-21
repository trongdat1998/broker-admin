package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.base.admin.AddContractRequest;
import io.bhex.base.admin.AdminOrgContractServiceGrpc;
import io.bhex.base.admin.ChangeContractRequest;
import io.bhex.base.admin.ListAllContractRequest;
import io.bhex.base.admin.ListContractReply;
import io.bhex.base.admin.ListContractRequest;
import io.bhex.base.admin.OrgType;
import io.bhex.base.admin.UpdateContactInfoRequest;
import io.bhex.bhop.common.config.GrpcConfig;
import io.bhex.broker.admin.grpc.client.ExchangeContractClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.grpc.client.impl
 * @Author: ming.xu
 * @CreateDate: 31/08/2018 11:40 AM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Component
public class ExchangeContractClientImpl  implements ExchangeContractClient {

    @Resource
    GrpcClientConfig grpcConfig;

    private final OrgType ORG_TYPE = OrgType.Broker_Org;

    private AdminOrgContractServiceGrpc.AdminOrgContractServiceBlockingStub getContractStub() {
        return grpcConfig.adminOrgContractServiceBlockingStub(GrpcConfig.SAAS_ADMIN_GRPC_CHANNEL_NAME);
    }

    @Override
    public ListContractReply listExchangeContract(Long brokerId, Integer current, Integer pageSize) {
        ListContractRequest request = ListContractRequest.newBuilder()
                .setOrgId(brokerId)
                .setApplyOrgType(ORG_TYPE)
                .setCurrent(current)
                .setPageSize(pageSize)
                .build();

        ListContractReply reply = getContractStub().listContract(request);
        return reply;
    }

    @Override
    public Boolean reopenExchangeContract(Long brokerId, Long cotractId) {
        ChangeContractRequest request = ChangeContractRequest.newBuilder()
                .setOrgId(brokerId)
                .setApplyOrgType(ORG_TYPE)
                .setContractId(cotractId)
                .build();

        return getContractStub().reopenContract(request).getResult();
    }

//    @Override
//    public Boolean reapplyContract(Long brokerId, Long cotractId) {
//        ChangeContractRequest request = ChangeContractRequest.newBuilder()
//                .setOrgId(brokerId)
//                .setApplyOrgType(ORG_TYPE)
//                .setContractId(cotractId)
//                .build();
//
//        return getContractStub().reapplyContract(request).getResult();
//    }

    @Override
    public Boolean closeExchangeContract(Long brokerId, Long cotractId) {
        ChangeContractRequest request = ChangeContractRequest.newBuilder()
                .setOrgId(brokerId)
                .setApplyOrgType(ORG_TYPE)
                .setContractId(cotractId)
                .build();

        return getContractStub().closeContract(request).getResult();
    }

//    @Override
//    public Boolean addExchangeContract(AddContractRequest request) {
//        return getContractStub().addContract(request).getResult();
//    }

    @Override
    public ListContractReply listApplication(Long brokerId, Integer current, Integer pageSize) {
        ListContractRequest request = ListContractRequest.newBuilder()
                .setOrgId(brokerId)
                .setApplyOrgType(ORG_TYPE)
                .setCurrent(current)
                .setPageSize(pageSize)
                .build();

        return getContractStub().listContractApplication(request);
    }

    @Override
    public Boolean enableApplication(Long brokerId, Long cotractId) {
        ChangeContractRequest request = ChangeContractRequest.newBuilder()
                .setOrgId(brokerId)
                .setApplyOrgType(ORG_TYPE)
                .setContractId(cotractId)
                .build();

        return getContractStub().enableApplication(request).getResult();
    }

    @Override
    public Boolean rejectApplication(Long brokerId, Long cotractId) {
        ChangeContractRequest request = ChangeContractRequest.newBuilder()
                .setOrgId(brokerId)
                .setApplyOrgType(ORG_TYPE)
                .setContractId(cotractId)
                .build();

        return getContractStub().rejectApplication(request).getResult();
    }

    @Override
    public Boolean addApplication(AddContractRequest request) {
        request = request.toBuilder().setApplyOrgType(ORG_TYPE).build();
        return getContractStub().addApplication(request).getResult();
    }

    @Override
    public Boolean editContactInfo(UpdateContactInfoRequest request) {
        request = request.toBuilder().setApplyOrgType(ORG_TYPE).build();
        return getContractStub().updateContactInfo(request).getResult();
    }

    @Override
    public ListContractReply listAllExchangeContractInfo(Long brokerId) {
        ListAllContractRequest request = ListAllContractRequest.newBuilder()
                .setOrgId(brokerId)
                .setApplyOrgType(ORG_TYPE)
                .build();

        ListContractReply reply = getContractStub().listAllContract(request);
        return reply;
    }
}
