package io.bhex.broker.admin.grpc.client;

import io.bhex.broker.grpc.invite.*;

import java.math.BigDecimal;

/**
 * @Description:
 * @Date: 2018/11/6 下午5:56
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
public interface InviteFeeBackClient {


    //获取邀请返佣列表
    GetAdminInviteBonusRecordResponse getAdminInviteBonusRecord(long orgId, long statisticsTime, long userId, String token, int page, int limit);

    InitInviteFeeBackActivityResponse initInviteFeeBackActivity(Long orgId);

    GetInviteFeeBackActivityResponse getInviteFeeBackActivity(Long orgId);

    UpdateInviteFeeBackLevelResponse updateInviteFeeBackLevel(Long orgId, Long actId, Long levelId,
                                                              Integer levelCondition, BigDecimal directRate, BigDecimal indirectRate);

    UpdateInviteFeeBackActivityResponse updateInviteFeeBackActivity(Long orgId, Long actId, Integer status, Integer coinStatus, Integer futuresStatus);

    GetInviteBlackListResponse getInviteBlackList(GetInviteBlackListRequest request);

    AddInviteBlackListResponse addInviteBlackList(AddInviteBlackListRequest request);

    DeleteInviteBlackListResponse deleteInviteBlackList(DeleteInviteBlackListRequest request);

    // 修改有效期限  请求参数中 period 为毫秒数  0为永久  返回ret = 0 成功  -1：活动不存在 -999：系统异常
    UpdateInviteFeebackPeriodResponse updateInviteFeebackPeriod(UpdateInviteFeebackPeriodRequest request);

    // 修改自动转账开关 返回ret = 0 成功  -1：活动不存在 -2：开关枚举错误 -999：系统异常
    UpdateInviteFeebackAutoTransferResponse updateInviteFeebackAutoTransfer(UpdateInviteFeebackAutoTransferRequest request);


    GetInviteStatisticsRecordListResponse getInviteStatisticsRecordList(GetInviteStatisticsRecordListRequest request);


    GetDailyTaskListResponse getDailyTaskList(GetDailyTaskListRequest request);

    ExecuteAdminGrantInviteBonusResponse executeAdminGrantInviteBonus(ExecuteAdminGrantInviteBonusRequest request);

    // 获取邀请返佣通用设置列表， 就是各种文案 这个接口除非异常不会为空， 但是value可能是空的
    GetInviteCommonSettingResponse getInviteCommonSetting(GetInviteCommonSettingRequest request);

    // 修改邀请返佣通用设置 ， 参数就是上个接口的返回队形， orgId \ key \ language 必传哈！ 然后desc字段默认都给带个话吧，看你们心情
    UpdateInviteCommonSettingResponse updateInviteCommonSetting(UpdateInviteCommonSettingRequest request);

    GenerateAdminInviteBonusRecordResponse generateAdminInviteBonusRecord(GenerateAdminInviteBonusRecordRequest request);

    void initInviteWechatConfig(Long orgId);
}