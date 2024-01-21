package io.bhex.broker.admin.grpc.client;

import io.bhex.broker.grpc.admin.*;

/**
 * @ProjectName: broker
 * @Package: io.bhex.broker.admin.grpc.client
 * @Author: ming.xu
 * @CreateDate: 24/08/2018 5:48 PM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
public interface UserVerifyClient {

    ListUnverifiedUserReply queryUnverifyiedUser(Integer current, Integer pageSize, Long brokerId, Long userId, String locale);

    QueryUserVerifyListReply queryUserVerifyList(QueryUserVerifyListRequest request);

    UserVerifyDetail getVerifyUserById(Long userVerifyId, Long brokerId, String locale, boolean decryptUrl);

    UpdateVerifyUserReply updateVerifyUser(Long brokerId, Long adminUserId, Long userVerifyId, int verifyStatus, Long reasonId, String remark);

    ListVerifyReasonReply listVerifyReason(String locale);

    ListVerifyHistoryReply listVerifyHistory(Long brokerId, String locale, Long userVerifyId);

    ListUpdateUserByDateReply listUpdateUserByDate(ListUpdateUserByDateRequest request);

    AddBrokerKycConfigReply addBrokerKycConfig(AddBrokerKycConfigRequest request);

    DegradeBrokerKycLevelReply degradeBrokerKycLevel(long brokerId, long userId);

    OpenThirdKycAuthReply openThirdKycAuth(long brokerId, long userId);

    GetBrokerKycConfigsReply getBrokerKycConfigs(long brokerId);
}
