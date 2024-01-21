package io.bhex.broker.admin.controller.param;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AllocationPO {
    @NotNull
    private Long id;
    @NotNull
    private Integer authType;
    @NotNull
    private String verifyCode;
}
