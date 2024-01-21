package io.bhex.broker.admin.controller.param;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.bhex.bhop.common.util.percent.Percentage;
import io.bhex.bhop.common.util.percent.PercentageInputDeserialize;
import io.bhex.bhop.common.util.validation.CommonInputValid;
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
public class DiscountFeeConfigPO {

    private Long id;

    private Long orgId;

    private Long exchangeId;

    private String symbolId;

    @JsonDeserialize(using = PercentageInputDeserialize.class)
    @Percentage(min = "10", max = "100")
    private BigDecimal coinTakerBuyFeeDiscount;

    @JsonDeserialize(using = PercentageInputDeserialize.class)
    @Percentage(min = "10", max = "100")
    private BigDecimal coinTakerSellFeeDiscount;

    @JsonDeserialize(using = PercentageInputDeserialize.class)
    @Percentage(min = "10", max = "100")
    private BigDecimal coinMakerBuyFeeDiscount;

    @JsonDeserialize(using = PercentageInputDeserialize.class)
    @Percentage(min = "10", max = "100")
    private BigDecimal coinMakerSellFeeDiscount;

    //    @JsonDeserialize(using = PercentageInputDeserialize.class)
//    @Percentage(min="10", max="100")
//    private BigDecimal optionTakerBuyFeeDiscount;
//    @JsonDeserialize(using = PercentageInputDeserialize.class)
//    @Percentage(min="10", max="100")
//    private BigDecimal optionTakerSellFeeDiscount;
//    @JsonDeserialize(using = PercentageInputDeserialize.class)
//    @Percentage(min="10", max="100")
//    private BigDecimal optionMakerBuyFeeDiscount;
//    @JsonDeserialize(using = PercentageInputDeserialize.class)
//    @Percentage(min="10", max="100")
//    private BigDecimal optionMakerSellFeeDiscount;
    @JsonDeserialize(using = PercentageInputDeserialize.class)
    @Percentage(min = "10", max = "100")
    private BigDecimal contractTakerBuyFeeDiscount;

    @JsonDeserialize(using = PercentageInputDeserialize.class)
    @Percentage(min = "10", max = "100")
    private BigDecimal contractTakerSellFeeDiscount;

    @JsonDeserialize(using = PercentageInputDeserialize.class)
    @Percentage(min = "0", max = "100")
    private BigDecimal contractMakerBuyFeeDiscount;

    @JsonDeserialize(using = PercentageInputDeserialize.class)
    @Percentage(min = "0", max = "100")
    private BigDecimal contractMakerSellFeeDiscount;

    @CommonInputValid
    private String name;

    @CommonInputValid
    private String mark;

    private Integer status;

    private Date created;

    private Date updated;

    private String userList;
}
