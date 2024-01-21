package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.bhex.bhop.common.util.percent.PercentageInputDeserialize;
import io.bhex.bhop.common.util.validation.BigDecimalStringValid;
import io.bhex.bhop.common.util.validation.TokenValid;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class CalcProductRebatePO {
    @NotNull
    private Long productId;

    @NotNull
    private Long productRebateId;

    @JsonDeserialize(using = PercentageInputDeserialize.class)
    private BigDecimal rebateRate;

    @BigDecimalStringValid
    private String rebateAmount;

    @TokenValid
    private String tokenId;

    @NotNull
    private Integer authType;

    @NotEmpty
    private String verifyCode;

}
