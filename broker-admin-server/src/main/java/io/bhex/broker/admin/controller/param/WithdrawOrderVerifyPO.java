package io.bhex.broker.admin.controller.param;

import io.bhex.bhop.common.util.validation.CommonInputValid;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class WithdrawOrderVerifyPO {

    @NotNull
    private Long withdrawOrderId;

    @NotNull
    private Long userId;

    @NotNull
    private Boolean verifyPassed;

    private Integer failedReason;

    @CommonInputValid
    private String remark;

    @CommonInputValid
    private String refuseReason;

}