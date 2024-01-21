package io.bhex.broker.admin.controller.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author JinYuYuan
 * @description
 * @date 2020-08-01 12:11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PushFilterCondition {
    private Integer type;
    private String TokenId;
    private String minValue;
    private String maxValue;
    private Map<String,String> extraInfo;
}
