package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author JinYuYuan
 * @description
 * @date 2021-01-13 18:11
 */
@Data
public class MarginRiskBlackPO {
    @NotNull
    public Long userId;
    @NotBlank
    public String confGroup;
    public String reason = "";
}
