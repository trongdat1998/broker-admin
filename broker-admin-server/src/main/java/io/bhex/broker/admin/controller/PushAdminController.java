package io.bhex.broker.admin.controller;

import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.PushTaskDTO;
import io.bhex.broker.admin.controller.dto.PushTaskSimpleDTO;
import io.bhex.broker.admin.controller.param.DeletePushTaskPo;
import io.bhex.broker.admin.controller.param.PushTaskPO;
import io.bhex.broker.admin.controller.param.SendTestTaskPO;
import io.bhex.broker.admin.service.PushAdminService;
import io.bhex.broker.grpc.app_push.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author JinYuYuan
 * @description
 * @date 2020-08-01 11:20
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/push")
public class PushAdminController extends BrokerBaseController {
    @Resource
    PushAdminService pushAdminService;

    /**
     * 添加push任务
     *
     * @param taskPO
     * @return
     */
    @RequestMapping(value = "/add/task", method = RequestMethod.POST)
    public ResultModel<Void> addPushTask(@RequestBody @Valid PushTaskPO taskPO) {
        AddAdminPushTaskResponse response = pushAdminService.addAdminPushTask(taskPO, getOrgId());
        if (response.getRet() != 0) {
            log.warn("addPushTask error ret:{},msg:{}", response.getRet(), response.getMsg());
            if (StringUtils.isNoneBlank(response.getMsg())) {
                return ResultModel.error(response.getMsg());
            } else {
                return ResultModel.error("Add Push task error");
            }
        }
        return ResultModel.ok();
    }

    @RequestMapping(value = "/send/test_task", method = RequestMethod.POST)
    public ResultModel<Void> sendTestTask(@RequestBody @Valid SendTestTaskPO taskPO) {
        SendAdminTestPushResponse response = pushAdminService.sendAdminTestPush(taskPO, getOrgId());
        if (response.getRet() != 0) {
            log.warn("sendTestTask error ret:{},msg:{}", response.getRet(), response.getMsg());
            if (StringUtils.isNoneBlank(response.getMsg())) {
                return ResultModel.error(response.getMsg());
            } else {
                return ResultModel.error("send test task error");
            }
        }
        return ResultModel.ok();
    }

    @RequestMapping(value = "/update/task", method = RequestMethod.POST)
    public ResultModel<Void> updatePushTask(@RequestBody @Valid PushTaskPO taskPO) {
        if (taskPO.getTaskId() == null || taskPO.getTaskId() == 0L) {
            return ResultModel.error("taskId is null");
        }
        EditAdminPushTaskResponse response = pushAdminService.editAdminPushTask(taskPO, getOrgId());
        if (response.getRet() != 0) {
            log.error("updatePushTask error ret:{},msg:{}", response.getRet(), response.getMsg());
            if (StringUtils.isNoneBlank(response.getMsg())) {
                return ResultModel.error(response.getMsg());
            } else {
                return ResultModel.error("update task error");
            }
        }
        return ResultModel.ok();
    }

    @RequestMapping(value = "/query/task", method = RequestMethod.GET)
    public ResultModel<PushTaskDTO> queryPushTaskByTaskId(@NotNull Long taskId) {
        PushTaskDTO dto = pushAdminService.queryAdminPushTask(taskId, getOrgId());
        return ResultModel.ok(dto);
    }

    @RequestMapping(value = "/delete/task", method = RequestMethod.POST)
    public ResultModel<Void> deletePushTask(@RequestBody @Valid DeletePushTaskPo taskPo) {
        DeleteAdminPushTaskResponse response = pushAdminService.deleteAdminPushTask(taskPo.getTaskId(), getOrgId());
        if (response.getRet() != 0) {
            log.error("deletePushTask error ret:{},msg:{}", response.getRet(), response.getMsg());
            if (StringUtils.isNoneBlank(response.getMsg())) {
                return ResultModel.error(response.getMsg());
            } else {
                return ResultModel.error("delete task error");
            }
        }
        return ResultModel.ok();
    }

    @RequestMapping(value = "/cancel/task", method = RequestMethod.POST)
    public ResultModel<Void> cancelPushTask(@RequestBody @Valid DeletePushTaskPo taskPo) {
        CancelAdminPushTaskResponse response = pushAdminService.cancelAdminPushTask(taskPo.getTaskId(), getOrgId());
        if (response.getRet() != 0) {
            log.warn("cancelPushTask error ret:{},msg:{}", response.getRet(), response.getMsg());
            if (StringUtils.isNoneBlank(response.getMsg())) {
                return ResultModel.error(response.getMsg());
            } else {
                return ResultModel.error("cancel task error");
            }
        }
        return ResultModel.ok();
    }

    @RequestMapping(value = "/query/task_list", method = RequestMethod.GET)
    public ResultModel<List<PushTaskSimpleDTO>> queryPushTaskList(@RequestParam(required = false, defaultValue = "0") Long startTime,
                                                                  @RequestParam(required = false, defaultValue = "0") Long endTime,
                                                                  @RequestParam(required = false, defaultValue = "0") Long startId,
                                                                  @RequestParam(required = false, defaultValue = "0") Long endId,
                                                                  @RequestParam(required = false, defaultValue = "50") Integer limit) {
        QueryAdminPushTaskSimplesResponse response = pushAdminService.queryAdminPushTaskSimples(startTime, endTime, startId, endId, limit, getOrgId());
        if (response.getRet() != 0) {
            log.error("queryAdminPushTaskSimples error ret:{},msg:{}", response.getRet(), response.getMsg());
            if (StringUtils.isNoneBlank(response.getMsg())) {
                return ResultModel.error(response.getMsg());
            } else {
                return ResultModel.error("query task error");
            }
        }
        List<PushTaskSimpleDTO> list = response.getTaskSimpleList().stream()
                .map(task -> PushTaskSimpleDTO.builder()
                        .taskId(task.getTaskId())
                        .taskRound(task.getTaskRound())
                        .name(task.getName())
                        .cycleType(task.getCycleType())
                        .cycleDayOfWeek(task.getCycleDayOfWeek())
                        .firstActionTime(task.getFirstActionTime())
                        .actionTime(task.getActionTime())
                        .status(task.getStatus())
                        .expireTime(task.getExpireTime())
                        .executeTime(task.getExecuteTime())
                        .sendCount(task.getSendCount())
                        .deliveryCount(task.getDeliveryCount())
                        .clickCount(task.getClickCount())
                        .remark(task.getRemark())
                        .created(task.getCreated())
                        .updated(task.getUpdated())
                        .build())
                .collect(Collectors.toList());
        return ResultModel.ok(list);
    }
}
