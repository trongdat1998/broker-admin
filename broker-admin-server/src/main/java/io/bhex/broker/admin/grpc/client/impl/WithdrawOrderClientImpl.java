package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.base.account.*;
import io.bhex.base.proto.BaseRequest;
import io.bhex.bhop.common.config.GrpcConfig;
import io.bhex.broker.admin.constants.GrpcConstant;
import io.bhex.broker.admin.grpc.client.WithdrawOrderClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.admin.*;
import io.bhex.broker.grpc.statistics.QueryOrgWithdrawOrderRequest;
import io.bhex.broker.grpc.statistics.QueryOrgWithdrawOrderResponse;
import io.bhex.broker.grpc.withdraw.WithdrawServiceGrpc;
import io.grpc.Deadline;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Description:
 * @Date: 2018/9/19 下午6:15
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Slf4j
@Service
public class WithdrawOrderClientImpl implements WithdrawOrderClient {

    @Resource
    GrpcClientConfig grpcConfig;

    private AdminWithdrawOrderServiceGrpc.AdminWithdrawOrderServiceBlockingStub getAdminWithdrawOrderStub() {
        return grpcConfig.adminWithdrawOrderServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    private WithdrawServiceGrpc.WithdrawServiceBlockingStub getBrokerWithdrawOrderStub() {
        return grpcConfig.withdrawServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME)
                .withDeadline(Deadline.after(GrpcConstant.DURATION_10_SECONDS, GrpcConstant.TIME_UNIT));
    }

    @Override
    public QueryUnverfiedOrdersResponse queryUnverfiedOrders(Long brokerId, Long accountId, Long fromId, Long endId, Integer pageSize) {
        QueryUnverfiedOrdersRequest request = QueryUnverfiedOrdersRequest.newBuilder()
                .setBrokerId(brokerId)
                .setAccountId(accountId)
                .setFromId(fromId)
                .setEndId(endId)
                .setLimit(pageSize)
                .build();
        QueryUnverfiedOrdersResponse response = getAdminWithdrawOrderStub().queryUnverfiedOrders(request);
        return response;
    }


    @Override
    public GetBrokerAuditingResponse queryUnverfiedOrdersFromBh(Long brokerId, Long accountId, Long fromId, Long endId, Integer pageSize) {
        GetBrokerAuditingRequest request = GetBrokerAuditingRequest.newBuilder()
                .setOrgId(brokerId)
                .setAccountId(accountId)
                .setFromOrderId(fromId)
                .setEndOrderId(endId)
                .setLimit(pageSize)
                .build();
        return grpcConfig.withdrawAdminServiceBlockingStub(GrpcClientConfig.BH_SERVER_CHANNEL_NAME).getBrokerAuditing(request);
    }

    @Override
    public GetWithdrawalOrderResponse getWithdrawalOrderFromBh(GetWithdrawalOrderRequest request) {
        return grpcConfig.withdrawalServiceBlockingStub(GrpcClientConfig.BH_SERVER_CHANNEL_NAME).getWithdrawalOrder(request);
    }

    @Override
    public VerifyOrderResponse verify(Long brokerId, Long userId, Long withdrawOrderId, boolean verifyPassed, String remark
            , String adminUserName, Integer failedReason, String refuseReason) {
        VerifyOrderRequest request = VerifyOrderRequest.newBuilder()
                .setBrokerId(brokerId)
                .setUserId(userId)
                .setWithdrawOrderId(withdrawOrderId)
                .setVerifyPassed(verifyPassed)
                .setRemark(remark)
                .setAdminUserName(adminUserName)
                .setFailedReason(failedReason)
                .setRefuseReason(refuseReason)
                .build();
        VerifyOrderResponse response = getAdminWithdrawOrderStub().verifyOrder(request);
        log.info("VerifyOrderRequest result = {}", response);
        return response;
    }

    @Override
    public QueryVerfiedOrdersResponse queryVerfiedOrders(Long brokerId, Long accountId, Long fromId, Long endId, Integer pageSize) {

        QueryVerfiedOrdersRequest request = QueryVerfiedOrdersRequest.newBuilder()
                .setBrokerId(brokerId)
                .setAccountId(accountId)
                .setFromId(fromId)
                .setEndId(endId)
                .setLimit(pageSize)
                .build();
        QueryVerfiedOrdersResponse response = getAdminWithdrawOrderStub().queryVerfiedOrders(request);

        return response;
    }

    @Override
    public QueryWithdrawOrderResponse queryWithdrawOrder(Long brokerId, Long withdrawOrderId) {

        QueryWithdrawOrderRequest request = QueryWithdrawOrderRequest.newBuilder()
                .setBrokerId(brokerId)
                .setWithdrawOrderId(withdrawOrderId)
                .build();

        return getAdminWithdrawOrderStub().queryWithdrawOrder(request);
    }

    @Override
    public SetWithdrawalAuditStatusResponse setWithdrawalAuditStatus(Long brokerId, Long accountId, Long bhWithdrawOrderId
            , boolean passed, Integer failedReason) {

        WithdrawalServiceGrpc.WithdrawalServiceBlockingStub stub = grpcConfig.withdrawalServiceBlockingStub(GrpcConfig.SAAS_ADMIN_GRPC_CHANNEL_NAME);

        BaseRequest baseRequest = BaseRequest.newBuilder()
                .setOrganizationId(brokerId)
                .setRequestTime(System.currentTimeMillis())
                .setInvokeType(BaseRequest.InvokerTypeEnum.USER)
                .build();
        WithdrawalBrokerAuditEnum auditStatus = passed ? WithdrawalBrokerAuditEnum.PASS : WithdrawalBrokerAuditEnum.NO_PASS;

        SetWithdrawalAuditStatusRequest request = SetWithdrawalAuditStatusRequest.newBuilder()
                .setBaseRequest(baseRequest)
                .setWithdrawalOrderId(bhWithdrawOrderId)
                .setAuditStatus(auditStatus)
                .setAccountId(accountId)
                .setFailedReason(failedReason)
                .build();


        SetWithdrawalAuditStatusResponse response = stub.setWithdrawalAuditStatus(request);
        return response;
    }

    @Override
    public QueryOrgWithdrawOrderResponse queryOrgWithdrawOrder(QueryOrgWithdrawOrderRequest request) {
        return grpcConfig.statisticsServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).queryOrgWithdrawOrder(request);
    }

    @Override
    public QueryOrdersReply queryWithdrawOrderFromBh(io.bhex.base.account.QueryOrdersRequest request) {
        return grpcConfig.withdrawAdminServiceBlockingStub(GrpcClientConfig.BH_SERVER_CHANNEL_NAME).queryOrders(request);
    }
}
