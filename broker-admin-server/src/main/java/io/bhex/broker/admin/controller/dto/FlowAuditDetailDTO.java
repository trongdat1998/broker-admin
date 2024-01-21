package io.bhex.broker.admin.controller.dto;

import lombok.Data;

/**
 * FlowAuditDetailDTO
 *
 * @author songxd
 * @date 2021-01-20
 */
@Data
public class FlowAuditDetailDTO {
    private Long orgId;
    private Long bizId;
    private Integer bizType;
}
