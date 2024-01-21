package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SimpleBrokerUserPO {
    @NotNull(message = "{javax.validation.constraints.NotEmpty.message}")
    private Long userId;

    private String remark;
}
