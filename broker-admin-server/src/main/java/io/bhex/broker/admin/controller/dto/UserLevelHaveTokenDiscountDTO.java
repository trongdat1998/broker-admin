package io.bhex.broker.admin.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bhex.bhop.common.util.percent.Percentage;
import io.bhex.bhop.common.util.percent.PercentageInputDeserialize;
import io.bhex.bhop.common.util.percent.PercentageOutputSerialize;
import io.bhex.bhop.common.util.validation.TokenValid;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class UserLevelHaveTokenDiscountDTO {


    @TokenValid(allowEmpty = true)
    private String tokenId;

    @NotNull
    private BigDecimal number = BigDecimal.ZERO;

    @JsonSerialize(using = PercentageOutputSerialize.class)
    @JsonDeserialize(using = PercentageInputDeserialize.class)
    @Percentage(min="0", max="100")
    private BigDecimal discount = BigDecimal.ONE;

    private Boolean spotSwitch = false;
    private Boolean contractSwitch = false;
    private Boolean marginSwitch = false;
    private Integer status;

}
