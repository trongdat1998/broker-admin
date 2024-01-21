package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.bhex.bhop.common.util.percent.Percentage;
import io.bhex.bhop.common.util.percent.PercentageInputDeserialize;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class UpdateBrokerTradeFeePO {

    Long exchangeId;

    @NotEmpty
    @Length(min = 1, max = 99)
    String symbolId;

    @NotNull(message = "{javax.validation.constraints.NotEmpty.message}")
    @JsonDeserialize(using = PercentageInputDeserialize.class)
    @Percentage(min="0", max="100")
    BigDecimal makerFeeRate;

//    @DecimalMin(value = "0")
//    @DecimalMax(value = "0")
//    private BigDecimal makerRewardToTakerRate;

    @NotNull(message = "{javax.validation.constraints.NotEmpty.message}")
    @JsonDeserialize(using = PercentageInputDeserialize.class)
    @Percentage(min="0", max="100")
    BigDecimal takerFeeRate;

    @NotNull(message = "{javax.validation.constraints.NotEmpty.message}")
    @JsonDeserialize(using = PercentageInputDeserialize.class)
    @Percentage(min="0", max="100")
    BigDecimal takerRewardToMakerRate;

    private Integer category = 1; // 默认为币币 1主类别，2创新类别, 3期权, 4期货
}
