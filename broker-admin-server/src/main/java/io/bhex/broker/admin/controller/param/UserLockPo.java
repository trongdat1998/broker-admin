package io.bhex.broker.admin.controller.param;


import io.bhex.bhop.common.util.validation.BigDecimalStringValid;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class UserLockPo {

    @NotEmpty
    private String userIds;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private Integer type;

    @NotEmpty
    private String tokenId;

    private String mark;
}
