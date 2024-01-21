package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.broker.admin.grpc.client.ContractApplicationClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.admin.AddApplicationRequest;
import io.bhex.broker.grpc.admin.AdminContractApplicationServiceGrpc;
import io.bhex.broker.grpc.admin.ChangeApplicationRequest;
import io.bhex.broker.grpc.admin.ListApplicationReply;
import io.bhex.broker.grpc.admin.ListApplicationRequest;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.grpc.client.impl
 * @Author: ming.xu
 * @CreateDate: 31/08/2018 11:56 AM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Component
public class ContractApplicationClientImpl implements ContractApplicationClient {

    @Resource
    GrpcClientConfig grpcConfig;

    private AdminContractApplicationServiceGrpc.AdminContractApplicationServiceBlockingStub getApplictionStub() {
        return grpcConfig.adminContractApplicationServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    @Override
    public ListApplicationReply listApplication(Long brokerId, Integer current, Integer pageSize) {
        ListApplicationRequest request = ListApplicationRequest.newBuilder()
                .setBrokerId(brokerId)
                .setCurrent(current)
                .setPageSize(pageSize)
                .build();

        ListApplicationReply reply = getApplictionStub().listApplication(request);
        return reply;
    }

    @Override
    public Boolean enableContractApplication(Long brokerId, Long applicationId) {
        ChangeApplicationRequest request = ChangeApplicationRequest.newBuilder()
                .setBrokerId(brokerId)
                .setApplicationId(applicationId)
                .build();

        return getApplictionStub().enableApplication(request).getResult();
    }

    @Override
    public Boolean rejectContractApplication(Long brokerId, Long applicationId) {
        ChangeApplicationRequest request = ChangeApplicationRequest.newBuilder()
                .setBrokerId(brokerId)
                .setApplicationId(applicationId)
                .build();

        return getApplictionStub().rejectApplication(request).getResult();
    }

    @Override
    public Boolean addContractApplication(AddApplicationRequest request) {
        return getApplictionStub().addApplication(request).getResult();
    }
}
