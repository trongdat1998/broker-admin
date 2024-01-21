package io.bhex.broker.admin.controller.dto;

import lombok.Data;

/**
 * get flow config list
 *
 * @author songxd
 * @date 2021-01-16
 */
@Data
public class FlowConfigGetListDTO {
    private Long orgId;
    private Integer bizType;
    private Integer startId;
    private Integer limit;
}
