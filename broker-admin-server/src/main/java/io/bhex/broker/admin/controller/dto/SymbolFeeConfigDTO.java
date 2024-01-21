package io.bhex.broker.admin.controller.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bhex.bhop.common.util.percent.PercentageOutputSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SymbolFeeConfigDTO {

    private Long id;

    private Long orgId;

    private Long exchangeId;

    private String symbolId;

    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal takerBuyFee;
    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal takerSellFee;
    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal makerBuyFee;
    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal makerSellFee;

    private Integer status;

    private String created;

    private String updated;

    private String baseTokenId;

    private String quoteTokenId;
}
