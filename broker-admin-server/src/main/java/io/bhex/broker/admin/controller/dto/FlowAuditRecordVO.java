package io.bhex.broker.admin.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FlowAuditRecordVO {
    private Integer id;
    private Long orgId;
    private Integer flowConfigId;
    private Long bizId;
    private Integer bizType;
    private String bizTitle;
    private Long applicant;
    private String applicantName;
    private Long applyDate;
    private Integer currentLevel;
    private Long approver;
    private String approverName;
    private Integer status;
    private Boolean auditRight;
    private Long createdAt;
}
