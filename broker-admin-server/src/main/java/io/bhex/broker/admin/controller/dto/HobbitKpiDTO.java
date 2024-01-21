package io.bhex.broker.admin.controller.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class HobbitKpiDTO {

    private Long statisticsTime;

    private BigDecimal totalCommissionUsdt = BigDecimal.ZERO;

    private BigDecimal directCommissionUsdt = BigDecimal.ZERO;

    private BigDecimal indirectCommissionUsdt = BigDecimal.ZERO;

    private BigDecimal tradeKpiHbc = BigDecimal.ZERO;

    private BigDecimal directKpiHbc = BigDecimal.ZERO;

    private BigDecimal indirectKpiHbc = BigDecimal.ZERO;

    private BigDecimal totalKpiHbc = BigDecimal.ZERO;


}
