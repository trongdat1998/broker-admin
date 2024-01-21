package io.bhex.broker.admin.service;

import io.bhex.broker.admin.controller.dto.InviteBonusRecordDTO;
import io.bhex.broker.admin.controller.dto.InviteCommonSettingDTO;
import io.bhex.broker.admin.controller.param.InviteBlackUserRes;
import io.bhex.broker.admin.controller.param.InviteCommonSettingPO;
import io.bhex.broker.admin.controller.param.InviteDailyTaskRes;
import io.bhex.broker.admin.controller.param.InviteStatisticsRecordRes;
import io.bhex.broker.grpc.invite.GetInviteFeeBackActivityResponse;

import java.math.BigDecimal;
import java.util.List;


/**
 * @Description:
 * @Date: 2018/11/6 下午5:47
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
public interface InviteFeeBackService {

    List<InviteBonusRecordDTO> getInviteBonusRecord(long orgId, long statisticsTime, long userId, String token, int page, int limit);

    GetInviteFeeBackActivityResponse getInviteFeeBackActivity(Long orgId);

    boolean updateInviteFeeBackLevel(Long orgId, Long actId, Long levelId,
                                     Integer levelCondition, BigDecimal directRate, BigDecimal indirectRate);

    boolean enableInviteFeeBackActivity(Long orgId, Long actId, Integer status, Integer coinStatus, Integer futuresStatus);

//    boolean disableInviteFeeBackActivity(Long orgId, Long actId);

    List<InviteBlackUserRes> getInviteBlackList(Long orgId, Long userId, int page, int limit);

    int addInviteBlackList(Long orgId, Long userId);

    boolean deleteInviteBlackList(Long orgId, Long userId);

    /**
     * @param orgId
     * @param timeInMonth 有效期单位月，如果为永久请写0或者负数
     * @return
     */
    boolean updateInviteFeebackPeriod(Long orgId, int timeInMonth);

    // 修改自动转账开关 返回ret = 0 成功  -1：活动不存在 -2：开关枚举错误 -999：系统异常
    boolean updateInviteFeebackAutoTransfer(Long orgId, boolean autoTransfer);

    List<InviteDailyTaskRes> getDailyTaskList(Long orgId, Integer page, Integer limit);

    List<InviteStatisticsRecordRes> getInviteStatisticsRecordList(Long orgId, long statisticsTime, int page, int limit);

    int executeAdminGrantInviteBonus(Long orgId, long statisticsTime);

    List<InviteCommonSettingDTO> getInviteCommonSetting(Long orgId);

    boolean editInviteCommonSetting(Long orgId, List<InviteCommonSettingPO> po);

    boolean createUserBonusRecord(Long orgId, Long statisticsTime);
}
