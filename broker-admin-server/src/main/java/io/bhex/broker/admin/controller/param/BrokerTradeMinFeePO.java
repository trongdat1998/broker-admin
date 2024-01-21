package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.bhex.bhop.common.util.percent.Percentage;
import io.bhex.bhop.common.util.percent.PercentageInputDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @ProjectName: exchange
 * @Package: io.bhex.ex.admin.dto.param
 * @Author: ming.xu
 * @CreateDate: 16/11/2018 10:24 AM
 * @Copyright（C）: 2018 BHEX Inc. All rights reserved.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrokerTradeMinFeePO {

    Long exchangeId;

    @JsonDeserialize(using = PercentageInputDeserialize.class)
    @Percentage(min = "0", max = "100")
    BigDecimal makerFeeRate;

    @JsonDeserialize(using = PercentageInputDeserialize.class)
    @Percentage(min = "0.0008", max = "100")
    BigDecimal takerFeeRate;

    @JsonDeserialize(using = PercentageInputDeserialize.class)
    @Percentage(min = "0", max = "100")
    BigDecimal makerBonusRate;
}