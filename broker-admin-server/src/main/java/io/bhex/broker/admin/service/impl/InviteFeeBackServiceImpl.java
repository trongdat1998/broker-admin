package io.bhex.broker.admin.service.impl;


import com.google.common.collect.Lists;
import io.bhex.bhop.common.exception.BizException;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.broker.admin.constants.BizConstant;
import io.bhex.broker.admin.controller.dto.InviteBonusRecordDTO;
import io.bhex.broker.admin.controller.dto.InviteCommonSettingDTO;
import io.bhex.broker.admin.controller.param.InviteBlackUserRes;
import io.bhex.broker.admin.controller.param.InviteCommonSettingPO;
import io.bhex.broker.admin.controller.param.InviteDailyTaskRes;
import io.bhex.broker.admin.controller.param.InviteStatisticsRecordRes;
import io.bhex.broker.admin.grpc.client.InviteFeeBackClient;
import io.bhex.broker.admin.service.InviteFeeBackService;
import io.bhex.broker.common.util.JsonUtil;
import io.bhex.broker.grpc.invite.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class InviteFeeBackServiceImpl implements InviteFeeBackService {

    @Autowired
    private InviteFeeBackClient inviteFeeBackClient;

    @Override
    public List<InviteBonusRecordDTO> getInviteBonusRecord(long orgId, long statisticsTime, long userId, String token, int page, int limit) {
        GetAdminInviteBonusRecordResponse response = inviteFeeBackClient
                .getAdminInviteBonusRecord(orgId, statisticsTime, userId, token, page, limit);
        List<InviteBonusRecord> list = response.getRecordListList();
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        List<InviteBonusRecordDTO> reslist = new ArrayList<>();
        for (InviteBonusRecord record : list) {
            InviteBonusRecordDTO res = new InviteBonusRecordDTO();
            BeanUtils.copyProperties(record, res);
            reslist.add(res);
        }

        return reslist;
    }

    @Override
    public GetInviteFeeBackActivityResponse getInviteFeeBackActivity(Long orgId) {
        GetInviteFeeBackActivityResponse response = inviteFeeBackClient.getInviteFeeBackActivity(orgId);
        if (CollectionUtils.isEmpty(response.getLevelListList())) {
            inviteFeeBackClient.initInviteFeeBackActivity(orgId);
            response = inviteFeeBackClient.getInviteFeeBackActivity(orgId);
        }
        return response;
    }

    @Override
    public boolean updateInviteFeeBackLevel(Long orgId, Long actId, Long levelId,
                                            Integer levelCondition, BigDecimal directRate, BigDecimal indirectRate) {
        UpdateInviteFeeBackLevelResponse response = inviteFeeBackClient
                .updateInviteFeeBackLevel(orgId, actId, levelId, levelCondition, directRate, indirectRate);

        return response.getRet() == 0;
    }

    @Override
    public boolean enableInviteFeeBackActivity(Long orgId, Long actId, Integer status, Integer coinStatus, Integer futuresStatus) {
        UpdateInviteFeeBackActivityResponse response = inviteFeeBackClient
                .updateInviteFeeBackActivity(orgId, actId, status, coinStatus, futuresStatus);
        return response.getRet() == 0;
    }

//    @Override
//    public boolean disableInviteFeeBackActivity(Long orgId, Long actId) {
//        UpdateInviteFeeBackActivityResponse response = inviteFeeBackClient
//                .updateInviteFeeBackActivity(orgId, actId, false);
//        return response.getRet() == 0;
//    }

    @Override
    public List<InviteBlackUserRes> getInviteBlackList(Long orgId, Long userId, int page, int limit) {
        GetInviteBlackListRequest request = GetInviteBlackListRequest.newBuilder()
                .setOrgId(orgId)
                .setUserId(userId == null ? 0L : userId)
                .setPage(page)
                .setLimit(limit)
                .build();
        GetInviteBlackListResponse response = inviteFeeBackClient.getInviteBlackList(request);
        List<InviteBlackUser> users = response.getUsersList();
        if (CollectionUtils.isEmpty(users)) {
            return new ArrayList<>();
        }

        List<InviteBlackUserRes> reslist = new ArrayList<>();
        for (InviteBlackUser user : users) {
            InviteBlackUserRes res = new InviteBlackUserRes();
            BeanUtils.copyProperties(user, res);
            String contact = res.getUserContact();
            if (contact.contains("@")) {
                res.setUserContact(res.getUserContact().replaceAll("(?<=.).(?=[^@]*?.@)", "*"));
            } else {
                res.setUserContact(res.getUserContact().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
            }
            reslist.add(res);
        }
        return reslist;
    }

    @Override
    public int addInviteBlackList(Long orgId, Long userId) {
        AddInviteBlackListResponse response = inviteFeeBackClient
                .addInviteBlackList(AddInviteBlackListRequest.newBuilder().setOrgId(orgId).setUserId(userId).build());
        log.info("orgId:{} userId:{} response:{}", orgId, userId, response);
        // 添加黑名单 返回 ret = 0 成功  -1:用户不存在  -2: 该用户在黑名单中 -999：系统异常
        return response.getRet();
    }

    @Override
    public boolean deleteInviteBlackList(Long orgId, Long userId) {
        DeleteInviteBlackListResponse response = inviteFeeBackClient
                .deleteInviteBlackList(DeleteInviteBlackListRequest.newBuilder().setOrgId(orgId).setUserId(userId).build());
        //删除黑名单 返回 ret = 0 成功  -1:用户不在黑名单中  -999：系统异常
        return response.getRet() == 0 || response.getRet() == -1;
    }

    @Override
    public boolean updateInviteFeebackPeriod(Long orgId, int timeInMonth) {
        UpdateInviteFeebackPeriodRequest request = UpdateInviteFeebackPeriodRequest.newBuilder()
                .setOrgId(orgId)
                .setPeriod(timeInMonth <= 0 ? 0L : timeInMonth * 31 * 24 * 3600 * 1000L)
                .build();
        UpdateInviteFeebackPeriodResponse response = inviteFeeBackClient.updateInviteFeebackPeriod(request);
        log.info("orgId:{} timeInMonth:{} response:{}", orgId, timeInMonth, response);
        return response.getRet() == 0;
    }

    @Override
    public boolean updateInviteFeebackAutoTransfer(Long orgId, boolean autoTransfer) {
        UpdateInviteFeebackAutoTransferRequest request = UpdateInviteFeebackAutoTransferRequest.newBuilder()
                .setOrgId(orgId)
                .setStatus(autoTransfer ? 1 : 0)
                .build();
        UpdateInviteFeebackAutoTransferResponse response = inviteFeeBackClient.updateInviteFeebackAutoTransfer(request);
        log.info("orgId:{} autoTransfer:{} response:{}", orgId, autoTransfer, response);
        return response.getRet() == 0;
    }

    @Override
    public List<InviteStatisticsRecordRes> getInviteStatisticsRecordList(Long orgId, long statisticsTime, int page, int limit) {
        GetInviteStatisticsRecordListRequest request = GetInviteStatisticsRecordListRequest.newBuilder()
                .setOrgId(orgId)
                .setStatisticsTime(statisticsTime)
                //   .setToken(token)
                .setPage(page)
                .setLimit(limit)
                .build();
        GetInviteStatisticsRecordListResponse response = inviteFeeBackClient.getInviteStatisticsRecordList(request);
        List<InviteStatisticsRecord> records = response.getRecordsList();
        if (CollectionUtils.isEmpty(records)) {
            return new ArrayList<>();
        }

        List<InviteStatisticsRecordRes> reslist = new ArrayList<>();
        for (InviteStatisticsRecord record : records) {
            InviteStatisticsRecordRes res = new InviteStatisticsRecordRes();
            BeanUtils.copyProperties(record, res);
            res.setAmount(new BigDecimal(record.getAmount()));
            res.setTransferAmount(new BigDecimal(record.getTransferAmount()));
            reslist.add(res);
        }

        return reslist;
    }

    @Override
    public List<InviteDailyTaskRes> getDailyTaskList(Long orgId, Integer page, Integer limit) {
        GetDailyTaskListRequest request = GetDailyTaskListRequest.newBuilder()
                .setOrgId(orgId)
                .setPage(page)
                .setLimit(limit)
                .build();
        GetDailyTaskListResponse response = inviteFeeBackClient.getDailyTaskList(request);
        List<InviteDailyTask> tasks = response.getTasksList();
        if (CollectionUtils.isEmpty(tasks)) {
            return new ArrayList<>();
        }
        List<InviteDailyTaskRes> reslist = tasks.stream().map(task -> {
            InviteDailyTaskRes res = new InviteDailyTaskRes();
            BeanUtils.copyProperties(task, res);
            res.setTotalAmount(new BigDecimal(task.getTotalAmount()));
            return res;
        }).collect(Collectors.toList());
        return reslist;
    }

    @Override
    public int executeAdminGrantInviteBonus(Long orgId, long statisticsTime) {
        ExecuteAdminGrantInviteBonusRequest request = ExecuteAdminGrantInviteBonusRequest.newBuilder()
                .setOrgId(orgId)
                .setStatisticsTime(statisticsTime)
                .build();
        // 触发执行邀请返佣发放 返回ret = 0 成功 -1：活动不存在 -2：已有任务在执行
        ExecuteAdminGrantInviteBonusResponse response = inviteFeeBackClient.executeAdminGrantInviteBonus(request);
        log.info("orgId:{} statisticsTime:{} response:{}", orgId, statisticsTime, response);
        return response.getRet();
    }

    @Override
    public List<InviteCommonSettingDTO> getInviteCommonSetting(Long orgId) {
        GetInviteCommonSettingRequest request = GetInviteCommonSettingRequest.newBuilder()
                .setOrgId(orgId)
                .build();
        GetInviteCommonSettingResponse response = inviteFeeBackClient.getInviteCommonSetting(request);
        List<InviteCommonSetting> settings = response.getSettingsList();
        Map<String, List<InviteCommonSetting>> settingMap = settings.stream()
                .collect(Collectors.groupingBy(InviteCommonSetting::getLanguage));

        List<InviteCommonSettingDTO> result = new ArrayList<>();
        for (String language : settingMap.keySet()) {
            InviteCommonSettingDTO dto = new InviteCommonSettingDTO();
            dto.setLocale(language);
            dto.setEnable(true);
            Map<String, Object> map = new HashMap<>();
            List<InviteCommonSetting> grpcSettings = settingMap.get(language);
            grpcSettings.stream().forEach(st -> {
                if (st.getKey().equals(BizConstant.INVITE_POSTER_TEMPLATE)) {
                    String v = st.getValue();
                    if (v.startsWith("[") && v.endsWith("]")) {
                        List<String> list = JsonUtil.defaultGson().fromJson(v, List.class);
                        map.put(st.getKey(), list);
                    } else {
                        map.put(st.getKey(), Lists.newArrayList(st.getValue()));
                    }
                } else {
                    map.put(st.getKey(), st.getValue());
                }
            });
            dto.setSettings(map);
            result.add(dto);
        }
        return result;
    }

    private List<InviteCommonSetting> getInviteCommonSettingResponse(Long orgId) {
        GetInviteCommonSettingRequest request = GetInviteCommonSettingRequest.newBuilder()
                .setOrgId(orgId)
                .build();
        GetInviteCommonSettingResponse response = inviteFeeBackClient.getInviteCommonSetting(request);
        List<InviteCommonSetting> settings = response.getSettingsList();
        return settings;
    }

    @Override
    public boolean editInviteCommonSetting(Long orgId, List<InviteCommonSettingPO> polist) {
        List<InviteCommonSetting> settings = getInviteCommonSettingResponse(orgId);
        final Map<String, String> originMap = new HashMap<>();
        settings.stream().forEach(s -> originMap.put(s.getLanguage() + s.getKey(), s.getValue()));
        List<String> keys = Lists.newArrayList("invite_share_wx_title",  "invite_share_wx_content",
                "invite_activity_rule_url", "invite_poster_template",  "invite_broker_logo_url", "invite_title_pic_app",
                "app_download_page_text", "invite_title_pic_pc", "bhop_download_android_url", "bhop_download_ios_url");
        for (InviteCommonSettingPO po : polist) {
            Map<String, Object> item = po.getSettings();
            for (String key : item.keySet()) {
                if (!keys.contains(key)) {
                    log.error("key:{} not not in key list", key);
                    throw new BizException(ErrorCode.ERR_REQUEST_PARAMETER);
                }
                boolean changed = (originMap.isEmpty() || !originMap.containsKey(po.getLocale() + key)) || !originMap.get(po.getLocale() + key).equals(item.get(key));
                if (changed) {
                    Object valueObj = item.get(key);
                    String value = valueObj instanceof String ? valueObj.toString() : JsonUtil.defaultGson().toJson(valueObj);
                    UpdateInviteCommonSettingRequest request = UpdateInviteCommonSettingRequest.newBuilder()
                            .setSetting(InviteCommonSetting.newBuilder()
                                    .setOrgId(orgId)
                                    .setLanguage(po.getLocale())
                                    .setKey(key)
                                    .setValue(value)
                                    .setDesc(System.currentTimeMillis() + "")
                                    .build())
                            .build();
                    UpdateInviteCommonSettingResponse response = inviteFeeBackClient.updateInviteCommonSetting(request);
                    log.info("request:{}, response:{}", request, response);
                }
            }
        }
        return true;
    }

    @Override
    public boolean createUserBonusRecord(Long orgId, Long statisticsTime) {
        GenerateAdminInviteBonusRecordRequest generateAdminInviteBonusRecordRequest = GenerateAdminInviteBonusRecordRequest
                .newBuilder()
                .setOrgId(orgId)
                .setStatisticsTime(statisticsTime)
                .build();
        GenerateAdminInviteBonusRecordResponse generateAdminInviteBonusRecordResponse
                = inviteFeeBackClient.generateAdminInviteBonusRecord(generateAdminInviteBonusRecordRequest);
        return generateAdminInviteBonusRecordResponse.getRet() == 1;
    }
}
