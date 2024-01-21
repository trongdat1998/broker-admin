package io.bhex.broker.admin.service;

import io.bhex.broker.admin.controller.dto.PushTaskDTO;
import io.bhex.broker.admin.controller.dto.PushTaskSimpleDTO;
import io.bhex.broker.admin.controller.param.DeletePushTaskPo;
import io.bhex.broker.admin.controller.param.PushTaskPO;
import io.bhex.broker.admin.controller.param.SendTestTaskPO;
import io.bhex.broker.grpc.app_push.*;

import java.util.List;

public interface PushAdminService {

    SendAdminTestPushResponse sendAdminTestPush(SendTestTaskPO po, Long orgId);

    AddAdminPushTaskResponse addAdminPushTask(PushTaskPO po, Long orgId);

    EditAdminPushTaskResponse editAdminPushTask(PushTaskPO po, Long orgId);

    PushTaskDTO queryAdminPushTask(Long taskId, Long orgId);

    DeleteAdminPushTaskResponse deleteAdminPushTask(Long taskId, Long orgId);

    CancelAdminPushTaskResponse cancelAdminPushTask(Long taskId, Long orgId);

    QueryAdminPushTaskSimplesResponse queryAdminPushTaskSimples(Long startTime, Long endTime, Long startId, Long endId, Integer limit, Long orgId);

}
