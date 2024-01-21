package io.bhex.broker.admin.controller.param;


import io.bhex.bhop.common.util.validation.BigDecimalStringValid;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class UserUnlockPo {
    private Long orgId;
    private Long lockId;

    @NotNull
    private Long userId;

    @NotNull
    @Positive
    private BigDecimal amount;

    private String mark;
}
