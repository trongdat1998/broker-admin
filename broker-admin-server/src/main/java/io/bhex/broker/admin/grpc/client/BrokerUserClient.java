package io.bhex.broker.admin.grpc.client;

import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.*;
import io.bhex.broker.admin.controller.param.UserLockPo;
import io.bhex.broker.grpc.account.QueryAllSubAccountRequest;
import io.bhex.broker.grpc.account.SubAccount;
import io.bhex.broker.grpc.admin.GetRcListRequest;
import io.bhex.broker.grpc.admin.GetRcListResponse;
import io.bhex.broker.grpc.admin.ListUserAccountResponse;
import io.bhex.broker.grpc.admin.QueryLoginLogsResponse;
import io.bhex.broker.grpc.bwlist.*;
import io.bhex.broker.grpc.common.AccountTypeEnum;
import io.bhex.broker.grpc.invite.BindInviteRelationResponse;
import io.bhex.broker.grpc.invite.CancelInviteRelationRequest;
import io.bhex.broker.grpc.invite.CancelInviteRelationResponse;
import io.bhex.broker.grpc.useraction.QueryLogsRequest;
import io.bhex.broker.grpc.useraction.UserActionRecord;

import java.util.List;

public interface BrokerUserClient extends BaseClient {


    Long getAccountId(Long orgId, Long userId);

    long getUserIdByAccount(long orgId, long accountId);

    BrokerUserDTO getBrokerUser(Long orgId, Long userId);

    BrokerUserDTO getBrokerUser(Long orgId, Long userId, String nationalCode, String mobile, String email);

    BrokerUserDTO getBrokerUser(Long orgId, Long accountId, Long userId, String nationalCode, String mobile, String email);

    ResultModel disableApiOperation(Long orgId, Long userId);

    ResultModel reopenApiOperation(Long orgId, Long userId);

    ResultModel disableUserLogin(Long orgId, Long userId);

    ResultModel reopenUserLogin(Long orgId, Long userId);

    ResultModel unbindGA(Long orgId, Long userId, Long adminUserId);

    ResultModel unbindEmail(Long orgId, Long userId, Long adminUserId);

    ResultModel unbindMobile(Long orgId, Long userId, Long adminUserId);

    UserKycDTO getKycInfo(Long orgId, Long userId);

    UserInviteInfoDTO getUserInviteInfo(Long orgId, Long userId, String mobile, String email);

    List<UserInviteRelationDTO> getUserInviteRelation(Long orgId, Long userId, String mobile, String email,
                                                      Long fromId, Long lastId, Long startTime, Long endTime, Integer limit);

    List<WithdrawAddressDTO> getWithdrawAddress(Long orgId, Long userId);

    List<DepositAddressDTO> queryDepositAddress(Long orgId, Long userId);

    QueryLoginLogsResponse queryLoginLogs(Long orgId, Long userId, Integer current, Integer pageSize);

    ListUserAccountResponse listUserAccount(Long orgId, List<Long> userIds);

    ListUserAccountResponse listUserAccount(Long orgId, List<Long> userIds, AccountTypeEnum accountTypeEnum);

    ResultModel getBrokerUserInfo(Long userId);

    ResultModel userLockBalance(Long orgId, UserLockPo po, Long adminId);

    ResultModel userUnlockBalance(Long orgId, Long lockId, Long userId, String amount, String mark, Long adminId);

    ResultModel queryUserLockBalance(Long orgId, Long userId, String tokenId);

    List<LockBalanceLogDTO> queryLockBalanceLogListByUserId(Long orgId, Long userId, Integer page, Integer size,Integer type);

    ResultModel unfreezeUser(Long orgId, Long userId, int frozenType);

    UserBlackWhiteConfig getUserBlackWhiteConfig(GetUserBlackWhiteConfigRequest request);

    List<UserBlackWhiteConfig> getBlackWhiteConfigs(GetBlackWhiteConfigsRequest request);

    EditUserBlackWhiteConfigResponse editUserBlackWhiteConfig(EditUserBlackWhiteConfigRequest request);

    List<SubAccount> queryAllSubAccount(QueryAllSubAccountRequest request);

    List<UserActionRecord> queryUserActionLogs(QueryLogsRequest request);

    List<GetRcListResponse.Item> getUserRcList(GetRcListRequest request);

    CancelInviteRelationResponse cancelInviteRelation(CancelInviteRelationRequest request);

    BindInviteRelationResponse bindInviteRelation(Long orgId, Long userId, Long inviteUserId);

    Long getMarginAccountId(Long orgId, Long userId);

    ListUserAccountResponse listUserMarginAccount(Long orgId, List<Long> userIds);

    ResultModel batchUnlockAirDrop(Long orgId,Integer unlockType,String tokenId,String userIds,String mark);
}
