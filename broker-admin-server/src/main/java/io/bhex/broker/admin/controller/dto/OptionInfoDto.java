package io.bhex.broker.admin.controller.dto;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OptionInfoDto {

    private Long id;

    private Long orgId;

    private Long exchangeId;

    private String tokenId;

    private String tokenName;

    private BigDecimal strikePrice;

    private Long issueDate;

    private Long settlementDate;

    private Integer isCall;

    private BigDecimal maxPayOff;

    private BigDecimal positionLimit;

    private String indexToken;

    private BigDecimal minTradeQuantity;

    private BigDecimal minTradeAmount;

    private BigDecimal minPricePrecision;

    private String digitMergeList;

    private BigDecimal basePrecision;

    private BigDecimal quotePrecision;

    private BigDecimal makerFeeRate;

    private BigDecimal takerFeeRate;

    private String coinToken;

    private BigDecimal minPrecision;

    private String underlyingId;
}
