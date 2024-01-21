package io.bhex.broker.admin.grpc.client.impl;

import io.bhex.broker.admin.grpc.client.FlowAuditClient;
import io.bhex.broker.admin.grpc.client.config.GrpcClientConfig;
import io.bhex.broker.grpc.auditflow.AdminAuditFlowServiceGrpc;
import io.bhex.broker.grpc.auditflow.AdminFlowAuditReply;
import io.bhex.broker.grpc.auditflow.AdminFlowAuditRequest;
import io.bhex.broker.grpc.auditflow.AdminFlowGetApprovedRecordListReply;
import io.bhex.broker.grpc.auditflow.AdminFlowGetAuditLogListReply;
import io.bhex.broker.grpc.auditflow.AdminFlowGetAuditLogListRequest;
import io.bhex.broker.grpc.auditflow.AdminFlowGetAuditRecordListReply;
import io.bhex.broker.grpc.auditflow.AdminFlowGetRecordListRequest;
import io.bhex.broker.grpc.auditflow.AdminGetFlowBizTypeListReply;
import io.bhex.broker.grpc.auditflow.AdminGetFlowBizTypeListRequest;
import io.bhex.broker.grpc.auditflow.AdminGetFlowConfigDetailReply;
import io.bhex.broker.grpc.auditflow.AdminGetFlowConfigDetailRequest;
import io.bhex.broker.grpc.auditflow.AdminGetFlowConfigListReply;
import io.bhex.broker.grpc.auditflow.AdminGetFlowConfigListRequest;
import io.bhex.broker.grpc.auditflow.AdminSaveFlowConfigReply;
import io.bhex.broker.grpc.auditflow.AdminSaveFlowConfigRequest;
import io.bhex.broker.grpc.auditflow.AdminSetFlowForbiddenReply;
import io.bhex.broker.grpc.auditflow.AdminSetFlowForbiddenRequest;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * flow audit call grpc
 *
 * @author songxd
 * @date 2021-01-15
 */
@Service
public class FlowAuditClientImpl implements FlowAuditClient {

    @Resource
    GrpcClientConfig grpcConfig;

    private AdminAuditFlowServiceGrpc.AdminAuditFlowServiceBlockingStub getStub() {
        return grpcConfig.adminAuditFlowServiceBlockingStub(GrpcClientConfig.BROKER_SERVER_CHANNEL_NAME);
    }

    @Override
    public AdminSaveFlowConfigReply saveFlowConfig(AdminSaveFlowConfigRequest request) {
        return getStub().saveFlowConfig(request);
    }

    @Override
    public AdminSetFlowForbiddenReply setFlowForbidden(AdminSetFlowForbiddenRequest request) {
        return getStub().setFlowForbidden(request);
    }

    @Override
    public AdminGetFlowConfigListReply getFlowConfigList(AdminGetFlowConfigListRequest request) {
        return getStub().getFlowConfigList(request);
    }

    @Override
    public AdminGetFlowBizTypeListReply getFlowBizTypeList(AdminGetFlowBizTypeListRequest request) {
        return getStub().getFlowBizTypeList(request);
    }

    @Override
    public AdminFlowGetAuditRecordListReply getAuditRecordList(AdminFlowGetRecordListRequest request) {
        return getStub().getAuditRecordList(request);
    }

    @Override
    public AdminFlowGetApprovedRecordListReply getApprovedRecordList(AdminFlowGetRecordListRequest request) {
        return getStub().getApprovedRecordList(request);
    }

    @Override
    public AdminFlowGetAuditLogListReply getAuditLogList(AdminFlowGetAuditLogListRequest request) {
        return getStub().getAuditLogList(request);
    }

    @Override
    public AdminFlowAuditReply auditProcess(AdminFlowAuditRequest request) {
        return getStub().auditProcess(request);
    }

    @Override
    public AdminGetFlowConfigDetailReply getFlowConfigDetail(AdminGetFlowConfigDetailRequest request){
        return getStub().getFlowConfigDetail(request);
    }
}
