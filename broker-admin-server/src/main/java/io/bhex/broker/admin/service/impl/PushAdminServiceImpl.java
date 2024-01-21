package io.bhex.broker.admin.service.impl;

import com.google.common.base.Strings;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.exception.BizException;
import io.bhex.bhop.common.exception.ErrorCode;
import io.bhex.broker.admin.controller.dto.PushFilterConditionDTO;
import io.bhex.broker.admin.controller.dto.PushTaskDTO;
import io.bhex.broker.admin.controller.dto.PushTaskLocaleDetailDTO;
import io.bhex.broker.admin.controller.dto.PushTaskSimpleDTO;
import io.bhex.broker.admin.controller.param.DeletePushTaskPo;
import io.bhex.broker.admin.controller.param.OpenPushBussinessPO;
import io.bhex.broker.admin.controller.param.PushTaskPO;
import io.bhex.broker.admin.controller.param.SendTestTaskPO;
import io.bhex.broker.admin.grpc.client.PushAdminClient;
import io.bhex.broker.admin.service.PushAdminService;
import io.bhex.broker.grpc.app_push.*;
import io.bhex.broker.grpc.common.Header;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author JinYuYuan
 * @description
 * @date 2020-08-01 15:32
 */
@Slf4j
@Service
public class PushAdminServiceImpl implements PushAdminService {
    @Resource
    PushAdminClient pushAdminClient;

    @Override
    public SendAdminTestPushResponse sendAdminTestPush(SendTestTaskPO po, Long orgId) {
        List<Long> userIds = Arrays.stream(po.getUserIds().split(","))
                .filter(id -> !Strings.isNullOrEmpty(id))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        if (userIds.isEmpty()) {
            log.warn("sendAdminTestPush userId is null");
            throw new BizException(ErrorCode.ERROR, "userId is null");
        }
        SendAdminTestPushRequest request = SendAdminTestPushRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setPushContent(po.getPushContent())
                .setPushSummary(po.getPushSummary())
                .setPushTitle(po.getPushTitle())
                .setPushUrl(po.getPushUrl())
                .setUrlType(po.getUrlType())
                .addAllUserId(userIds)
                .build();
        return pushAdminClient.sendAdminTestPush(request);
    }


    @Override
    public AddAdminPushTaskResponse addAdminPushTask(PushTaskPO po, Long orgId) {
        List<Long> userIds = null;
        if (po.getRangeType() == 9) {
            userIds = Arrays.stream(po.getUserIds().split(","))
                    .filter(id -> !Strings.isNullOrEmpty(id))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            if (userIds.isEmpty()) {
                log.warn("sendAdminTestPush userId is null");
                throw new BizException(ErrorCode.ERROR, "userId is null");
            }
        }
        List<FilterCondition> filterConditions;
        if (po.getFilterConditions() == null || po.getFilterConditions().size() == 0) {
            filterConditions = new ArrayList<>();
        } else {
            filterConditions = po.getFilterConditions().stream()
                    .map(filter -> FilterCondition.newBuilder()
                            .setType(filter.getType())
                            .setMaxValue(filter.getMaxValue())
                            .setMinValue(filter.getMinValue())
                            .setTokenId(filter.getTokenId())
                            .putAllExtraInfo(filter.getExtraInfo()).build())
                    .collect(Collectors.toList());
        }
        List<PushTaskLocale> taskLocales = po.getPushTaskLocaleDetails().stream()
                .map(task -> PushTaskLocale.newBuilder()
                            .setLanguage(task.getLocale())
                            .setPushSummary(task.getPushSummary())
                            .setPushContent(task.getPushContent())
                            .setPushTitle(task.getPushTitle())
                            .setPushUrl(task.getPushUrl())
                            .setUrlType(task.getUrlType())
                            .build())
                .collect(Collectors.toList());
        AddAdminPushTaskRequest request = AddAdminPushTaskRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setName(po.getName())
                .setRangeType(po.getRangeType())
                .addAllUserId(CollectionUtils.isEmpty(userIds) ? new ArrayList<>() : userIds)
                .setPushCategory(po.getPushCategory())
                .setCycleType(CycleType.forNumber(po.getCycleType()))
                .setCycleDayOfWeek(po.getCycleDayOfWeek())
                .setFirstActionTime(po.getFirstActionTime())
                .setDefaultLanguage(po.getDefaultLocale())
                .addAllFilterCondition(filterConditions)
                .addAllTaskLocale(taskLocales)
                .build();
        return pushAdminClient.addAdminPushTask(request);
    }

    @Override
    public EditAdminPushTaskResponse editAdminPushTask(PushTaskPO po, Long orgId) {
        List<Long> userIds = null;
        if (po.getRangeType() == 9) {
            userIds = Arrays.stream(po.getUserIds().split(","))
                    .filter(id -> !Strings.isNullOrEmpty(id))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            if (userIds.isEmpty()) {
                log.warn("sendAdminTestPush userId is null");
                throw new BizException(ErrorCode.ERROR, "userId is null");
            }
        }
        List<FilterCondition> filterConditions;
        if (po.getFilterConditions() == null || po.getFilterConditions().size() == 0) {
            filterConditions = new ArrayList<>();
        } else {
            filterConditions = po.getFilterConditions().stream()
                    .map(filter -> FilterCondition.newBuilder()
                            .setType(filter.getType())
                            .setMaxValue(filter.getMaxValue())
                            .setMinValue(filter.getMinValue())
                            .setTokenId(filter.getTokenId())
                            .putAllExtraInfo(filter.getExtraInfo()).build())
                    .collect(Collectors.toList());
        }
        List<PushTaskLocale> taskLocales = po.getPushTaskLocaleDetails().stream()
                .map(task -> PushTaskLocale.newBuilder()
                        .setLanguage(task.getLocale())
                        .setPushSummary(task.getPushSummary())
                        .setPushContent(task.getPushContent())
                        .setPushTitle(task.getPushTitle())
                        .setPushUrl(task.getPushUrl())
                        .setUrlType(task.getUrlType())
                        .build()
                ).collect(Collectors.toList());
        EditAdminPushTaskRequest request = EditAdminPushTaskRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setTaskId(po.getTaskId())
                .setName(po.name)
                .setRangeType(po.getRangeType())
                .addAllUserId(CollectionUtils.isEmpty(userIds) ? new ArrayList<>() : userIds)
                .setPushCategory(po.getPushCategory())
                .setCycleType(CycleType.forNumber(po.getCycleType()))
                .setCycleDayOfWeek(po.cycleDayOfWeek)
                .setFirstActionTime(po.getFirstActionTime())
                .setDefaultLanguage(po.getDefaultLocale())
                .addAllFilterCondition(filterConditions)
                .addAllTaskLocale(taskLocales)
                .build();
        return pushAdminClient.editAdminPushTask(request);
    }

    @Override
    public PushTaskDTO queryAdminPushTask(Long taskId, Long orgId) {
        QueryAdminPushTaskRequest request = QueryAdminPushTaskRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setTaskId(taskId)
                .build();
        QueryAdminPushTaskResponse reply = pushAdminClient.queryAdminPushTask(request);
        if (reply.getRet() != 0) {
            log.error("queryAdminPushTask error ret:{}", reply.getRet());
            throw new BizException(ErrorCode.ERROR, "query task error");
        }
        List<PushFilterConditionDTO> filters = reply.getFilterConditionList().stream()
                .map(filter -> PushFilterConditionDTO.builder()
                        .extraInfo(filter.getExtraInfoMap())
                        .maxValue(filter.getMaxValue())
                        .minValue(filter.getMinValue())
                        .type(filter.getType())
                        .build())
                .collect(Collectors.toList());
        List<PushTaskLocaleDetailDTO> tasks = reply.getTaskLocaleList().stream()
                .map(task -> PushTaskLocaleDetailDTO.builder()
                        .locale(task.getLanguage())
                        .pushContent(task.getPushContent())
                        .pushTitle(task.getPushTitle())
                        .pushSummary(task.getPushSummary())
                        .pushUrl(task.getPushUrl())
                        .urlType(task.getUrlType())
                        .build())
                .collect(Collectors.toList());
        return PushTaskDTO.builder()
                .name(reply.getName())
                .taskId(reply.getTaskId())
                .rangeType(reply.getRangeType())
                .userIds(reply.getUserIdList())
                .pushCategory(reply.getPushCategory())
                .cycleType(reply.getCycleTypeValue())
                .cycleDayOfWeek(reply.getCycleDayOfWeek())
                .firstActionTime(reply.getFirstActionTime())
                .defaultLocale(reply.getDefaultLanguage())
                .filterConditions(filters)
                .pushTaskLocaleDetails(tasks)
                .build();
    }

    @Override
    public DeleteAdminPushTaskResponse deleteAdminPushTask(Long taskId, Long orgId) {
        DeleteAdminPushTaskRequest request = DeleteAdminPushTaskRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setTaskId(taskId)
                .build();
        return pushAdminClient.deleteAdminPushTask(request);
    }

    @Override
    public CancelAdminPushTaskResponse cancelAdminPushTask(Long taskId, Long orgId) {
        CancelAdminPushTaskRequest request = CancelAdminPushTaskRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setTaskId(taskId)
                .build();
        return pushAdminClient.cancelAdminPushTask(request);
    }

    @Override
    public QueryAdminPushTaskSimplesResponse queryAdminPushTaskSimples(Long startTime, Long endTime, Long startId, Long endId, Integer limit, Long orgId) {
        QueryAdminPushTaskSimplesRequest request = QueryAdminPushTaskSimplesRequest.newBuilder()
                .setHeader(Header.newBuilder().setOrgId(orgId).build())
                .setStartTime(startTime)
                .setEndTime(endTime)
                .setStartId(startId)
                .setEndId(endId)
                .setLimit(limit)
                .build();
        return pushAdminClient.queryAdminPushTaskSimples(request);
    }
}
