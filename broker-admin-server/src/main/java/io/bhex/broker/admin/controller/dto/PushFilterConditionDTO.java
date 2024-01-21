package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author JinYuYuan
 * @description
 * @date 2020-08-01 15:04
 */
@Data
@Builder(builderClassName = "Builder", toBuilder = true)
public class PushFilterConditionDTO {
    private Integer type;
    private String TokenId;
    private String minValue;
    private String maxValue;
    private Map<String,String> extraInfo;
}
