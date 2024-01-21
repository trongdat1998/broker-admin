package io.bhex.broker.admin.controller;

import com.google.common.base.Strings;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.bizlog.BussinessLogAnnotation;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.bhop.common.util.validation.TokenValid;
import io.bhex.broker.admin.controller.dto.BrokerUserDTO;
import io.bhex.broker.admin.controller.dto.InviteBonusRecordDTO;
import io.bhex.broker.admin.controller.param.*;
import io.bhex.broker.admin.grpc.client.BrokerUserClient;
import io.bhex.broker.admin.service.InviteFeeBackService;
import io.bhex.broker.grpc.invite.GetInviteFeeBackActivityResponse;
import io.bhex.broker.grpc.invite.InviteLevel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Date: 2018/11/6 下午6:07
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/invite_fee_back")
public class InviteFeeBackController extends BrokerBaseController {

    @Autowired
    private InviteFeeBackService inviteFeeBackService;
    @Autowired
    private BrokerUserClient brokerUserClient;

    @RequestMapping(value = "/levels", method = RequestMethod.POST)
    public ResultModel getLevels(AdminUserReply adminUser) {
        GetInviteFeeBackActivityResponse activityResponse = inviteFeeBackService.getInviteFeeBackActivity(adminUser.getOrgId());
        Map<String, Object> data = new HashMap<>();
        data.put("showStatus", activityResponse.getActivity().getStatus());
        data.put("actId", activityResponse.getActivity().getId());
        data.put("coinStatus", activityResponse.getActivity().getCoinStatus());
        data.put("futuresStatus", activityResponse.getActivity().getFuturesStatus());


        long period = activityResponse.getActivity().getPeriod();
        data.put("fixed", period > 0);
        data.put("fixedTimeInMonth", period / (31 * 24 * 3600 * 1000L));
        List<InviteFeeBackLevelRes> levels = new ArrayList<>();
        for (InviteLevel inviteLevel : activityResponse.getLevelListList()) {
            InviteFeeBackLevelRes res = new InviteFeeBackLevelRes();
            res.setLevelId(inviteLevel.getId());
            res.setLevel(inviteLevel.getLevel());
            res.setLevelCondition(Integer.parseInt(inviteLevel.getLevelCondition()));
            res.setLevelTag(inviteLevel.getLevelTag());
            res.setIndirectRate(new BigDecimal(inviteLevel.getIndirectRate()));
            res.setDirectRate(new BigDecimal(inviteLevel.getDirectRate()));
            levels.add(res);
        }
        data.put("list", levels);
        data.put("configList", inviteFeeBackService.getInviteCommonSetting(adminUser.getOrgId()));
        return ResultModel.ok(data);
    }

    @BussinessLogAnnotation(opContent = "Edit Invite Rebate Setting")
    @RequestMapping(value = "/edit_invite_common_setting", method = RequestMethod.POST)
    public ResultModel editInviteCommonSetting(@RequestBody @Valid InviteCommonSettingPOs po, AdminUserReply adminUser) {
        inviteFeeBackService.editInviteCommonSetting(adminUser.getOrgId(), po.getList());
        return ResultModel.ok();
    }

    private boolean checkPrecious(BigDecimal val) {
        BigDecimal c = val.multiply(new BigDecimal("10000"));
        return c.subtract(c.setScale(0, BigDecimal.ROUND_DOWN)).compareTo(BigDecimal.ZERO) > 0;
    }

    @BussinessLogAnnotation(opContent = "Edit Invite Rebate Level Setting")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public ResultModel editLevel(@RequestBody @Valid InviteFeeBackLevelEditPO po) {
        if (checkPrecious(po.getDirectRate())) {
            return ResultModel.errorParameter("directRate", "precision.error");
        }
        if (checkPrecious(po.getIndirectRate())) {
            return ResultModel.errorParameter("indirectRate", "precision.error");
        }

        inviteFeeBackService.updateInviteFeeBackLevel(getOrgId(), po.getActId(), po.getLevelId(),
                po.getLevelCondition(), po.getDirectRate(), po.getIndirectRate());

        return ResultModel.ok();
    }

    @BussinessLogAnnotation(opContent = "{#po.status == 1 ? 'Open' : 'Close'} Invite Rebate Activity")
    @RequestMapping(value = "/enable_activity", method = RequestMethod.POST)
    public ResultModel enableFeeBackActivity(@RequestBody @Valid InviteFeeBackLevelEnablePO po) {
        inviteFeeBackService.enableInviteFeeBackActivity(getOrgId(), po.getActId(), po.getStatus(), po.getCoinStatus(), po.getFuturesStatus());
        return ResultModel.ok();
    }

    //    int64                           actId                           = 2;
//    @BussinessLogAnnotation
//    @RequestMapping(value = "/disable_activity", method = RequestMethod.POST)
//    public ResultModel disableFeeBackActivity(@RequestBody @Valid InviteFeeBackLevelEnablePO po) {
//        inviteFeeBackService.disableInviteFeeBackActivity(getOrgId(), po.getActId());
//        return ResultModel.ok();
//    }

    @RequestMapping(value = "/update_period", method = RequestMethod.POST)
    public ResultModel updateInviteFeebackPeriod(@RequestBody @Valid UpdateInviteFeebackPeriodPO po) {

        boolean success = inviteFeeBackService.updateInviteFeebackPeriod(getOrgId(),
                po.getFixed() ? po.getFixedTimeInMonth() : 0);
        return success ? ResultModel.ok() : ResultModel.error("");
    }

    @RequestMapping(value = "/invite_black_list", method = RequestMethod.POST)
    public ResultModel queryBlackList(@RequestBody @Valid QueryPagePO po) {
        int pageSize = po.getPageSize() > 0 ? po.getPageSize() : 10;
        int current = po.getCurrent() > 0 ? po.getCurrent() : 1;
        List<InviteBlackUserRes> list = inviteFeeBackService.getInviteBlackList(getOrgId(), null,
                current, pageSize);
        return ResultModel.ok(list);
    }

    @BussinessLogAnnotation(opContent = "Delete Invite Rebate BlackList UID:{#po.userId}")
    @RequestMapping(value = "/delete_invite_black_list", method = RequestMethod.POST)
    public ResultModel deleteInviteBlackList(@RequestBody @Valid SimpleBrokerUserPO po) {
        BrokerUserDTO dto = brokerUserClient.getBrokerUser(getOrgId(),
                po.getUserId(), null, null, null);
        if (dto == null) {
            return ResultModel.validateFail("userId.error", po.getUserId());
        }
        boolean success = inviteFeeBackService.deleteInviteBlackList(getOrgId(), po.getUserId());
        return success ? ResultModel.ok() : ResultModel.error("");
    }

    @BussinessLogAnnotation(opContent = "Add Invite Rebate BlackList UID:{#po.userId}")
    @RequestMapping(value = "/add_invite_black_list", method = RequestMethod.POST)
    public ResultModel addInviteBlackList(@RequestBody @Valid SimpleBrokerUserPO po) {
        BrokerUserDTO dto = brokerUserClient.getBrokerUser(getOrgId(),
                po.getUserId(), null, null, null);
        if (dto == null) {
            return ResultModel.validateFail("userId.error", po.getUserId());
        }
        //// 添加黑名单 返回 ret = 0 成功  -1:用户不存在  -2: 该用户在黑名单中 -999：系统异常
        int result = inviteFeeBackService.addInviteBlackList(getOrgId(), po.getUserId());
        if (result == 0) {
            return ResultModel.ok();
        }
        if (result == -2) {
            return ResultModel.error("in.invite.feedback.blacklist");
        }
        return ResultModel.error("result code is " + result);
    }

    @BussinessLogAnnotation(opContent = "{#po.enable ? 'Open Automatic Transfer' : 'Open Manual Transfer'} ")
    @RequestMapping(value = "/auto_transfer", method = RequestMethod.POST)
    public ResultModel updateInviteFeebackAutoTransfer(@RequestBody @Valid EnablePO po) {
        boolean success = inviteFeeBackService.updateInviteFeebackAutoTransfer(getOrgId(), po.getEnable());
        log.info("result:{}", success);
        return success ? ResultModel.ok() : ResultModel.error("");
    }

    @RequestMapping(value = "/invite_statistics_daily_list", method = RequestMethod.POST)
    public ResultModel getInviteStatisticsRecordList(@RequestBody @Valid InviteStatisticsRecordListPO po) {
        GetInviteFeeBackActivityResponse activityResponse = inviteFeeBackService.getInviteFeeBackActivity(getOrgId());
        Map<String, Object> map = new HashMap<>();
        if (po.getPage() == null) {
            po.setPage(1);
        }
        if (po.getLimit() == null) {
            po.setLimit(30);
        }
        List<InviteDailyTaskRes> list = inviteFeeBackService.getDailyTaskList(getOrgId(), po.getPage(), po.getLimit());
        map.put("autoTransfer", activityResponse.getActivity().getAutoTransfer() == 1);
        map.put("list", list);
        map.put("page", po.getPage());
        map.put("limit", po.getLimit());
        return ResultModel.ok(map);
    }

    @RequestMapping(value = "/invite_statistics_daily_detail", method = RequestMethod.POST)
    public ResultModel getInviteStatisticsRecordDetail(@RequestBody @Valid StatisticsTimePO po) {
        List<InviteStatisticsRecordRes> list = inviteFeeBackService.getInviteStatisticsRecordList(getOrgId(),
                po.getStatisticsTime(), 1, 500);
        return ResultModel.ok(list);
    }

    @RequestMapping(value = "/invite_bonus_record", method = RequestMethod.POST)
    public ResultModel getInviteBonusRecord(@RequestBody @Valid InviteBonusRecordListPO po) {
        List<InviteBonusRecordDTO> list = inviteFeeBackService.getInviteBonusRecord(getOrgId(),
                po.getStatisticsTime() == null ? 0 : po.getStatisticsTime(),
                po.getUserId() == null ? 0 : po.getUserId(),
                Strings.nullToEmpty(po.getToken()),
                po.getPage(), po.getPageSize());

        Map<String, Object> result = new HashMap<>();
        result.put("page", po.getPage());
        result.put("list", list);
        return ResultModel.ok(result);
    }


    @BussinessLogAnnotation(opContent = "Execute Rebate Send Bonus")
    @RequestMapping(value = "/execute_send_bonus", method = RequestMethod.POST)
    public ResultModel executeAdminGrantInviteBonus(@RequestBody @Valid StatisticsTimePO po) {
        // 触发执行邀请返佣发放 返回ret = 0 成功 -1：活动不存在 -2：已有任务在执行
        int result = inviteFeeBackService.executeAdminGrantInviteBonus(getOrgId(), po.getStatisticsTime());
        if (result == 0) {
            return ResultModel.ok();
        }
        if (result == -2) {
            log.warn("org:{} time:{} task is doing", getOrgId(), "");
            return ResultModel.ok();
        }
        return ResultModel.error("result code is " + result);
    }

    @BussinessLogAnnotation
    @RequestMapping(value = "/create_user_bonus_record", method = RequestMethod.POST)
    public ResultModel createUserBonusRecord(@RequestBody @Valid StatisticsTimePO po) {
        if (inviteFeeBackService.createUserBonusRecord(getOrgId(), po.getStatisticsTime())) {
            return ResultModel.ok();
        } else {
            return ResultModel.error("");
        }
    }

    @Data
    private static class EnablePO {
        private Boolean enable;
    }

    @Data
    private static class StatisticsTimePO {
        private Long statisticsTime;
    }

    @Data
    private static class InviteCommonSettingPOs {
        List<InviteCommonSettingPO> list;
    }

    @Data
    private static class InviteStatisticsRecordListPO {
        private Integer page;
        @Max(value = 500)
        private Integer limit;
    }

    @Data
    private static class InviteBonusRecordListPO {

        private Long statisticsTime;

        private Integer page;

        @Max(value = 500)
        private Integer pageSize;

        private Long userId;

        @TokenValid
        private String token;

    }
}
