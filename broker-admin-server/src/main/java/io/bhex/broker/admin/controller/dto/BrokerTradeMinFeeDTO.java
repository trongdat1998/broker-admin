package io.bhex.broker.admin.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bhex.bhop.common.util.percent.PercentageOutputSerialize;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:交易所对券商费率最小值的设置
 * @Date: 2018/11/22 上午10:18
 * @Author: liwei
 * @Copyright（C）: 2018 BlueHelix Inc. All rights reserved.
 */
@Data
@Builder
public class BrokerTradeMinFeeDTO {


    private Long exchangeId;

    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal makerFeeRate;

    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal takerFeeRate;

    @JsonSerialize(using = PercentageOutputSerialize.class)
    @JsonProperty(value = "takerRewardToMakerRate")
    private BigDecimal makerBonusRate;
}
