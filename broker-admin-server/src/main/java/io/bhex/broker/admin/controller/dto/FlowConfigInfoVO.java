package io.bhex.broker.admin.controller.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FlowConfigInfoVO {
    private Integer id;
    private Long orgId;
    private String flowName;
    private Integer bizType;
    private Integer levelCount;
    private Integer allowModify;
    private Integer allowForbidden;
    private Integer status;
    private List<FlowNodeVO> nodes;
}
