package io.bhex.broker.admin.controller.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bhex.bhop.common.util.percent.PercentageOutputSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountFeeConfigDTO {

    private Long id;

    private Long orgId;

    private Long exchangeId;

    private String symbolId;

    private Integer type;

    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal coinTakerBuyFeeDiscount;
    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal coinTakerSellFeeDiscount;
    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal coinMakerBuyFeeDiscount;
    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal coinMakerSellFeeDiscount;
    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal optionTakerBuyFeeDiscount;
    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal optionTakerSellFeeDiscount;
    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal optionMakerBuyFeeDiscount;
    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal optionMakerSellFeeDiscount;
    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal contractTakerBuyFeeDiscount;
    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal contractTakerSellFeeDiscount;
    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal contractMakerBuyFeeDiscount;
    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal contractMakerSellFeeDiscount;

    private String name;

    private String mark;

    private Integer status;

    private Date created;

    private Date updated;

    private String userList;
}
