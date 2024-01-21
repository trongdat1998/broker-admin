package io.bhex.broker.admin.controller.param;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UnfreezeUserPO {
    @NotNull(message = "{javax.validation.constraints.NotEmpty.message}")
    private Long userId;

    private Integer frozenType;

    private String remark;

    private Integer authType;

    private String verifyCode;

}
