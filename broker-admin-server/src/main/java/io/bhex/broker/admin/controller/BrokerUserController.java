package io.bhex.broker.admin.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.bizlog.BussinessLogAnnotation;
import io.bhex.bhop.common.dto.PaginationVO;
import io.bhex.bhop.common.dto.param.BalanceDetailDTO;
import io.bhex.bhop.common.grpc.client.AccountAssetClient;
import io.bhex.bhop.common.jwt.filter.AccessAnnotation;
import io.bhex.bhop.common.service.AdminLoginUserService;
import io.bhex.bhop.common.service.AdminUserNameService;
import io.bhex.bhop.common.util.Combo2;
import io.bhex.bhop.common.util.LocaleUtil;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.constants.BizConstant;
import io.bhex.broker.admin.constants.OpTypeConstant;
import io.bhex.broker.admin.controller.dto.*;
import io.bhex.broker.admin.controller.param.*;
import io.bhex.broker.admin.grpc.client.AgentClient;
import io.bhex.broker.admin.grpc.client.BrokerUserClient;
import io.bhex.broker.admin.service.*;
import io.bhex.broker.admin.util.BeanCopyUtils;
import io.bhex.broker.common.util.JsonUtil;
import io.bhex.broker.grpc.account.QueryAllSubAccountRequest;
import io.bhex.broker.grpc.account.SubAccount;
import io.bhex.broker.grpc.admin.GetRcListRequest;
import io.bhex.broker.grpc.admin.GetRcListResponse;
import io.bhex.broker.grpc.admin.QueryLoginLogsResponse;
import io.bhex.broker.grpc.admin.UserLoginLog;
import io.bhex.broker.grpc.bwlist.GetUserBlackWhiteConfigRequest;
import io.bhex.broker.grpc.bwlist.UserBlackWhiteConfig;
import io.bhex.broker.grpc.bwlist.UserBlackWhiteListType;
import io.bhex.broker.grpc.bwlist.UserBlackWhiteType;
import io.bhex.broker.grpc.common.Header;
import io.bhex.broker.grpc.invite.CancelInviteRelationRequest;
import io.bhex.broker.grpc.invite.CancelInviteRelationResponse;
import io.bhex.broker.grpc.margin.AdminChangeMarginPositionStatusResponse;
import io.bhex.broker.grpc.useraction.QueryLogsRequest;
import io.bhex.broker.grpc.useraction.UserActionRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * @Description:
 * @Date: 2018/11/7 上午11:48
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
public class BrokerUserController extends BrokerBaseController {

    @Autowired
    private BrokerUserClient brokerUserClient;
    @Autowired
    private AccountAssetClient accountAssetClient;
    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;
    @Autowired
    private UserVerifyService userVerifyService;
    @Autowired
    @Qualifier(value = "baseConfigService")
    private BaseConfigService baseConfigService;
    @Resource
    private OTCService otcService;
    @Autowired
    private AgentClient agentClient;
    @Autowired
    private AdminLoginUserService adminLoginUserService;

    @Autowired
    private MarginService marginService;

    @RequestMapping(value = "/brokeruser/login_logs", method = RequestMethod.POST)
    public ResultModel<PaginationVO<LoginLogDTO>> queryLoginLogs(@RequestBody @Valid QueryLoginLogsPO po, AdminUserReply adminUserReply) {

        QueryLoginLogsResponse response = brokerUserClient.queryLoginLogs(adminUserReply.getOrgId(),
                po.getUserId(), po.getCurrent(), po.getPageSize());

        int total = response.getTotal();
        PaginationVO vo = new PaginationVO();
        vo.setCurrent(po.getCurrent());
        vo.setPageSize(po.getPageSize());
        vo.setTotal(response.getTotal());
        List<UserLoginLog> loginLogs = response.getLoginLogsList();
        if (total == 0 || CollectionUtils.isEmpty(loginLogs)) {
            return ResultModel.ok(vo);
        }
        List<LoginLogDTO> dtos = loginLogs.stream().map(log -> {
            LoginLogDTO logDTO = new LoginLogDTO();
            BeanUtils.copyProperties(log, logDTO);
            String appHeader = log.getAppBaseHeader();
            if (!StringUtils.isEmpty(appHeader) && appHeader.startsWith("{") && appHeader.endsWith("}")) {
                AppBaseHeaderDTO appHeaderDTO = JsonUtil.defaultGson().fromJson(log.getAppBaseHeader(), new TypeToken<AppBaseHeaderDTO>() {
                }.getType());
                logDTO.setAppHeader(appHeaderDTO);
            }

            return logDTO;
        }).collect(Collectors.toList());
        vo.setList(dtos);
        return ResultModel.ok(vo);
    }

    @BussinessLogAnnotation(name = OpTypeConstant.GET_WHOLE_EMAIL_INFO)
    @RequestMapping(value = "/brokeruser/get_whole_email", method = RequestMethod.POST)
    public ResultModel<String> reqWholeEmailInfo(@RequestBody @Valid GetSecretInfoPO po, AdminUserReply adminUserReply) {
        BrokerUserDTO dto = brokerUserClient.getBrokerUser(adminUserReply.getOrgId(), po.getUserId());
        if (dto == null) {
            return ResultModel.ok();
        }
        return ResultModel.ok(dto.getRealEmail());
    }

    @BussinessLogAnnotation(name = OpTypeConstant.GET_WHOLE_PHONE_INFO)
    @RequestMapping(value = "/brokeruser/get_whole_phone", method = RequestMethod.POST)
    public ResultModel<String> reqWholePhoneInfo(@RequestBody @Valid GetSecretInfoPO po, AdminUserReply adminUserReply) {
        BrokerUserDTO dto = brokerUserClient.getBrokerUser(adminUserReply.getOrgId(), po.getUserId());
        if (dto == null) {
            return ResultModel.ok();
        }
        return ResultModel.ok(dto.getRealMobile());
    }

    @AccessAnnotation(verifyAuth = false) //数据排行页面也可使用
    @RequestMapping(value = "/brokeruser/get_broker_user", method = RequestMethod.POST)
    public ResultModel<BrokerUserDTO> getBrokerUser(@RequestBody @Valid GetBrokerUserPO po, AdminUserReply adminUser) {

        BrokerUserDTO dto = brokerUserClient.getBrokerUser(adminUser.getOrgId(), po.getAccountId(),
                po.getUserId(), po.getNationalCode(), po.getPhone(), po.getEmail());
        if (dto == null) {
            return ResultModel.ok();
        }
        Long accountId = brokerUserClient.getAccountId(adminUser.getOrgId(), dto.getUserId());
        dto.setAccountId(accountId);

        QueryBrokerAgentPO agentPO = new QueryBrokerAgentPO();
        agentPO.setLimit(1);
        agentPO.setPage(1);
        agentPO.setUserId(dto.getUserId());
        List<QueryBrokerAgentDTO> brokerAgentUserList = agentClient.queryBrokerAgentList(adminUser.getOrgId(), agentPO);
        if (!CollectionUtils.isEmpty(brokerAgentUserList)) {
            dto.setAgentLevel(brokerAgentUserList.get(0).getLevel());
        } else {
            dto.setAgentLevel(0);
        }

        return ResultModel.ok(dto);
    }

    @RequestMapping(value = "/brokeruser/get_user_kyc", method = RequestMethod.POST)
    public ResultModel<UserKycDTO> getUserKyc(@RequestBody @Valid SimpleBrokerUserPO po) {

        UserKycDTO dto = brokerUserClient.getKycInfo(getOrgId(), po.getUserId());
        if (dto == null) {
            return ResultModel.ok();
        }

        UserVerifyDTO vo = userVerifyService.getVerifyUserById(dto.getUserVerifyId(), getOrgId(), LocaleUtil.getLanguage(), true);
        dto.setCardHandUrl(vo.getCardHandUrl());
        dto.setCardFrontUrl(vo.getCardFrontUrl());
        dto.setFacePhotoUrl(vo.getFacePhotoUrl());
        dto.setFaceVideoUrl(vo.getFaceVideoUrl());
        dto.setVideoUrl(vo.getVideoUrl());
        dto.setCardNo(vo.getCardNo());
        dto.setCardBackUrl(vo.getCardBackUrl());
        dto.setUpdated(vo.getUpdated());
        return ResultModel.ok(dto);
    }

    @RequestMapping(value = "/brokeruser/user_kyc_reverify", method = RequestMethod.POST)
    public ResultModel updateVerifyUser(@RequestBody @Valid VerifyUserPO po) {
        AdminUserReply requestUser = getRequestUser();
        long brokerId = requestUser.getOrgId();
        Boolean isOk = userVerifyService.updateVerifyUser(brokerId, requestUser.getId(), po.getUserVerifyId(),
               VerifyUserPO.UserVerifyStatus.UNDER_REVIEW.value(), 0L, "");

        UserVerifyDTO vo = userVerifyService.getVerifyUserById(po.getUserVerifyId(), brokerId, LocaleUtil.getLanguage(), false);

        String opContent = "reset kyc verify UID:" + vo.getUserId();
        saveBizLog(OpTypeConstant.USER_KYC_VERIFY, "resetkyc", opContent, JSON.toJSONString(po), isOk ? 1 : 0, vo.getUserId().toString());
        return ResultModel.ok(isOk);
    }


    @RequestMapping(value = {"/invite/get_user_invite_info", "/brokeruser/get_user_invite_info"}, method = RequestMethod.POST)
    public ResultModel<UserInviteInfoDTO> getUserInviteInfo(@RequestBody @Valid GetUserInviteInfoPO po) {
        UserInviteInfoDTO dto = brokerUserClient.getUserInviteInfo(getOrgId(), po.getUserId(), po.getPhone(), po.getEmail());
        if (dto == null) {
            return ResultModel.ok();
        }
        return ResultModel.ok(dto);
    }

    @BussinessLogAnnotation(opContent = "cancelInviteRelation invitedUserId:{#po.invitedUserId}", entityId = "{#po.invitedUserId}")
    @RequestMapping(value = {"/invite/cancel_invite_relation"}, method = RequestMethod.POST)
    public ResultModel<Void> cancelInviteRelation(@RequestBody @Valid InviteRelationPO po) {
        long orgId = getOrgId();
        BrokerUserDTO dto1 = brokerUserClient.getBrokerUser(orgId, po.getInvitedUserId());
        //BrokerUserDTO dto2 = brokerUserClient.getBorkerUser(orgId, po.getInviteUserId());
        if (dto1 == null) {
            return ResultModel.error("UserId Error!");
        }

        CancelInviteRelationRequest request = CancelInviteRelationRequest.newBuilder()
                .setOrgId(getOrgId())
                //.setInviteUserId(po.getInviteUserId())
                .setInvitedUserId(po.getInvitedUserId()).build();
        CancelInviteRelationResponse response = brokerUserClient.cancelInviteRelation(request);

        return ResultModel.ok();
    }

    @BussinessLogAnnotation(opContent = "rebuildUserInviteRelation inviteUserId:{#po.inviteUserId}  invitedUserId:{#po.invitedUserId}", entityId = "{#po.invitedUserId}")
    @RequestMapping(value = {"/invite/rebuild_invite_relation"}, method = RequestMethod.POST)
    public ResultModel<Void> rebuildUserInviteRelation(@RequestBody @Valid InviteRelationPO po, AdminUserReply adminUser) {
        if (po.getInvitedUserId().equals(po.getInviteUserId())) {
            return ResultModel.error("cant.invite.self");
        }
        long orgId = adminUser.getOrgId();
        BrokerUserDTO dto1 = brokerUserClient.getBrokerUser(orgId, po.getInvitedUserId());
        BrokerUserDTO dto2 = brokerUserClient.getBrokerUser(orgId, po.getInviteUserId());
        if (dto1 == null || dto2 == null) {
            return ResultModel.error("UserId Error!");
        }

        brokerUserClient.bindInviteRelation(orgId, po.getInvitedUserId(), po.getInviteUserId());

        return ResultModel.ok();
    }

    @RequestMapping(value = {"/invite/get_user_invite_relation", "/brokeruser/get_user_invite_relation"}, method = RequestMethod.POST)
    public ResultModel<UserInviteRelationDTO> getUserInviteRelationList(@RequestBody @Valid GetUserInviteRelationPO po) {
        List<UserInviteRelationDTO> dtoList = brokerUserClient.getUserInviteRelation(getOrgId(), po.getUserId(), po.getPhone(), po.getEmail(),
                po.getFromId(), po.getLastId(), po.getStartTime(), po.getEndTime(), po.getLimit());
        if (dtoList == null) {
            return ResultModel.ok();
        }
        return ResultModel.ok(dtoList);
    }

    @RequestMapping(value = "/brokeruser/get_sub_users", method = RequestMethod.POST)
    public ResultModel<List<SubAccountDTO>> getSubUsers(@RequestBody @Valid SimpleBrokerUserPO po) {
        Header header = Header.newBuilder().setOrgId(getOrgId()).setUserId(po.getUserId()).build();
        List<SubAccount> list = brokerUserClient.queryAllSubAccount(QueryAllSubAccountRequest.newBuilder().setHeader(header).build());
        if (CollectionUtils.isEmpty(list)) {
            return ResultModel.ok(new ArrayList<>());
        }

        List<SubAccountDTO> result = list.stream().map(s -> {
            SubAccountDTO dto = new SubAccountDTO();
            BeanCopyUtils.copyPropertiesIgnoreNull(s, dto);
            return dto;
        }).collect(Collectors.toList());

        return ResultModel.ok(result);
    }

    @RequestMapping(value = "/brokeruser/get_sub_user_asset", method = RequestMethod.POST)
    public ResultModel<List<BalanceDetailDTO>> getSubUserAsset(@RequestBody @Valid GetSubUserAssetPO po) {
        Header header = Header.newBuilder().setOrgId(getOrgId()).setUserId(po.getUserId()).build();
        List<SubAccount> subAccounts = brokerUserClient.queryAllSubAccount(QueryAllSubAccountRequest.newBuilder().setHeader(header).build());
        if (CollectionUtils.isEmpty(subAccounts)) {
            return ResultModel.ok(new ArrayList<>());
        }
        boolean valid = subAccounts.stream().anyMatch(s -> s.getAccountId() == po.getAccountId() && s.getStatus() > 0);
        if (!valid) {
            log.error("error userId:{} accountId:{}", po.getUserId(), po.getAccountId());
            return ResultModel.ok(new ArrayList<>());
        }
        List<BalanceDetailDTO> list = accountAssetClient.getBalances(getOrgId(), po.getAccountId());
        if (CollectionUtils.isEmpty(list)) {
            return ResultModel.ok(new ArrayList<>());
        }

        return ResultModel.ok(list);
    }


    @RequestMapping(value = "/brokeruser/get_user_asset", method = RequestMethod.POST)
    public ResultModel<List<BalanceDetailDTO>> getUserAsset(@RequestBody @Valid SimpleBrokerUserPO po) {
        Combo2<Long, Long> combo2 = getUserIdAndAccountId(po, getOrgId());
        if (combo2 == null) {
            return ResultModel.ok(new ArrayList<>());
        }
        List<BalanceDetailDTO> list = accountAssetClient.getBalances(getOrgId(), combo2.getV2());
        if (CollectionUtils.isEmpty(list)) {
            return ResultModel.ok(new ArrayList<>());
        }

        return ResultModel.ok(list);
    }

    @RequestMapping(value = "/brokeruser/get_withdraw_address", method = RequestMethod.POST)
    public ResultModel<List<WithdrawAddressDTO>> getWithdrawAddress(@RequestBody @Valid SimpleBrokerUserPO po) {
        List<WithdrawAddressDTO> dtos = brokerUserClient.getWithdrawAddress(getOrgId(), po.getUserId());
        return ResultModel.ok(dtos);
    }

    @RequestMapping(value = "/brokeruser/query_deposit_address", method = RequestMethod.POST)
    public ResultModel<List<DepositAddressDTO>> queryDepositAddress(@RequestBody @Valid SimpleBrokerUserPO po) {
        List<DepositAddressDTO> dtos = brokerUserClient.queryDepositAddress(getOrgId(), po.getUserId());
        return ResultModel.ok(dtos);
    }

    @BussinessLogAnnotation(opContent = "unbind GA UID:{#po.userId} ", entityId = "{#po.userId}")
    @RequestMapping(value = "/brokeruser/unbind_ga", method = RequestMethod.POST)
    public ResultModel<Void> unbindGA(@RequestBody @Valid SimpleBrokerUserPO po) {
        long adminUserId = getRequestUserId();
        String key = "unbindga.op." + getOrgId() + adminUserId;
        String o = redisTemplate.opsForValue().get(key);
        Integer opTimes = StringUtils.isEmpty(o) ? 0 : Integer.parseInt(o);
        if (opTimes >= BizConstant.UNBIND_GA_MAX_TIMES) {
            return ResultModel.error("unbind.ga.over.maxtimes");
        }
        ResultModel<Void> resultModel = brokerUserClient.unbindGA(getOrgId(), po.getUserId(), adminUserId);
        if (resultModel.getCode() == 0) {
            if (opTimes == 0) {
                redisTemplate.opsForValue().set(key, String.valueOf(opTimes + 1), 24, TimeUnit.HOURS);
            } else {
                long secondToLive = redisTemplate.getExpire(key, TimeUnit.SECONDS);
                redisTemplate.opsForValue().set(key, String.valueOf(opTimes + 1), secondToLive, TimeUnit.SECONDS);
            }
        }
        return resultModel;
    }

    @BussinessLogAnnotation(opContent = "unbind Email UID:{#po.userId} ", entityId = "{#po.userId}")
    @RequestMapping(value = "/brokeruser/unbind_email", method = RequestMethod.POST)
    public ResultModel<Void> unbindEmail(@RequestBody @Valid SimpleBrokerUserPO po) {
        long adminUserId = getRequestUserId();
//        String key = "unbind_email.op." + getOrgId() + adminUserId;
//        String o = redisTemplate.opsForValue().get(key);
//        Integer opTimes = StringUtils.isEmpty(o) ? 0 : Integer.parseInt(o);
//        if (opTimes >= BizConstant.UNBIND_GA_MAX_TIMES) {
//            return ResultModel.error("unbind.ga.over.maxtimes");
//        }
        ResultModel<Void> resultModel = brokerUserClient.unbindEmail(getOrgId(), po.getUserId(), adminUserId);
//        if (resultModel.getCode() == 0) {
//            if (opTimes == 0) {
//                redisTemplate.opsForValue().set(key, String.valueOf(opTimes + 1), 24, TimeUnit.HOURS);
//            } else {
//                long secondToLive = redisTemplate.getExpire(key, TimeUnit.SECONDS);
//                redisTemplate.opsForValue().set(key, String.valueOf(opTimes + 1), secondToLive, TimeUnit.SECONDS);
//            }
//        }
        return resultModel;
    }

    @BussinessLogAnnotation(opContent = "unbind Mobile UID:{#po.userId} ", entityId = "{#po.userId}")
    @RequestMapping(value = "/brokeruser/unbind_mobile", method = RequestMethod.POST)
    public ResultModel<Void> unbindMobile(@RequestBody @Valid SimpleBrokerUserPO po) {
        long adminUserId = getRequestUserId();
//        String key = "unbind_email.op." + getOrgId() + adminUserId;
//        String o = redisTemplate.opsForValue().get(key);
//        Integer opTimes = StringUtils.isEmpty(o) ? 0 : Integer.parseInt(o);
//        if (opTimes >= BizConstant.UNBIND_GA_MAX_TIMES) {
//            return ResultModel.error("unbind.ga.over.maxtimes");
//        }
        ResultModel<Void> resultModel = brokerUserClient.unbindMobile(getOrgId(), po.getUserId(), adminUserId);
//        if (resultModel.getCode() == 0) {
//            if (opTimes == 0) {
//                redisTemplate.opsForValue().set(key, String.valueOf(opTimes + 1), 24, TimeUnit.HOURS);
//            } else {
//                long secondToLive = redisTemplate.getExpire(key, TimeUnit.SECONDS);
//                redisTemplate.opsForValue().set(key, String.valueOf(opTimes + 1), secondToLive, TimeUnit.SECONDS);
//            }
//        }
        return resultModel;
    }



    @BussinessLogAnnotation(opContent = "Disable Api Operation UID:{#po.userId} ", entityId = "{#po.userId}")
    @RequestMapping(value = "/brokeruser/disable_api_operation", method = RequestMethod.POST)
    public ResultModel<Void> disableApiOperation(@RequestBody @Valid SimpleBrokerUserPO po) {
        ResultModel<Void> resultModel = brokerUserClient.disableApiOperation(getOrgId(), po.getUserId());
        return resultModel;
    }

    @BussinessLogAnnotation(opContent = "Reopen Api Operation UID:{#po.userId} ", entityId = "{#po.userId}")
    @RequestMapping(value = "/brokeruser/reopen_api_operation", method = RequestMethod.POST)
    public ResultModel<Void> reopenApiOperation(@RequestBody @Valid SimpleBrokerUserPO po) {
        ResultModel<Void> resultModel = brokerUserClient.reopenApiOperation(getOrgId(), po.getUserId());
        return resultModel;
    }

    @BussinessLogAnnotation(opContent = "LockBalance UIDs:{#po.userIds} amount:{#po.amount} token:{#po.tokenId}")
    @RequestMapping(value = "/brokeruser/lock_balance", method = RequestMethod.POST)
    public ResultModel<String> userLockBalance(@RequestBody @Valid UserLockPo po) {
        ResultModel<String> resultModel = brokerUserClient.userLockBalance(getOrgId(), po, getRequestUserId());
        return resultModel;
    }

    @BussinessLogAnnotation(opContent = "UnlockBalance UID:{#po.userId} amount:{#po.amount} token:--", entityId = "{#po.userId}")
    @RequestMapping(value = "/brokeruser/unlock_balance", method = RequestMethod.POST)
    public ResultModel<String> userUnlockBalance(@RequestBody @Valid UserUnlockPo po, AdminUserReply adminUser) {
        ResultModel<String> resultModel = brokerUserClient.userUnlockBalance(adminUser.getOrgId(), po.getLockId(), po.getUserId(),
                po.getAmount().toPlainString(), po.getMark(), getRequestUserId());
        return resultModel;
    }

    @RequestMapping(value = "/brokeruser/query_lock_balance", method = RequestMethod.POST)
    public ResultModel<String> queryUserLockBalance(@RequestBody @Valid QueryUserLockPo po) {
        ResultModel<String> resultModel = brokerUserClient.queryUserLockBalance(getOrgId(), po.getUserId(), po.getTokenId());
        return resultModel;
    }

    @RequestMapping(value = "/brokeruser/query_lock_list", method = RequestMethod.POST)
    public ResultModel<String> queryLockBalanceLogListByUserId(@RequestBody @Valid QueryUserLockListPo po) {
        if (po.getSize() == null) {
            po.setSize(20);
        }
        if (po.getPage() == null) {
            po.setPage(1);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("list", brokerUserClient.queryLockBalanceLogListByUserId(getOrgId(), po.getUserId(), po.getPage(), po.getSize(),po.getType()));
        map.put("page", po.getPage());
        map.put("size", po.getSize());
        return ResultModel.ok(map);
    }

    @RequestMapping(value = "/brokeruser/unfreeze_user", method = RequestMethod.POST)
    public ResultModel<Void> unfreezeUser(@RequestBody @Valid UnfreezeUserPO po, AdminUserReply adminUser) {
        String key = "unfreeze.user.op." + getOrgId() + "."+adminUser.getId();
        String o = redisTemplate.opsForValue().get(key);
        Integer opTimes = StringUtils.isEmpty(o) ? 0 : Integer.parseInt(o);
        if (opTimes >= BizConstant.UNBIND_GA_MAX_TIMES) {
            return ResultModel.error("unbind.ga.over.maxtimes");
        }
        adminLoginUserService.verifyAdvance(po.getAuthType(), po.getVerifyCode(), adminUser.getId(), adminUser.getOrgId(), getAdminPlatform());
        ResultModel<Void> resultModel = brokerUserClient.unfreezeUser(adminUser.getOrgId(), po.getUserId(), po.getFrozenType());
        if (resultModel.getCode() == 0) {
            if (opTimes == 0) {
                redisTemplate.opsForValue().set(key, String.valueOf(opTimes + 1), 24, TimeUnit.HOURS);
            } else {
                long secondToLive = redisTemplate.getExpire(key, TimeUnit.SECONDS);
                redisTemplate.opsForValue().set(key, String.valueOf(opTimes + 1), secondToLive, TimeUnit.SECONDS);
            }
        }
        return resultModel;
    }

    @RequestMapping(value = "/{path}/get_user_withdraw_setting", method = RequestMethod.POST)
    public ResultModel<Void> getUserWithdrawSetting(@RequestBody @Valid UserBlackWhiteSettingDTO po, @PathVariable String path) {
        if (!path.equals("brokeruser") && !path.equals("userrisk")) {
            return ResultModel.error("request.parameter.error");
        }
        Map<String, Integer> result = new HashMap<>();

        GetUserBlackWhiteConfigRequest.Builder builder = GetUserBlackWhiteConfigRequest.newBuilder();
        BeanCopyUtils.copyPropertiesIgnoreNull(po, builder);
        builder.setHeader(Header.newBuilder().setOrgId(getOrgId()).build());


        builder.setListTypeValue(UserBlackWhiteListType.WITHDRAW_BLACK_WHITE_LIST_TYPE_VALUE);
        builder.setBwType(UserBlackWhiteType.BLACK_CONFIG);
        GetUserBlackWhiteConfigRequest request = builder.build();

        UserBlackWhiteConfig blackGrpcConfig = brokerUserClient.getUserBlackWhiteConfig(request);
        if (blackGrpcConfig.getId() == 0) {
            result.put(UserBlackWhiteType.BLACK_CONFIG.name(), 0);
        } else {
            result.put(UserBlackWhiteType.BLACK_CONFIG.name(), blackGrpcConfig.getStatus());
        }

        request = request.toBuilder().setBwType(UserBlackWhiteType.WHITE_CONFIG).build();
        UserBlackWhiteConfig whiteGrpcConfig = brokerUserClient.getUserBlackWhiteConfig(request);
        if (whiteGrpcConfig.getId() == 0) {
            result.put(UserBlackWhiteType.WHITE_CONFIG.name(), 0);
        } else {
            result.put(UserBlackWhiteType.WHITE_CONFIG.name(), whiteGrpcConfig.getStatus());
        }

//        UserBlackWhiteSettingDTO result = new UserBlackWhiteSettingDTO();
//        BeanCopyUtils.copyPropertiesIgnoreNull(grpcConfig, result);
//        result.setBwType(grpcConfig.getBwTypeValue());
//        result.setListType(grpcConfig.getListTypeValue());

        return ResultModel.ok(result);
    }


    @RequestMapping(value = "/brokeruser/user_action_logs", method = RequestMethod.POST)
    public ResultModel<Void> queryUserActionLogs(@RequestBody @Valid UserActionLogsPO po) {
        Header header = Header.newBuilder().setUserId(po.getUserId()).setOrgId(getOrgId()).build();
        List<UserActionRecord> grpcRecords = brokerUserClient.queryUserActionLogs(QueryLogsRequest.newBuilder().setHeader(header)
                .setPageSize(po.getPageSize())
                .setFromId(po.getLastId()).build());
        List<UserActionRecordDTO> result = grpcRecords.stream().map(r -> {
            UserActionRecordDTO dto = new UserActionRecordDTO();
            BeanCopyUtils.copyPropertiesIgnoreNull(r, dto);
            return dto;
        }).collect(Collectors.toList());
        return ResultModel.ok(result);
    }

    @RequestMapping(value = "/{path}/user_rc_records", method = RequestMethod.POST)
    public ResultModel<Void> queryUserRiskLogs(@RequestBody @Valid UserRcRecordsPO po, @PathVariable String path) {
        if (!path.equals("brokeruser") && !path.equals("userrisk")) {
            return ResultModel.error("request.parameter.error");
        }
        List<GetRcListResponse.Item> grpcRecords = brokerUserClient.getUserRcList(GetRcListRequest.newBuilder()
                .setRcType(po.getRcType())
                .setBrokerId(getOrgId())
                .setPageSize(po.getPageSize())
                .setFromId(po.getLastId()).build());
        List<UserRiskRecordDTO> result = grpcRecords.stream().map(r -> {
            UserRiskRecordDTO dto = new UserRiskRecordDTO();
            BeanCopyUtils.copyPropertiesIgnoreNull(r, dto);
            return dto;
        }).collect(Collectors.toList());
        return ResultModel.ok(result);
    }

    @RequestMapping(value = "/brokeruser/user_switches", method = RequestMethod.POST)
    public ResultModel getUserConfigSwitches(@RequestBody UserSwitchesPO po, AdminUserReply adminUser) {

        List<Map<String, Object>> list = new ArrayList<>();
        for (String group : po.getGroups()) {
            BaseConfigPO configPO = new BaseConfigPO();
            configPO.setGroup(group);
            configPO.setKey(po.getUserId().toString());
            if (group.endsWith(".bh")) {
                configPO.setOpPlatform(BaseConfigPO.OP_PLATFORM_BH);
            }
            configPO.setWithLanguage(false);


            BaseConfigDTO configDTO = baseConfigService.getOneConfig(getOrgId(), configPO);
            Map<String, Object> item = new HashMap<>();
            item.put("group", group);
            if (configDTO != null) {
                boolean opened = configDTO.getValue().equalsIgnoreCase("true");
                item.put("open", opened);
                if (opened) {
                    item.put("username", configDTO.getAdminUserName());
                    item.put("created", configDTO.getUpdated());
                }
            } else {
                item.put("open", false);
                item.put("source", "");
            }


            list.add(item);
        }

        return ResultModel.ok(list);
    }




    @RequestMapping(value = "/brokeruser/otc/payments")
    public ResultModel<OTCPaymentDTO> listPayment(@RequestBody @NotNull IdPO idPO) {
        List<OTCPaymentDTO> list = otcService.listPayments(getOrgId(), idPO.getId());
        return ResultModel.ok(list);
    }

    @RequestMapping(value = "/brokeruser/otc/user_info")
    public ResultModel<OtcWhiteUserDTO> findOtcInfo(@RequestBody @NotNull IdPO idPO) {
        List<OtcWhiteUserDTO> list = otcService.listUser(1, 1, Lists.newArrayList(idPO.getId()), getOrgId());
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(list)) {
            return ResultModel.ok();
        }

        return ResultModel.ok(list.get(0));
    }

    @RequestMapping(value = "/brokeruser/otc/nickname/modify")
    public ResultModel<Boolean> modifyOtcNickname(@RequestBody @NotNull KeyValueParamPO kvParam) {

        String nickname = kvParam.getValue();
        long accountId = 0;

        if (StringUtils.isEmpty(kvParam.getKey()) || StringUtils.isEmpty(nickname)) {
            log.warn("modify nickname fail,invalid param");
            return ResultModel.error("invalid param");
        }

        log.info("modify nickname,param={}", JSON.toJSONString(kvParam));

        try {
            accountId = Long.parseLong(kvParam.getKey());
        } catch (Exception e) {
            return ResultModel.error("invalid accountId," + kvParam.getKey());
        }

        boolean success = otcService.modifyNickname(getOrgId(), accountId, nickname);
        return ResultModel.ok(success);
    }


    @BussinessLogAnnotation(opContent = "batchUnlockAirDorp UID:{#po.userIds} unlockType:{#po.unlockType} tokenId:{#po.tokenId}")
    @RequestMapping(value = "/brokeruser/batch_unlock_airdrop", method = RequestMethod.POST)
    public ResultModel<String> batchUnlockAirDorp(@RequestBody @Valid UserBatchUnlockAirDropPo po, AdminUserReply adminUser) {
        ResultModel<String> resultModel = brokerUserClient.batchUnlockAirDrop(adminUser.getOrgId(), po.getUnlockType(),po.getTokenId(),
                po.getUserIds(),po.getMark());
        return resultModel;
    }

    @RequestMapping(value = "/brokeruser/margin/position_status", method = RequestMethod.POST)
    public ResultModel queryMarginPositionStatus(@RequestBody @Valid QueryMarginPositionStatusPO po, AdminUserReply adminUser) {
        MarginPositionStatusDTO dto = marginService.queryMarginPositionStatus(adminUser.getOrgId(),po.userId);
        return ResultModel.ok(dto);
    }
    @RequestMapping(value = "/brokeruser/margin/change_position_status", method = RequestMethod.POST)
    public ResultModel changeMarginPositionStatus(@RequestBody @Valid ChangeMarginPositionStatusPO po, AdminUserReply adminUser) {
        AdminChangeMarginPositionStatusResponse response = marginService.changeMarginPositionStatus(adminUser.getOrgId(),po.userId,po.changeToStatus,po.getCurStatus());
        if (response.getRet()!= 0 ){
            return  ResultModel.error("change margin position status error");
        }
        ChangeMarginPositionStatusDTO dto = ChangeMarginPositionStatusDTO.builder()
                .curStatus(response.getCurStatus())
                .build();
        return ResultModel.ok(dto);
    }

}
