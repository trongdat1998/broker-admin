package io.bhex.broker.admin.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FlowApprovedRecordVO {
    private Integer recordId;
    private Long orgId;
    private Integer flowConfigId;
    private Long bizId;
    private Integer bizType;
    private String bizTitle;
    private Long applicant;
    private String applicantName;
    private Long applyDate;
    private Long auditDate;
    private Integer level;
    private Long approver;
    private String approverName;
    private Integer approvalStatus;
    private String approvalNote;
}
