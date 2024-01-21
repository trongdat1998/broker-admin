package io.bhex.broker.admin.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FlowNodeVO {
    private Integer level;
    private Long approver;
    private String approverName;
    private Integer allowNotify;
    private Integer notifyMode;
    private Integer allowPass;
}
