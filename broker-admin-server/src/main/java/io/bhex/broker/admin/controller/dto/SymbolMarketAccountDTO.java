package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SymbolMarketAccountDTO {

    private Long id;

    private Long orgId;

    private Long exchangeId;

    private Long accountId;

    private String symbolId;

    private Integer category;

    private String makerBuyFeeRate;

    private String makerSellFeeRate;

    private String takerBuyFeeRate;

    private String takerSellFeeRate;
}
