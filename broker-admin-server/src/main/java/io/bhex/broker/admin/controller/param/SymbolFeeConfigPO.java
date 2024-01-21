package io.bhex.broker.admin.controller.param;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.bhex.bhop.common.util.percent.PercentageInputDeserialize;
import io.bhex.bhop.common.util.validation.SymbolValid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SymbolFeeConfigPO {

    private Long id;

    private Long orgId;

    private Long exchangeId;

    @SymbolValid
    private String symbolId;

    @JsonDeserialize(using = PercentageInputDeserialize.class)
    private BigDecimal takerBuyFee;

    @JsonDeserialize(using = PercentageInputDeserialize.class)
    private BigDecimal takerSellFee;

    @JsonDeserialize(using = PercentageInputDeserialize.class)
    private BigDecimal makerBuyFee;

    @JsonDeserialize(using = PercentageInputDeserialize.class)
    private BigDecimal makerSellFee;

    private Integer status;

    private String created;

    private String updated;
}
