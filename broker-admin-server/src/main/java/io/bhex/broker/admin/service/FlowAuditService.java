package io.bhex.broker.admin.service;

import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.*;

/**
 * flow audit
 *
 * @author songxd
 * @date 2021-01-15
 */
public interface FlowAuditService {
    /**
     * 新增、修改 流程
     *
     * @param dto       dto
     * @param userReply current user
     * @param language  language
     * @return resultModel
     */
    ResultModel saveFlowConfig(FlowConfigDTO dto, AdminUserReply userReply, String language);

    /**
     * 设置流程禁用
     *
     * @param dto       dto
     * @param userReply current user
     * @return resultModel
     */
    ResultModel setFlowForbidden(FlowSetForbiddenDTO dto, AdminUserReply userReply);

    /**
     * 获取流程列表
     *
     * @param dto      dto
     * @param language language
     * @return resultModel
     */
    ResultModel getFlowConfigList(FlowConfigGetListDTO dto, String language);

    /**
     * 获取流程业务类型
     *
     * @param orgId    org id
     * @param language language
     * @return resultModel
     */
    ResultModel getFlowBizTypeList(Long orgId, String language);

    /**
     * 获取待审批记录
     *
     * @param dto       dto
     * @param userReply current user
     * @return resultModel
     */
    ResultModel getAuditRecordList(FlowGetRecordsDTO dto, AdminUserReply userReply);

    /**
     * 获取已审核记录
     *
     * @param dto       dto
     * @param userReply current user
     * @return resultModel
     */
    ResultModel getApprovedRecordList(FlowGetRecordsDTO dto, AdminUserReply userReply);

    /**
     * 获取审批日志
     *
     * @param dto dto
     * @return resultModel
     */
    ResultModel getAuditLogList(FlowGetAuditLogDTO dto);

    /**
     * 审批
     *
     * @param dto       dto
     * @param userReply current user
     * @return resultModel
     */
    ResultModel auditProcess(FlowAuditDTO dto, AdminUserReply userReply);

    /**
     * 获取流程配置详情
     *
     * @param dto dto
     * @return resultModel
     */
    ResultModel getFlowConfigDetail(FlowGetDetailDTO dto);

    /**
     * 获取审批详情
     *
     * @param dto dto
     * @return resultModel
     */
    ResultModel getAuditDetail(FlowAuditDetailDTO dto);
}