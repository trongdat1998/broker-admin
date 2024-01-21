package io.bhex.broker.admin.controller.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bhex.bhop.common.util.percent.PercentageOutputSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentProductRebateDTO {
    private Long rebateDate;
    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal rebateRate;
    private Integer status;
}
