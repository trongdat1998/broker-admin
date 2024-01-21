package io.bhex.broker.admin.controller.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * set flow forbidden DTO
 *
 * @author songxd
 * @date 2021-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FlowSetForbiddenDTO extends BaseDTO{
    private Integer flowConfigId;
    private Integer forbiddenStatus;
}
