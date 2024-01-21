package io.bhex.broker.admin.controller.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * flow record audit
 *
 * @author songxd
 * @date 2021-01-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FlowAuditDTO extends BaseDTO{
    private Integer recordId;
    private Integer auditStatus;
}
