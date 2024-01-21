package io.bhex.broker.admin.grpc.client;

import io.bhex.broker.grpc.auditflow.*;

/**
 * flow audit grpc client
 *
 * @author songxd
 * @date 2021-01-15
 */
public interface FlowAuditClient {

    /**
     * 新增、修改 流程
     *
     * @param request
     * @return
     */
    AdminSaveFlowConfigReply saveFlowConfig(AdminSaveFlowConfigRequest request);

    /**
     * 设置流程禁用
     *
     * @param request
     * @return
     */
    AdminSetFlowForbiddenReply setFlowForbidden(AdminSetFlowForbiddenRequest request);

    /**
     * 获取流程列表
     *
     * @param request
     * @return
     */
    AdminGetFlowConfigListReply getFlowConfigList (AdminGetFlowConfigListRequest request);

    /**
     * 获取流程业务类型
     *
     * @param request
     * @return
     */
    AdminGetFlowBizTypeListReply getFlowBizTypeList(AdminGetFlowBizTypeListRequest request);

    /**
     * 获取待审批记录
     *
     * @param request
     * @return
     */
    AdminFlowGetAuditRecordListReply getAuditRecordList (AdminFlowGetRecordListRequest request);

    /**
     * 获取已审核记录
     *
     * @param request
     * @return
     */
    AdminFlowGetApprovedRecordListReply getApprovedRecordList (AdminFlowGetRecordListRequest request);

    /**
     * 获取审批日志
     *
     * @param request
     * @return
     */
    AdminFlowGetAuditLogListReply getAuditLogList(AdminFlowGetAuditLogListRequest request);

    /**
     * 审批
     *
     * @param request
     * @return
     */
    AdminFlowAuditReply auditProcess(AdminFlowAuditRequest request);

    /**
     * 获取流程配置详情
     *
     * @param request
     * @return
     */
    AdminGetFlowConfigDetailReply getFlowConfigDetail(AdminGetFlowConfigDetailRequest request);
}
