package io.bhex.broker.admin.controller.param;

import io.bhex.bhop.common.util.validation.BigDecimalStringValid;
import io.bhex.bhop.common.util.validation.TokenValid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SetMarginLoanLimitPo {
    @TokenValid
    private String tokenId;

    @BigDecimalStringValid
    private String limitAmount;

    @NotNull
    private Integer status;
}
