package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.base.account.GetEarnestAddressReply;
import io.bhex.base.account.GetEarnestAddressRequest;
import io.bhex.base.account.OrgServiceGrpc;
import io.bhex.bhop.common.config.GrpcConfig;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class EarnestClient {

    @Resource
    GrpcClientConfig grpcConfig;

    public String getEarnestAddress(Long orgId){
        OrgServiceGrpc.OrgServiceBlockingStub stub = grpcConfig.orgServiceBlockingStub(GrpcConfig.SAAS_ADMIN_GRPC_CHANNEL_NAME);
        GetEarnestAddressRequest request = GetEarnestAddressRequest.newBuilder()
                .setOrgId(orgId)
                .build();
        GetEarnestAddressReply reply = stub.getEarnestAddress(request);
        return reply.getAddress();
    }

//    public Long getEarnestAccountId(Long brokerId){
//        AccountServiceGrpc.AccountServiceBlockingStub stub = AccountServiceGrpc.newBlockingStub(bhChannel);
//        GetAccountIdByOrgIdAndAccountTypeRequest request = GetAccountIdByOrgIdAndAccountTypeRequest.newBuilder()
//                .setOrgId(brokerId)
//                .setAccountType(AccountType.EXCHANGE_EARNEST_ACCOUNT)
//                .build();
//        GetAccountIdByOrgIdAndAccountTypeReply reply = stub.getAccountIdByOrgIdAndAccountType(request);
//        if(reply == null || reply.getAccountId() == 0){
//            return 0L;
//        }
//        return reply.getAccountId();
//    }

}
