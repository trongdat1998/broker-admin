package io.bhex.broker.admin.controller.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FlowConfigVO {
    private Integer id;
    private Long orgId;
    private Long userId;
    private String userName;
    private String flowName;
    private Integer bizType;
    private String bizName;
    private Integer levelCount;
    private Integer allowModify;
    private Integer allowForbidden;
    private Long createdAt;
    private Integer status;
}
