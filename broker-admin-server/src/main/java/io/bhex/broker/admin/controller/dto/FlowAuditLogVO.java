package io.bhex.broker.admin.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FlowAuditLogVO {
    private Integer id;
    private Long orgId;
    private Long approver;
    private String approverName;
    private Integer level;
    private Integer approvalStatus;
    private String approvalNote;
    private Long auditDte;
}
