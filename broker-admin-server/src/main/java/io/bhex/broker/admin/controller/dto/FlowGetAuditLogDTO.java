package io.bhex.broker.admin.controller.dto;

import lombok.Data;

/**
 * get flow audit logs
 *
 * @author songxd
 * @date 2021-01-16
 */
@Data
public class FlowGetAuditLogDTO {
    private Long orgId;
    private Integer flowConfigId;
    private Integer recordId;
}
