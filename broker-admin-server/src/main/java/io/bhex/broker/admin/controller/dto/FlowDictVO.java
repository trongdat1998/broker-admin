package io.bhex.broker.admin.controller.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FlowDictVO {
    private Integer dictValue;
    private String dictText;
}
