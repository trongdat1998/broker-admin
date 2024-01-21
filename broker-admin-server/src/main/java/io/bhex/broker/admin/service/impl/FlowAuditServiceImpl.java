package io.bhex.broker.admin.service.impl;

import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.bhex.base.admin.common.AccountType;
import io.bhex.base.admin.common.AdminUserReply;
import io.bhex.bhop.common.grpc.client.AdminUserClient;
import io.bhex.bhop.common.util.ResultModel;
import io.bhex.broker.admin.controller.dto.*;
import io.bhex.broker.admin.grpc.client.FlowAuditClient;
import io.bhex.broker.admin.util.AdminUtils;
import io.bhex.broker.grpc.admin.AirdropInfo;
import io.bhex.broker.grpc.auditflow.*;
import io.bhex.broker.admin.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * flow audit service impl
 *
 * @author songxd
 * @date 2021-01-15
 */
@Slf4j
@Service
public class FlowAuditServiceImpl implements FlowAuditService {
    @Resource
    private FlowAuditClient flowAuditClient;

    @Autowired
    private AirdropService airdropService;

    @Autowired
    private AdminUserClient adminUserClient;

    private final static Integer DEFAULT_PAGE_SIZE = 20;
    private final static Integer DEFAULT_MAX_PAGE_SIZE = 100;
    private final static String DEFAULT_LANGUAGE = "en_US";

    private final Cache<Long, String> ADMIN_USER_NAME_CACHE = CacheBuilder
            .newBuilder()
            .expireAfterWrite(10L, TimeUnit.MINUTES)
            .build();

    private final Cache<Long, AdminUserReply> ADMIN_USER_CACHE = CacheBuilder
            .newBuilder()
            .expireAfterWrite(10L, TimeUnit.MINUTES)
            .build();

    @Override
    public ResultModel saveFlowConfig(FlowConfigDTO dto, AdminUserReply userReply, String language) {
        // param check
        if (dto == null || CollectionUtils.isEmpty(dto.getNodes())) {
            log.error("flow save flow config is null or nodes count is zero:org-{},uid-{}", userReply.getOrgId(), userReply.getId());
            return ResultModel.error("request.parameter.error");
        }

        if(dto.getId() == 0 && dto.getStatus() == 0){
            log.error("flow add flow config but status is 0:orgId:{},uid:{}", userReply.getOrgId(), userReply.getId());
            return ResultModel.error("request.parameter.error");
        }

        // set flow node list
        List<FlowNode> listNode = new ArrayList<>();

        int index = 1;
        for (FlowNodeDTO node : dto.getNodes()) {
            AdminUserReply adminUserReply = getAdminUser(userReply.getOrgId(), node.getApprover());

            if (adminUserReply == null || adminUserReply.getId() == 0L) {
                log.error("flow audit node approver error:flowId{}-approverId{}", dto.getId(), node.getApprover());
                return ResultModel.error("request.parameter.error");
            }
            listNode.add(FlowNode.newBuilder()
                    .setLevel(index++)
                    .setApprover(node.getApprover())
                    .setApproverName(adminUserReply.getRealName())
                    .setAllowNotify(node.getAllowNotify())
                    .setNotifyMode(node.getNotifyMode())
                    .setAllowPass(0)
                    .setApproverEmail(adminUserReply.getEmail())
                    .setApproverPhone(adminUserReply.getTelephone())
                    .setLanguage(language)
                    .build());
        }

        // set flow config
        AdminSaveFlowConfigRequest request = AdminSaveFlowConfigRequest.newBuilder()
                .setId(dto.getId())
                .setOrgId(userReply.getOrgId())
                .setUserId(userReply.getId())
                .setUserName(userReply.getRealName())
                .setFlowName("")
                .setBizType(dto.getBizType())
                .setLevelCount(dto.getLevelCount())
                .setAllowModify(dto.getAllowModify())
                .setAllowForbidden(dto.getAllowForbidden())
                .setStatus(dto.getStatus())
                .addAllNodes(listNode)
                .build();
        AdminSaveFlowConfigReply reply = flowAuditClient.saveFlowConfig(request);
        if (reply.getCode() == 0) {
            return ResultModel.ok(reply.getFlowConfigId());
        } else {
            switch (reply.getCode()) {
                case 1:
                    return ResultModel.error("request.parameter.error");
                case 2:
                    return ResultModel.error("flow.audit.config.biz.type.exists");
                case 3:
                    return ResultModel.error("flow.audit.config.not.allow.modify");
                case 4:
                    return ResultModel.error("flow.audit.config.not.allow.set.forbidden");
                case 5:
                    return ResultModel.error("flow.audit.unprocessed.records");
                default:
                    return ResultModel.error("flow.audit.unknown");
            }
        }
    }

    @Override
    public ResultModel setFlowForbidden(FlowSetForbiddenDTO dto, AdminUserReply userReply) {
        if (dto == null) {
            log.error("flow save set forbidden error:org-{},uid-{}", userReply.getOrgId(), userReply.getId());
            return ResultModel.error("request.parameter.error");
        }
        AdminSetFlowForbiddenRequest request = AdminSetFlowForbiddenRequest.newBuilder()
                .setFlowConfigId(dto.getFlowConfigId())
                .setOrgId(userReply.getOrgId())
                .setUserId(userReply.getId())
                .setForbiddenStatus(dto.getForbiddenStatus())
                .build();
        AdminSetFlowForbiddenReply reply = flowAuditClient.setFlowForbidden(request);
        if (reply.getCode() == 0) {
            return ResultModel.ok();
        } else {
            switch (reply.getCode()) {
                case 1:
                    return ResultModel.error("request.parameter.error");
                case 2:
                    return ResultModel.error("flow.audit.config.not.allow.set.forbidden");
                default:
                    return ResultModel.error("flow.audit.unknown");
            }
        }
    }

    @Override
    public ResultModel getFlowConfigList(FlowConfigGetListDTO dto, String language) {
        if (dto.getLimit() == null || dto.getLimit() > DEFAULT_MAX_PAGE_SIZE) {
            dto.setLimit(DEFAULT_PAGE_SIZE);
        }
        if (dto.getBizType() == null) {
            dto.setBizType(0);
        }
        if (dto.getStartId() == null) {
            dto.setStartId(0);
        }
        AdminGetFlowConfigListRequest request = AdminGetFlowConfigListRequest.newBuilder()
                .setOrgId(dto.getOrgId())
                .setBizType(dto.getBizType())
                .setStartFlowConfigId(dto.getStartId())
                .setLimit(dto.getLimit())
                .setLanguage(language)
                .build();
        AdminGetFlowConfigListReply reply = flowAuditClient.getFlowConfigList(request);
        if (reply == null || CollectionUtils.isEmpty(reply.getFlowsList())) {
            return ResultModel.ok(new ArrayList<>());
        }
        return ResultModel.ok(reply.getFlowsList().stream().map(this::getFlowConfigVO).collect(Collectors.toList()));
    }

    @Override
    public ResultModel getFlowBizTypeList(Long orgId, String language) {
        AdminGetFlowBizTypeListRequest request = AdminGetFlowBizTypeListRequest.newBuilder()
                .setOrgId(orgId)
                .setLanguage(language)
                .build();
        AdminGetFlowBizTypeListReply reply = flowAuditClient.getFlowBizTypeList(request);
        if (reply == null || CollectionUtils.isEmpty(reply.getBizTypesList())) {
            if (DEFAULT_LANGUAGE.equalsIgnoreCase(language)) {
                return ResultModel.ok(new ArrayList<>());
            } else {
                request = AdminGetFlowBizTypeListRequest.newBuilder()
                        .setOrgId(orgId)
                        .setLanguage(DEFAULT_LANGUAGE)
                        .build();
                reply = flowAuditClient.getFlowBizTypeList(request);
                if (reply == null || CollectionUtils.isEmpty(reply.getBizTypesList())) {
                    return ResultModel.ok(new ArrayList<>());
                } else {
                    return ResultModel.ok(reply.getBizTypesList());
                }
            }
        }
        return ResultModel.ok(reply.getBizTypesList().stream().map(this::getFlowDictVO).collect(Collectors.toList()));
    }

    @Override
    public ResultModel getAuditRecordList(FlowGetRecordsDTO dto, AdminUserReply userReply) {
        if (dto.getLimit() == null || dto.getLimit() > DEFAULT_MAX_PAGE_SIZE) {
            dto.setLimit(DEFAULT_PAGE_SIZE);
        }
        if (dto.getBizType() == null) {
            dto.setBizType(0);
        }
        if (dto.getStartId() == null) {
            dto.setStartId(0);
        }
        AdminFlowGetRecordListRequest request = AdminFlowGetRecordListRequest.newBuilder()
                .setOrgId(userReply.getOrgId())
                .setUserId(userReply.getAccountType() == AccountType.ROOT_ACCOUNT ? 0L : userReply.getId())
                .setBizType(dto.getBizType())
                .setStartRecordId(dto.getStartId())
                .setLimit(dto.getLimit())
                .build();
        AdminFlowGetAuditRecordListReply reply = flowAuditClient.getAuditRecordList(request);
        if (reply == null || CollectionUtils.isEmpty(reply.getRecordsList())) {
            return ResultModel.ok(new ArrayList<>());
        }
        return ResultModel.ok(reply.getRecordsList().stream().map(record -> getFlowAuditRecordVO(record, userReply)).collect(Collectors.toList()));
    }

    @Override
    public ResultModel getApprovedRecordList(FlowGetRecordsDTO dto, AdminUserReply userReply) {
        if (dto.getLimit() == null || dto.getLimit() > DEFAULT_MAX_PAGE_SIZE) {
            dto.setLimit(DEFAULT_PAGE_SIZE);
        }
        if (dto.getBizType() == null) {
            dto.setBizType(0);
        }
        if (dto.getStartId() == null) {
            dto.setStartId(0);
        }
        AdminFlowGetRecordListRequest request = AdminFlowGetRecordListRequest.newBuilder()
                .setOrgId(userReply.getOrgId())
                .setUserId(userReply.getAccountType() == AccountType.ROOT_ACCOUNT ? 0L : userReply.getId())
                .setBizType(dto.getBizType())
                .setStartRecordId(dto.getStartId())
                .setLimit(dto.getLimit())
                .build();
        AdminFlowGetApprovedRecordListReply reply = flowAuditClient.getApprovedRecordList(request);
        if (reply == null || CollectionUtils.isEmpty(reply.getRecordsList())) {
            return ResultModel.ok(new ArrayList<>());
        }
        return ResultModel.ok(reply.getRecordsList().stream().map(this::getFlowApprovedRecordVO).collect(Collectors.toList()));
    }

    @Override
    public ResultModel getAuditLogList(FlowGetAuditLogDTO dto) {
        if (dto.getRecordId() == null || dto.getFlowConfigId() == null) {
            return ResultModel.error("request.parameter.error");
        }
        AdminFlowGetAuditLogListRequest request = AdminFlowGetAuditLogListRequest.newBuilder()
                .setOrgId(dto.getOrgId())
                .setFlowConfigId(dto.getFlowConfigId())
                .setRecordId(dto.getRecordId())
                .build();
        AdminFlowGetAuditLogListReply reply = flowAuditClient.getAuditLogList(request);
        if (reply == null || CollectionUtils.isEmpty(reply.getAuditLogsList())) {
            return ResultModel.ok(new ArrayList<>());
        }
        return ResultModel.ok(reply.getAuditLogsList().stream().map(this::getFlowAuditLogVO).collect(Collectors.toList()));
    }

    @Override
    public ResultModel auditProcess(FlowAuditDTO dto, AdminUserReply userReply) {
        if (dto.getRecordId() == null || dto.getAuditStatus() == null) {
            return ResultModel.error("request.parameter.error");
        }
        AdminFlowAuditRequest request = AdminFlowAuditRequest.newBuilder()
                .setOrgId(userReply.getOrgId())
                .setUserId(userReply.getId())
                .setUserName(userReply.getRealName())
                .setRecordId(dto.getRecordId())
                .setAuditStatus(dto.getAuditStatus())
                .setAuditNote("")
                .build();
        AdminFlowAuditReply reply = flowAuditClient.auditProcess(request);
        if (reply.getCode() == 0) {
            return ResultModel.ok();
        } else {
            if (reply.getCode() == 1) {
                return ResultModel.error("flow.audit.approver.error");
            }
            return ResultModel.error("flow.audit.unknown");
        }
    }

    @Override
    public ResultModel getFlowConfigDetail(FlowGetDetailDTO dto) {
        if (dto.getId() == null) {
            return ResultModel.error("request.parameter.error");
        }
        AdminGetFlowConfigDetailRequest request = AdminGetFlowConfigDetailRequest.newBuilder()
                .setFlowConfigId(dto.getId())
                .setOrgId(dto.getOrgId())
                .build();
        AdminGetFlowConfigDetailReply reply = flowAuditClient.getFlowConfigDetail(request);
        if (reply.getCode() == 0) {
            return ResultModel.ok(getFlowConfigInfoVO(reply.getConfig()));
        } else {
            switch (reply.getCode()) {
                case 1:
                    return ResultModel.error("request.parameter.error");
                default:
                    return ResultModel.error("flow.audit.unknown");
            }
        }
    }

    @Override
    public ResultModel getAuditDetail(FlowAuditDetailDTO dto) {
        if (dto.getBizType() == null || dto.getBizId() == null) {
            return ResultModel.error("request.parameter.error");
        }
        return getAuditDetailByBizType(dto);
    }

    /**
     * get audit detail by biz type
     *
     * @param dto
     * @return
     */
    private ResultModel getAuditDetailByBizType(FlowAuditDetailDTO dto) {
        // air drop
        if (dto.getBizType() == 1 || dto.getBizType() == 2) {
            AirdropInfo airdropInfo = airdropService.getAirdropInfo(dto.getBizId(), dto.getOrgId());
            AirdropDTO airDto = new AirdropDTO();
            BeanUtils.copyProperties(airdropInfo, airDto);
            airDto.setAirdropTokenNum(new BigDecimal(airdropInfo.getAirdropTokenNum()));
            airDto.setHaveTokenNum(new BigDecimal(airdropInfo.getHaveTokenNum()));
            airDto.setUserAccountIds(airdropInfo.getUserIds());
            airDto.setTmplModel(StringUtils.isEmpty(airdropInfo.getTmplUrl()) ? 0 : 1);
            return ResultModel.ok(airDto);
        }
        return ResultModel.ok();
    }

    /**
     * get dict value vo
     *
     * @param dictValue
     * @return
     */
    private FlowDictVO getFlowDictVO(AdminGetFlowBizTypeListReply.DictValue dictValue) {
        return FlowDictVO.builder().dictText(dictValue.getDictText()).dictValue(dictValue.getDictValue()).build();
    }

    /**
     * get flow config vo
     *
     * @param flowConfig
     * @return
     */
    private FlowConfigVO getFlowConfigVO(FlowConfig flowConfig) {
        return FlowConfigVO.builder()
                .id(flowConfig.getId())
                .orgId(flowConfig.getOrgId())
                .userId(flowConfig.getUserId())
                .userName(Strings.isNullOrEmpty(flowConfig.getUserName()) ? getAdminUserName(flowConfig.getOrgId(), flowConfig.getUserId()) : flowConfig.getUserName())
                .flowName(flowConfig.getFlowName())
                .bizName(flowConfig.getBizName())
                .bizType(flowConfig.getBizType())
                .levelCount(flowConfig.getLevelCount())
                .allowModify(flowConfig.getAllowModify())
                .allowForbidden(flowConfig.getAllowForbidden())
                .createdAt(flowConfig.getCreatedAt())
                .status(flowConfig.getStatus())
                .build();
    }

    /**
     * get flow audit record vo
     *
     * @param record
     * @return
     */
    private FlowAuditRecordVO getFlowAuditRecordVO(FlowBizRecord record, AdminUserReply userReply) {
        return FlowAuditRecordVO.builder()
                .id(record.getId())
                .orgId(record.getOrgId())
                .flowConfigId(record.getFlowConfigId())
                .bizId(record.getBizId())
                .bizType(record.getBizType())
                .bizTitle(record.getBizTitle())
                .applicant(record.getApplicant())
                .applicantName(Strings.isNullOrEmpty(record.getApplicantName()) ? getAdminUserName(record.getOrgId(), record.getApplicant()) : record.getApplicantName())
                .applyDate(record.getApplyDate())
                .currentLevel(record.getCurrentLevel())
                .approver(record.getApprover())
                .approverName(Strings.isNullOrEmpty(record.getApproverName()) ? getAdminUserName(record.getOrgId(), record.getApprover()) : record.getApproverName())
                .status(record.getStatus())
                .createdAt(record.getCreatedAt())
                .auditRight(userReply.getId() == record.getApprover())
                .build();
    }

    /**
     * get flow approved record vo
     *
     * @param record
     * @return
     */
    private FlowApprovedRecordVO getFlowApprovedRecordVO(FlowApprovedRecord record) {
        return FlowApprovedRecordVO.builder()
                .recordId(record.getRecordId())
                .orgId(record.getOrgId())
                .flowConfigId(record.getFlowConfigId())
                .bizId(record.getBizId())
                .bizType(record.getBizType())
                .bizTitle(record.getBizTitle())
                .applicant(record.getApplicant())
                .applicantName(Strings.isNullOrEmpty(record.getApplicantName()) ? getAdminUserName(record.getOrgId(), record.getApplicant()) : record.getApplicantName())
                .applyDate(record.getApplyDate())
                .auditDate(record.getAuditDate())
                .level(record.getLevel())
                .approver(record.getApprover())
                .approverName(Strings.isNullOrEmpty(record.getApproverName()) ? getAdminUserName(record.getOrgId(), record.getApprover()) : record.getApproverName())
                .approvalStatus(record.getApprovalStatus())
                .approvalNote(record.getApprovalNote())
                .build();
    }

    /**
     * get flow audit log vo
     *
     * @param auditLog
     * @return
     */
    private FlowAuditLogVO getFlowAuditLogVO(FlowAuditLog auditLog) {
        return FlowAuditLogVO.builder()
                .id(auditLog.getId())
                .orgId(auditLog.getOrgId())
                .approver(auditLog.getApprover())
                .approverName(Strings.isNullOrEmpty(auditLog.getApproverName()) ? getAdminUserName(auditLog.getOrgId(), auditLog.getApprover()) : auditLog.getApproverName())
                .level(auditLog.getLevel())
                .approvalStatus(auditLog.getApprovalStatus())
                .approvalNote(auditLog.getApprovalNote())
                .auditDte(auditLog.getAuditDate())
                .build();
    }

    /**
     * get flow config detail info vo
     *
     * @param flowConfigInfo
     * @return
     */
    private FlowConfigInfoVO getFlowConfigInfoVO(AdminGetFlowConfigDetailReply.FlowConfigInfo flowConfigInfo) {
        List<FlowNodeVO> listNodes = new ArrayList<>();
        flowConfigInfo.getNodesList().forEach(node -> {
            listNodes.add(FlowNodeVO.builder()
                    .level(node.getLevel())
                    .approver(node.getApprover())
                    .approverName(
                            Strings.isNullOrEmpty(node.getApproverName()) ? getAdminUserName(flowConfigInfo.getOrgId(), node.getApprover()) : node.getApproverName())
                    .allowNotify(node.getAllowNotify())
                    .allowPass(node.getAllowPass())
                    .notifyMode(node.getNotifyMode())
                    .build());
        });
        return FlowConfigInfoVO.builder()
                .id(flowConfigInfo.getId())
                .orgId(flowConfigInfo.getOrgId())
                .flowName(flowConfigInfo.getFlowName())
                .bizType(flowConfigInfo.getBizType())
                .levelCount(flowConfigInfo.getLevelCount())
                .allowModify(flowConfigInfo.getAllowModify())
                .allowForbidden(flowConfigInfo.getAllowForbidden())
                .status(flowConfigInfo.getStatus())
                .nodes(listNodes)
                .build();
    }

    /**
     * get admin user name
     *
     * @param orgId
     * @param userId
     * @return
     */
    private String getAdminUserName(Long orgId, Long userId) {
        String name;
        try {
            name = ADMIN_USER_NAME_CACHE.get(userId, () -> {
                AdminUserReply reply = adminUserClient.getAdminUserById(userId);
                if(Strings.isNullOrEmpty(reply.getRealName())){
                    return AdminUtils.emailEncrypt(reply.getEmail());
                } else{
                    return reply.getRealName();
                }
            });
        } catch (Exception e) {
            name = "";
        }
        return name;
    }

    /**
     * get admin user name
     *
     * @param orgId
     * @param userId
     * @return
     */
    private AdminUserReply getAdminUser(Long orgId, Long userId) {
        AdminUserReply reply;
        try {
            reply = ADMIN_USER_CACHE.get(userId, () ->
                    adminUserClient.getAdminUserById(userId));
        } catch (Exception e) {
            reply = AdminUserReply.newBuilder().build();
        }
        return reply;
    }
}
