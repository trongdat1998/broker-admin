package io.bhex.broker.admin.grpc.client.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.bhex.base.account.BalanceServiceGrpc;
import io.bhex.base.account.GetPositionRequest;
import io.bhex.base.account.PositionResponseList;
import io.bhex.bhop.common.exception.BizException;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.bhop.common.util.LocaleUtil;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.BrokerUserDTO;
import io.bhex.broker.admin.controller.dto.DepositAddressDTO;
import io.bhex.broker.admin.controller.dto.LockBalanceLogDTO;
import io.bhex.broker.admin.controller.dto.UserInfoDto;
import io.bhex.broker.admin.controller.dto.UserInviteInfoDTO;
import io.bhex.broker.admin.controller.dto.UserInviteRelationDTO;
import io.bhex.broker.admin.controller.dto.UserKycDTO;
import io.bhex.broker.admin.controller.dto.UserPositionDTO;
import io.bhex.broker.admin.controller.dto.WithdrawAddressDTO;
import io.bhex.broker.admin.controller.param.UserLockPo;
import io.bhex.broker.admin.grpc.client.BrokerUserClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.admin.service.impl.OsAccessAuthService;
import io.bhex.broker.admin.util.BeanCopyUtils;
import io.bhex.broker.common.util.JsonUtil;
import io.bhex.broker.grpc.account.QueryAllSubAccountRequest;
import io.bhex.broker.grpc.account.SubAccount;
import io.bhex.broker.grpc.admin.BindFundAccountRequest;
import io.bhex.broker.grpc.admin.BindFundAccountResponse;
import io.bhex.broker.grpc.admin.BrokerUser;
import io.bhex.broker.grpc.admin.BrokerUserServiceGrpc;
import io.bhex.broker.grpc.admin.CheckBindFundAccountRequest;
import io.bhex.broker.grpc.admin.CheckBindFundAccountResponse;
import io.bhex.broker.grpc.admin.DisableApiOperationRequest;
import io.bhex.broker.grpc.admin.DisableApiOperationResponse;
import io.bhex.broker.grpc.admin.DisableUserLoginRequest;
import io.bhex.broker.grpc.admin.DisableUserLoginResponse;
import io.bhex.broker.grpc.admin.GetBrokerUserRequest;
import io.bhex.broker.grpc.admin.GetBrokerUserResponse;
import io.bhex.broker.grpc.admin.GetPersonalKycRequest;
import io.bhex.broker.grpc.admin.GetPersonalKycResponse;
import io.bhex.broker.grpc.admin.GetRcListRequest;
import io.bhex.broker.grpc.admin.GetRcListResponse;
import io.bhex.broker.grpc.admin.GetUserIdByAccountRequest;
import io.bhex.broker.grpc.admin.GetWithdrawAddressRequest;
import io.bhex.broker.grpc.admin.GetWithdrawAddressResponse;
import io.bhex.broker.grpc.admin.ListUserAccountRequest;
import io.bhex.broker.grpc.admin.ListUserAccountResponse;
import io.bhex.broker.grpc.admin.LockBalanceLog;
import io.bhex.broker.grpc.admin.QueryFundAccountRequest;
import io.bhex.broker.grpc.admin.QueryFundAccountResponse;
import io.bhex.broker.grpc.admin.QueryLoginLogsRequest;
import io.bhex.broker.grpc.admin.QueryLoginLogsResponse;
import io.bhex.broker.grpc.admin.QueryUserLockLogListRequest;
import io.bhex.broker.grpc.admin.QueryUserLockLogListResponse;
import io.bhex.broker.grpc.admin.ReopenApiOperationRequest;
import io.bhex.broker.grpc.admin.ReopenApiOperationResponse;
import io.bhex.broker.grpc.admin.ReopenUserLoginRequest;
import io.bhex.broker.grpc.admin.ReopenUserLoginResponse;
import io.bhex.broker.grpc.admin.SecurityUnbindEmailRequest;
import io.bhex.broker.grpc.admin.SecurityUnbindEmailResponse;
import io.bhex.broker.grpc.admin.SecurityUnbindGARequest;
import io.bhex.broker.grpc.admin.SecurityUnbindGAResponse;
import io.bhex.broker.grpc.admin.SecurityUnbindMobileRequest;
import io.bhex.broker.grpc.admin.SecurityUnbindMobileResponse;
import io.bhex.broker.grpc.admin.SetFundAccountShowRequest;
import io.bhex.broker.grpc.admin.SetFundAccountShowResponse;
import io.bhex.broker.grpc.admin.UnfreezeUserRequest;
import io.bhex.broker.grpc.admin.UnfreezeUserResponse;
import io.bhex.broker.grpc.admin.UserBatchUnLockAirDropRequest;
import io.bhex.broker.grpc.admin.UserBatchUnLockAirDropResponse;
import io.bhex.broker.grpc.admin.UserLockRequest;
import io.bhex.broker.grpc.admin.UserLockResponse;
import io.bhex.broker.grpc.admin.UserUnlockRequest;
import io.bhex.broker.grpc.admin.UserUnlockResponse;
import io.bhex.broker.grpc.admin.UserWithdrawAddress;
import io.bhex.broker.grpc.bwlist.EditUserBlackWhiteConfigRequest;
import io.bhex.broker.grpc.bwlist.EditUserBlackWhiteConfigResponse;
import io.bhex.broker.grpc.bwlist.GetBlackWhiteConfigsRequest;
import io.bhex.broker.grpc.bwlist.GetUserBlackWhiteConfigRequest;
import io.bhex.broker.grpc.bwlist.UserBlackWhiteConfig;
import io.bhex.broker.grpc.bwlist.UserBlackWhiteListConfigServiceGrpc;
import io.bhex.broker.grpc.common.AccountTypeEnum;
import io.bhex.broker.grpc.common.Header;
import io.bhex.broker.grpc.common.Platform;
import io.bhex.broker.grpc.deposit.DepositAddressObj;
import io.bhex.broker.grpc.deposit.DepositServiceGrpc;
import io.bhex.broker.grpc.deposit.QueryUserDepositAddressRequest;
import io.bhex.broker.grpc.deposit.QueryUserDepositAddressResponse;
import io.bhex.broker.grpc.invite.BindInviteRelationRequest;
import io.bhex.broker.grpc.invite.BindInviteRelationResponse;
import io.bhex.broker.grpc.invite.CancelInviteRelationRequest;
import io.bhex.broker.grpc.invite.CancelInviteRelationResponse;
import io.bhex.broker.grpc.invite.GetInviteInfoRequest;
import io.bhex.broker.grpc.invite.GetInviteInfoResponse;
import io.bhex.broker.grpc.invite.InviteInfo;
import io.bhex.broker.grpc.invite.InviteServiceGrpc;
import io.bhex.broker.grpc.user.GetUserInfoRequest;
import io.bhex.broker.grpc.user.GetUserInfoResponse;
import io.bhex.broker.grpc.user.QueryUserInviteInfoRequest;
import io.bhex.broker.grpc.user.QueryUserInviteInfoResponse;
import io.bhex.broker.grpc.user.User;
import io.bhex.broker.grpc.user.UserServiceGrpc;
import io.bhex.broker.grpc.useraction.QueryLogsRequest;
import io.bhex.broker.grpc.useraction.UserActionRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service("brokerUserClient")
public class BrokerUserClientImpl implements BrokerUserClient {


    @Resource
    GrpcClientConfig grpcConfig;

    @Autowired
    private OsAccessAuthService osAccessAuthService;

    private BrokerUserServiceGrpc.BrokerUserServiceBlockingStub getUserStub() {
        return grpcConfig.brokerUserServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    private DepositServiceGrpc.DepositServiceBlockingStub getDepositStub() {
        return grpcConfig.depositServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    private UserServiceGrpc.UserServiceBlockingStub getUserServiceStub() {
        return grpcConfig.userServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    private InviteServiceGrpc.InviteServiceBlockingStub getInviteServiceStub() {
        return grpcConfig.inviteServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    private UserBlackWhiteListConfigServiceGrpc.UserBlackWhiteListConfigServiceBlockingStub getBlackWhiteListConfigStub() {
        return grpcConfig.userBlackWhiteListConfigServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    private BalanceServiceGrpc.BalanceServiceBlockingStub balanceServiceBlockingStub() {
        return grpcConfig.bhBalanceServiceBlockingStub(GrpcClientConfig.BH_SERVER_CHANNEL_NAME);
    }

    @Override
    public Long getAccountId(Long orgId, Long userId) {
        ListUserAccountResponse resp = this.listUserAccount(orgId, Lists.newArrayList(userId));
        if (resp.getRet() != 0 || CollectionUtils.isEmpty(resp.getAccountInfoList())) {
            throw new BizException(ErrorCode.ERROR);
        }

        return resp.getAccountInfoList().get(0).getAccountId();
    }

    @Override
    public long getUserIdByAccount(long orgId, long accountId) {
        GetUserIdByAccountRequest request = GetUserIdByAccountRequest.newBuilder().setOrgId(orgId).setAccountId(accountId).build();
        return getUserStub().getUserIdByAccount(request).getUserId();
    }

    @Override
    public BrokerUserDTO getBrokerUser(Long orgId, Long userId) {
        return getBrokerUser(orgId, userId, null, null, null);
    }

    @Override
    public BrokerUserDTO getBrokerUser(Long orgId, Long userId, String nationalCode, String mobile, String email) {
        return getBrokerUser(orgId, null, userId, nationalCode, mobile, email);
    }

    @Override
    public BrokerUserDTO getBrokerUser(Long orgId, Long accountId, Long userId, String nationalCode, String mobile, String email) {
        String language = LocaleUtil.getLanguage();

        GetBrokerUserRequest.Builder builder = GetBrokerUserRequest.newBuilder();
        builder.setOrgId(orgId).setLanguage(language);
        if (accountId != null && accountId > 0) {
            builder.setAccountId(accountId);
        }
        if (userId != null && userId > 0) {
            builder.setUserId(userId);
        }
        if (!StringUtils.isEmpty(mobile)) {
            builder.setMobile(mobile);
            if (!StringUtils.isEmpty(nationalCode)) {
                builder.setNationalCode(nationalCode);
            }
        }
        if (!StringUtils.isEmpty(email)) {
            builder.setEmail(email);
        }

        GetBrokerUserResponse response = getUserStub().getBrokerUser(builder.build());
        if (response.getBrokerUser().getUserId() == 0) {
            return null;
        }
        BrokerUser brokerUser = response.getBrokerUser();
        BrokerUserDTO dto = new BrokerUserDTO();
        BeanCopyUtils.copyProperties(brokerUser, dto);
        dto.setMobile(brokerUser.getMobile().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
        dto.setEmail(brokerUser.getEmail().replaceAll("(?<=.).(?=[^@]*?.@)", "*"));
        dto.setRealEmail(brokerUser.getEmail());
        dto.setRealMobile(brokerUser.getMobile());
        dto.setIsFreezeLogin(brokerUser.getIsFreezeLogin());

        List<BrokerUserDTO.FrozenInfo> infos = brokerUser.getFrozenInfoList().stream().map(f -> {
            BrokerUserDTO.FrozenInfo info = new BrokerUserDTO.FrozenInfo();
            BeanCopyUtils.copyProperties(f, info);
            return info;
        }).collect(Collectors.toList());
        dto.setFrozenInfos(infos);

        return dto;
    }

    @Override
    public ResultModel disableApiOperation(Long orgId, Long userId) {
        DisableApiOperationRequest request = DisableApiOperationRequest.newBuilder()
                .setOrgId(orgId).setUserId(userId)
                .build();
        DisableApiOperationResponse response = getUserStub().disableApiOperation(request);
        ResultModel result = new ResultModel(response.getRet(), response.getMsg(), null);
        return result;
    }

    @Override
    public ResultModel reopenApiOperation(Long orgId, Long userId) {
        ReopenApiOperationRequest request = ReopenApiOperationRequest.newBuilder()
                .setOrgId(orgId).setUserId(userId)
                .build();
        ReopenApiOperationResponse response = getUserStub().reopenApiOperation(request);
        ResultModel result = new ResultModel(response.getRet(), response.getMsg(), null);
        return result;
    }

    @Override
    public ResultModel disableUserLogin(Long orgId, Long userId) {
        DisableUserLoginRequest request = DisableUserLoginRequest.newBuilder()
                .setOrgId(orgId).setUserId(userId)
                .build();
        DisableUserLoginResponse response = getUserStub().disableUserLogin(request);
        ResultModel result = new ResultModel(response.getRet(), response.getMsg(), null);
        return result;
    }

    @Override
    public ResultModel reopenUserLogin(Long orgId, Long userId) {
        ReopenUserLoginRequest request = ReopenUserLoginRequest.newBuilder()
                .setOrgId(orgId).setUserId(userId)
                .build();
        ReopenUserLoginResponse response = getUserStub().reopenUserLogin(request);
        ResultModel result = new ResultModel(response.getRet(), response.getMsg(), null);
        return result;
    }

    @Override
    public ResultModel unbindGA(Long orgId, Long userId, Long adminUserId) {
        SecurityUnbindGARequest request = SecurityUnbindGARequest.newBuilder()
                .setAdminUserId(adminUserId)
                .setOrgId(orgId)
                .setUserId(userId)
                .build();
        SecurityUnbindGAResponse response = getUserStub().unbindGA(request);
        ResultModel result = new ResultModel(response.getRet(), response.getMsg(), null);
        return result;
    }

    @Override
    public ResultModel unbindMobile(Long orgId, Long userId, Long adminUserId) {
        SecurityUnbindMobileRequest request = SecurityUnbindMobileRequest.newBuilder()
                .setAdminUserId(adminUserId)
                .setOrgId(orgId)
                .setUserId(userId)
                .build();
        SecurityUnbindMobileResponse response = getUserStub().unbindMobile(request);
        ResultModel result = new ResultModel(response.getRet(), response.getMsg(), null);
        return result;
    }

    @Override
    public ResultModel unbindEmail(Long orgId, Long userId, Long adminUserId) {
        SecurityUnbindEmailRequest request = SecurityUnbindEmailRequest.newBuilder()
                .setAdminUserId(adminUserId)
                .setOrgId(orgId)
                .setUserId(userId)
                .build();
        SecurityUnbindEmailResponse response = getUserStub().unbindEmail(request);
        ResultModel result = new ResultModel(response.getRet(), response.getMsg(), null);
        return result;
    }

    @Override
    public UserKycDTO getKycInfo(Long orgId, Long userId) {
        String language = LocaleUtil.getLanguage();
        GetPersonalKycRequest request = GetPersonalKycRequest.newBuilder()
                .setOrgId(orgId)
                .setUserId(userId)
                .setLanguage(language)
                .build();
        GetPersonalKycResponse response = grpcConfig.adminUserVerifyServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME)
                .getPersonalKyc(request);
        if (response.getRet() != 0) {
            return null;
        }
        UserKycDTO dto = new UserKycDTO();
        BeanUtils.copyProperties(response, dto);
        dto.setCardFrontUrl(osAccessAuthService.createAccessUrl(dto.getCardFrontUrl()));
        dto.setCardHandUrl(osAccessAuthService.createAccessUrl(dto.getCardHandUrl()));

        dto.setFacePhotoUrl(osAccessAuthService.createAccessUrl(dto.getFacePhotoUrl()));
        dto.setFaceVideoUrl(osAccessAuthService.createAccessUrl(dto.getFaceVideoUrl()));
        dto.setVideoUrl(osAccessAuthService.createAccessUrl(dto.getVideoUrl()));

        return dto;
    }

    @Override
    public UserInviteInfoDTO getUserInviteInfo(Long orgId, Long userId, String mobile, String email) {
        GetInviteInfoRequest.Builder requestBuilder = GetInviteInfoRequest.newBuilder();
        if (userId != null && userId > 0) {
            requestBuilder.setHeader(Header.newBuilder().setOrgId(orgId).setUserId(userId).build());
        } else {
            if (Strings.isNullOrEmpty(mobile) && Strings.isNullOrEmpty(email)) {
                return null;
            }
            GetUserInfoRequest getUserInfoRequest = GetUserInfoRequest.newBuilder()
                    .setHeader(Header.newBuilder().setOrgId(orgId).build())
                    .setMobile(mobile)
                    .setEmail(email)
                    .build();
            GetUserInfoResponse response = getUserServiceStub().getUserInfo(getUserInfoRequest);
            if (response.getRet() != 0) {
                log.error("getUserInfo with request:{} error, {}", getUserInfoRequest, response.getRet());
                return null;
            }
            requestBuilder.setHeader(Header.newBuilder().setOrgId(orgId).setUserId(response.getUser().getUserId()).build());
        }
        GetInviteInfoResponse response = getInviteServiceStub().getInviteInfo(requestBuilder.build());
        InviteInfo inviteInfo = response.getInfo();
        UserInviteInfoDTO.Builder dtoBuilder = UserInviteInfoDTO.builder().inviteCode(response.getInviteCode());
        if (inviteInfo != null && inviteInfo.getUserId() > 0) {
            dtoBuilder.userId(inviteInfo.getUserId())
                    .inviteCount(inviteInfo.getInviteCount())
                    .inviteValidCount(inviteInfo.getInviteVaildCount())
                    .inviteDirectValidCount(inviteInfo.getInviteDirectVaildCount())
                    .inviteIndirectValidCount(inviteInfo.getInviteIndirectVaildCount())
                    .inviteLevel(inviteInfo.getInviteLevel())
                    .directRate(Double.valueOf(inviteInfo.getDirectRate()))
                    .indirectRate(Double.valueOf(inviteInfo.getIndirectRate()))
                    .bonusCoin(Double.valueOf(inviteInfo.getBonusCoin()))
                    .bonusPoint(Double.valueOf(inviteInfo.getBonusPoint()))
                    .inviteHobbitLeaderCount(inviteInfo.getInviteHobbitLeaderCount())
                    .build();
        }
        return dtoBuilder.build();
    }

    @Override
    public List<UserInviteRelationDTO> getUserInviteRelation(Long orgId, Long userId, String mobile, String email,
                                                             Long fromId, Long lastId, Long startTime, Long endTime, Integer limit) {
        QueryUserInviteInfoRequest.Builder requestBuilder = QueryUserInviteInfoRequest.newBuilder();
        if (userId != null && userId > 0) {
            requestBuilder.setHeader(Header.newBuilder().setOrgId(orgId).setUserId(userId).build()).setUserId(userId);
        } else {
            if (Strings.isNullOrEmpty(mobile) && Strings.isNullOrEmpty(email)) {
                return null;
            }
            GetUserInfoRequest getUserInfoRequest = GetUserInfoRequest.newBuilder()
                    .setHeader(Header.newBuilder().setOrgId(orgId).build())
                    .setMobile(mobile)
                    .setEmail(email)
                    .build();
            GetUserInfoResponse response = getUserServiceStub().getUserInfo(getUserInfoRequest);
            if (response.getRet() != 0) {
                log.error("getUserInfo with request:{} error, {}", getUserInfoRequest, response.getRet());
                return null;
            }
            requestBuilder.setHeader(Header.newBuilder().setOrgId(orgId).setUserId(response.getUser().getUserId()).build())
                    .setUserId(response.getUser().getUserId());
        }
        if (fromId != null && fromId > 0) {
            requestBuilder.setFromId(fromId);
        }
        if (lastId != null && lastId > 0) {
            requestBuilder.setLastId(lastId);
        }
        if (startTime != null && startTime > 0) {
            requestBuilder.setStartTime(startTime);
        }
        if (endTime != null && endTime > 0) {
            requestBuilder.setEndTime(endTime);
        }
        if (limit != null && limit > 0) {
            requestBuilder.setLimit(limit);
        } else {
            requestBuilder.setLimit(100);
        }
        log.info("query user invitation relation request:{}", JsonUtil.defaultGson().toJson(requestBuilder.build()));
        QueryUserInviteInfoResponse response = getUserServiceStub().queryUserInviteInfo(requestBuilder.build());
        return response.getInviteList().stream()
                .map(inviteInfo -> UserInviteRelationDTO.builder()
                        .inviteId(inviteInfo.getInviteId())
                        .userId(inviteInfo.getUserId())
                        .nationalCode(inviteInfo.getNationalCode())
                        .mobile(inviteInfo.getMobile().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"))
                        .email(inviteInfo.getEmail().replaceAll("(?<=.).(?=[^@]*?.@)", "*"))
                        .inviteType(inviteInfo.getInviteType())
                        .verifyStatus(inviteInfo.getVerifyStatus())
                        .registerType(inviteInfo.getRegisterType())
                        .registerDate(inviteInfo.getRegisterTime())

                        .name(inviteInfo.getKycName())
                        .inviteLeader(inviteInfo.getInvitedLeader())
                        .inviteIndirectCount(inviteInfo.getInviteIndirectCount())

                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<WithdrawAddressDTO> getWithdrawAddress(Long orgId, Long userId) {
        BrokerUserDTO brokerUserDTO = getBrokerUser(orgId, userId, null, null, null);
        if (brokerUserDTO == null) {
            return new ArrayList<>();
        }

        GetWithdrawAddressRequest request = GetWithdrawAddressRequest.newBuilder()
                .setOrgId(orgId).setUserId(userId).build();
        GetWithdrawAddressResponse response = getUserStub().getWithdrawAddress(request);

        List<UserWithdrawAddress> addresses = response.getUserWithdrawAddressesList();

        List<WithdrawAddressDTO> dtos = addresses.stream().map(address -> {
            WithdrawAddressDTO dto = new WithdrawAddressDTO();
            BeanUtils.copyProperties(address, dto);
            return dto;
        }).collect(Collectors.toList());

        return dtos;
    }

    @Override
    public List<DepositAddressDTO> queryDepositAddress(Long orgId, Long userId) {
        BrokerUserDTO brokerUserDTO = getBrokerUser(orgId, userId, null, null, null);
        if (brokerUserDTO == null) {
            return new ArrayList<>();
        }

        QueryUserDepositAddressRequest request = QueryUserDepositAddressRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).setUserId(userId).setPlatform(Platform.PC).build())
                .build();
        QueryUserDepositAddressResponse response = getDepositStub().queryUserDepositAddress(request);

        List<DepositAddressObj> depositAddressObjList = response.getDepositAddressList();
        return depositAddressObjList.stream()
                .map(this::getDepositAddressResult)
                .collect(Collectors.toList());
    }

    private DepositAddressDTO getDepositAddressResult(DepositAddressObj depositAddressObj) {
        DepositAddressDTO result = DepositAddressDTO.builder()
                .tokenId(depositAddressObj.getTokenId())
                .chainType(depositAddressObj.getChainType())
                .address(depositAddressObj.getAddress())
                .addressExt(depositAddressObj.getAddressExt())
                .hasMultiChainType(depositAddressObj.getHasMultiChainType())
                .build();
        if (depositAddressObj.getHasMultiChainType()) {
            result.setMultiDepositAddress(depositAddressObj.getMulDepositAddressList().stream().map(this::getDepositAddressResult).collect(Collectors.toList()));
        }
        return result;
    }

    @Override
    public QueryLoginLogsResponse queryLoginLogs(Long orgId, Long userId, Integer current, Integer pageSize) {

        QueryLoginLogsRequest request = QueryLoginLogsRequest.newBuilder()
                .setOrgId(orgId).setUserId(userId)
                .setCurrent(current)
                .setPageSize(pageSize)
                .build();

        QueryLoginLogsResponse response = getUserStub().queryLoginLogs(request);
        return response;


    }


    @Override
    public ListUserAccountResponse listUserAccount(Long orgId, List<Long> userIds) {
        return listUserAccount(orgId, userIds, AccountTypeEnum.COIN);
    }

    @Override
    public ListUserAccountResponse listUserAccount(Long orgId, List<Long> userIds, AccountTypeEnum accountTypeEnum) {
        ListUserAccountRequest request = ListUserAccountRequest.newBuilder()
                .setOrgId(orgId)
                .addAllUserId(userIds)
                .setAccountType(accountTypeEnum)
                .build();

        ListUserAccountResponse response = getUserStub().listUserAccount(request);
        log.info("listUserAccount result : {}", response);
        return response;
    }

    @Override
    public ResultModel getBrokerUserInfo(Long userId) {
        log.info("getBrokerUserInfo userId {}", userId);
        GetUserInfoRequest getUserInfoRequest = GetUserInfoRequest
                .newBuilder()
                .setHeader(Header.newBuilder().setUserId(userId).build())
                .build();
        GetUserInfoResponse getUserInfoResponse = getUserServiceStub().getUserInfo(getUserInfoRequest);
        if (getUserInfoResponse == null || getUserInfoResponse.getUser() == null) {
            return ResultModel.error("not find user info");
        }
        User user = getUserInfoResponse.getUser();
        UserInfoDto userInfoDto = new UserInfoDto();
        userInfoDto.setBindGa(user.getBindGa());
        userInfoDto.setEmail(user.getEmail());
        userInfoDto.setBindTradePwd(user.getBindTradePwd());
        userInfoDto.setMobile(user.getMobile());
        userInfoDto.setVerifyStatus(user.getVerifyStatus());
        return ResultModel.ok(userInfoDto);
    }

    @Override
    public ResultModel userUnlockBalance(Long orgId, Long lockId, Long userId, String amount, String mark, Long adminId) {
        if (orgId == null || lockId == null || userId == null) {
            return ResultModel.error("Parameter not null");
        }

        if (StringUtils.isEmpty(amount)) {
            return ResultModel.error("Parameter not null");
        }

        UserUnlockResponse response = unlockBalance(lockId, userId, orgId, amount, mark, adminId);
        if (response.getRet() != 200) {
            return ResultModel.error(response.getRet(), "");
        }
        return ResultModel.ok(response.getMsg());
    }

    @Override
    public ResultModel userLockBalance(Long orgId, UserLockPo po, Long adminId) {
        ResultModel resultModel = checkParameter(po);
        if (resultModel != null) {
            return resultModel;
        }
        UserLockResponse response
                = lockBalance(po.getUserIds(), orgId, po.getTokenId(), po.getAmount().toPlainString(), po.getMark(), po.getType(), adminId);
        if (response.getRet() != 200) {
            return ResultModel.error(response.getRet(), "");
        }
        return ResultModel.ok(response.getMsg());
    }

    private UserLockResponse lockBalance(String userStr, Long orgId, String tokenId, String amount, String mark, Integer type, Long adminId) {
        UserLockRequest request = UserLockRequest
                .newBuilder()
                .setAmount(amount)
                .setOrgId(orgId)
                .setTokenId(tokenId)
                .setType(type)
                .setUserIdStr(userStr)
                .setMark(mark)
                .setAdminId(adminId)
                .build();
        return getUserStub().userLockBalanceForAdmin(request);
    }

    private UserUnlockResponse unlockBalance(Long lockId, Long userId, Long orgId, String amount, String mark, Long adminId) {
        UserUnlockRequest request = UserUnlockRequest
                .newBuilder()
                .setOrgId(orgId)
                .setAmount(amount)
                .setLockId(lockId)
                .setUserId(userId)
                .setMark(mark)
                .setAdminId(adminId)
                .build();
        UserUnlockResponse response = getUserStub().userUnLockBalanceForAdmin(request);
        return response;
    }

    @Override
    public ResultModel queryUserLockBalance(Long orgId, Long userId, String tokenId) {
        Long accountId = queryAccountId(orgId, userId);
        if (accountId == 0L) {
            ResultModel.error("not find account id");
        }

        GetPositionRequest getPositionRequest = GetPositionRequest
                .newBuilder()
                .setTokenId(tokenId)
                .setBrokerId(orgId)
                .setAccountId(accountId)
                .build();

        PositionResponseList positionResponseList
                = balanceServiceBlockingStub().getPosition(getPositionRequest);
        List<UserPositionDTO> positions = new ArrayList<>();
        if (positionResponseList.getPositionListCount() > 0) {
            positionResponseList.getPositionListList().forEach(position -> {
                positions.add(UserPositionDTO
                        .builder()
                        .available(position.getAvailable())
                        .locked(position.getLocked())
                        .tokenId(position.getTokenId())
                        .total(position.getTotal())
                        .build());
            });
        } else {
            return ResultModel.ok(new ArrayList<>());
        }
        return ResultModel.ok(positions);
    }

    @Override
    public List<LockBalanceLogDTO> queryLockBalanceLogListByUserId(Long orgId, Long userId, Integer page, Integer size,Integer type) {
        QueryUserLockLogListRequest request = QueryUserLockLogListRequest
                .newBuilder()
                .setUserId(userId == null ? 0L : userId)
                .setBrokerId(orgId)
                .setPage(page)
                .setSize(size)
                .setType(type)
                .build();

        QueryUserLockLogListResponse response
                = getUserStub().queryLockBalanceLogListByUserId(request);
        return buildLockBalanceLog(response.getLockLogList());
    }

    private Long queryAccountId(Long orgId, Long uid) {
        Long accountId = 0L;
        try {
            accountId = getAccountId(orgId, uid);
        } catch (BizException bizException) {
            log.warn("user lock fail not find account id uid is : {}", uid);
        }
        return accountId;
    }

    private ResultModel checkParameter(UserLockPo po) {
        if (StringUtils.isEmpty(po.getUserIds())) {
            return ResultModel.error("user id not null");
        }

        if (StringUtils.isEmpty(po.getAmount())) {
            return ResultModel.error("amount not null");
        }

        if (StringUtils.isEmpty(po.getTokenId())) {
            return ResultModel.error("token id not null");
        }

        if (po.getType() == null || po.getType() <= 0) {
            return ResultModel.error("type not null");
        }
        return null;
    }

    private List<LockBalanceLogDTO> buildLockBalanceLog(List<LockBalanceLog> lockBalanceLogs) {
        List<LockBalanceLogDTO> lockBalanceLogList = new ArrayList<>();
        lockBalanceLogs.forEach(log -> {
            lockBalanceLogList.add(LockBalanceLogDTO
                    .builder()
                    .type(log.getType())
                    .accountId(String.valueOf(log.getAccountId()))
                    .amount(log.getAmount())
                    .brokerId(log.getBrokerId())
                    .id(log.getId())
                    .subjectType(log.getSubjectType())
                    .clientOrderId(log.getClientOrderId())
                    .lastAmount(log.getLastAmount())
                    .unlockAmount(log.getUnlockAmount())
                    .mark(log.getMark())
                    .tokenId(log.getTokenId())
                    .userId(String.valueOf(log.getUserId()))
                    .createTime(log.getCreateTime())
                    .updateTime(log.getUpdateTime())
                    .build());
        });
        return lockBalanceLogList;
    }

    @Override
    public ResultModel unfreezeUser(Long orgId, Long userId, int frozenType) {
        UnfreezeUserResponse response
                = getUserStub().unfreezeUser(UnfreezeUserRequest.newBuilder()
                .setOrgId(orgId).setUserId(userId).setType(frozenType).build());
        if (response.getRet() == 0) {
            return ResultModel.ok();
        } else {
            return ResultModel.error(response.getMsg());
        }
    }

    @Override
    public UserBlackWhiteConfig getUserBlackWhiteConfig(GetUserBlackWhiteConfigRequest request) {
        UserBlackWhiteConfig response = getBlackWhiteListConfigStub().getUserBlackWhiteConfig(request);
        return response;
    }

    @Override
    public List<UserBlackWhiteConfig> getBlackWhiteConfigs(GetBlackWhiteConfigsRequest request) {
        return getBlackWhiteListConfigStub().getBlackWhiteConfigs(request).getConfigsList();
    }

    @Override
    public EditUserBlackWhiteConfigResponse editUserBlackWhiteConfig(EditUserBlackWhiteConfigRequest request) {
        EditUserBlackWhiteConfigResponse response = getBlackWhiteListConfigStub().editUserBlackWhiteConfig(request);
        log.info("req:{} res:{}", request, response);
        return response;
    }


    @Override
    public List<UserActionRecord> queryUserActionLogs(QueryLogsRequest request) {
        return grpcConfig.userActionLogServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME)
                .queryLogs(request)
                .getUserActionList();
    }

    @Override
    public List<GetRcListResponse.Item> getUserRcList(GetRcListRequest request) {
        return getUserStub().getRcList(request).getItemsList();
    }


    @Override
    public List<SubAccount> queryAllSubAccount(QueryAllSubAccountRequest request) {
        return grpcConfig.brokerAccountServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).queryAllSubAccount(request).getSubAccountList();
    }


    @Override
    public CancelInviteRelationResponse cancelInviteRelation(CancelInviteRelationRequest request) {
        return grpcConfig.inviteServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).cancelInviteRelation(request);
    }

    @Override
    public BindInviteRelationResponse bindInviteRelation(Long orgId, Long userId, Long inviteUserId) {
        return grpcConfig.inviteServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME).bindInviteRelation(BindInviteRelationRequest.newBuilder()
                .setOrgId(orgId)
                .setInviteUserId(inviteUserId)
                .setInvitedUserId(userId)
                .build());
    }

    @Override
    public Long getMarginAccountId(Long orgId, Long userId) {
        ListUserAccountResponse resp = this.listUserMarginAccount(orgId, Lists.newArrayList(userId));
        if (resp.getRet() != 0 || CollectionUtils.isEmpty(resp.getAccountInfoList())) {
            throw new BizException(ErrorCode.ERROR);
        }

        return resp.getAccountInfoList().get(0).getAccountId();
    }

    @Override
    public ListUserAccountResponse listUserMarginAccount(Long orgId, List<Long> userIds) {
        return listUserAccount(orgId, userIds, AccountTypeEnum.MARGIN);
    }

    @Override
    public ResultModel batchUnlockAirDrop(Long orgId, Integer unlockType, String tokenId, String userIds, String mark) {
        if(unlockType!=1 && StringUtils.isEmpty(userIds)){
            return ResultModel.error("userId not null");
        }
        UserBatchUnLockAirDropRequest request = UserBatchUnLockAirDropRequest.newBuilder()
                .setOrgId(orgId)
                .setUnlockType(unlockType)
                .setTokenId(tokenId)
                .setUserIds(userIds)
                .setMark(mark)
                .build();
        UserBatchUnLockAirDropResponse response = getUserStub().userBatchUnLockAirDropForAdmin(request);
        if (response.getCode() ==200) {
            return ResultModel.ok();
        } else {
            log.warn("batchUnlockAirDrop error {}",response.getCode());
            return ResultModel.error("error");
        }

    }

    public CheckBindFundAccountResponse checkBindFundAccount(CheckBindFundAccountRequest request) {
        return getUserStub().checkBindFundAccount(request);
    }

    public BindFundAccountResponse bindFundAccount(BindFundAccountRequest request) {
        return getUserStub().bindFundAccount(request);
    }

    public QueryFundAccountResponse queryFundAccount(QueryFundAccountRequest request) {
        return getUserStub().queryFundAccount(request);
    }


    public SetFundAccountShowResponse setFundAccountShow(SetFundAccountShowRequest request) {
        return getUserStub().setFundAccountShow(request);
    }
}
