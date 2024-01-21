package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.broker.admin.grpc.client.UserVerifyClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.admin.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.grpc.client.impl
 * @Author: ming.xu
 * @CreateDate: 24/08/2018 5:49 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Service
public class UserVerifyClientImpl implements UserVerifyClient {

    @Resource
    GrpcClientConfig grpcConfig;

    private AdminUserVerifyServiceGrpc.AdminUserVerifyServiceBlockingStub getUserVerifyStub() {
        return grpcConfig.adminUserVerifyServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    @Override
    public ListUnverifiedUserReply queryUnverifyiedUser(Integer current, Integer pageSize, Long brokerId, Long userId, String locale) {
        if (null == userId) {
            userId = 0L;
        }

        QueryUnverifiedUserRequest request = QueryUnverifiedUserRequest.newBuilder()
                .setCurrent(current)
                .setPageSize(pageSize)
                .setBrokerId(brokerId)
                .setUserId(userId)
                .setLocale(locale)
                .build();

        return getUserVerifyStub().queryUnverifiedUser(request);
    }

    @Override
    public QueryUserVerifyListReply queryUserVerifyList(QueryUserVerifyListRequest request) {
        return getUserVerifyStub().queryUserVerifyList(request);
    }

    @Override
    public UserVerifyDetail getVerifyUserById(Long userVerifyId, Long brokerId, String locale, boolean decryptUrl) {
        GetVerifyUserRequest request = GetVerifyUserRequest.newBuilder()
                .setUserVerifyId(userVerifyId)
                .setBrokerId(brokerId)
                .setLocale(locale)
                .setDecryptUrl(decryptUrl)
                .build();

        return getUserVerifyStub().getUnverifiedUser(request);
    }

    @Override
    public UpdateVerifyUserReply updateVerifyUser(Long brokerId, Long adminUserId, Long userVerifyId, int verifyStatus, Long reasonId, String remark) {
        if (null == reasonId) {
            reasonId = 0L;
        }
        if (StringUtils.isEmpty(remark)) {
            remark = new String();
        }
        UpdateVerifyUserRequest request = UpdateVerifyUserRequest.newBuilder()
                .setBrokerId(brokerId)
                .setAdminUserId(adminUserId)
                .setUserVerifyId(userVerifyId)
                .setReasonId(reasonId)
                .setRemark(remark)
                .setVerifyStatus(verifyStatus)
                .build();

        return getUserVerifyStub().updateVerifyUser(request);
    }

    @Override
    public ListVerifyReasonReply listVerifyReason(String locale) {
        ListVerifyReasonRequest request = ListVerifyReasonRequest.newBuilder()
                .setLocale(locale)
                .build();

        return getUserVerifyStub().listVerifyReason(request);
    }

    @Override
    public ListVerifyHistoryReply listVerifyHistory(Long brokerId, String locale, Long userVerifyId) {
        ListVerifyHistoryRequest request = ListVerifyHistoryRequest.newBuilder()
                .setBrokerId(brokerId)
                .setLocale(locale)
                .setUserVerifyId(userVerifyId)
                .build();

        return getUserVerifyStub().listVerifyHistory(request);
    }

    @Override
    public ListUpdateUserByDateReply listUpdateUserByDate(ListUpdateUserByDateRequest request) {
        return getUserVerifyStub().listUpdateUserByDate(request);
    }

    @Override
    public AddBrokerKycConfigReply addBrokerKycConfig(AddBrokerKycConfigRequest request) {
        return getUserVerifyStub().addBrokerKycConfig(request);
    }

    @Override
    public DegradeBrokerKycLevelReply degradeBrokerKycLevel(long brokerId, long userId) {
        return getUserVerifyStub().degradeBrokerKycLevel(DegradeBrokerKycLevelRequest.newBuilder()
                .setBrokerId(brokerId)
                .setUserId(userId)
                .build());
    }

    @Override
    public OpenThirdKycAuthReply openThirdKycAuth(long brokerId, long userId) {
        return getUserVerifyStub().openThirdKycAuth(OpenThirdKycAuthRequest.newBuilder()
                .setBrokerId(brokerId)
                .setUserId(userId)
                .build());
    }

    @Override
    public GetBrokerKycConfigsReply getBrokerKycConfigs(long brokerId) {
        return getUserVerifyStub()
                .getBrokerKycConfigs(GetBrokerKycConfigsRequest.newBuilder().setBrokerId(brokerId).build());
    }
}
