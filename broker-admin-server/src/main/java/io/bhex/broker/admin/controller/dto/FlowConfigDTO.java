package io.bhex.broker.admin.controller.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Flow Config DTO
 *
 * @author songxd
 * @date 2021-01-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FlowConfigDTO extends BaseDTO{

    private Integer id;

    /**
     * 业务类型:1=免费空投 2=持币空投
     */
    private Integer bizType;

    /**
     * 审批级别
     */
    private Integer levelCount;

    /**
     * 是否允许修改 0=否 1=是
     */
    private Integer allowModify;

    /**
     * 是否允许禁用:0=否 1=是
     */
    private Integer allowForbidden;

    /**
     * 状态 0=禁用 1=启用
     */
    private Integer status;

    private List<FlowNodeDTO> nodes;
}
