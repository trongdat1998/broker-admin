package io.bhex.broker.admin.controller.param;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SymbolMarketAccountDetailPO {

    private Long id;

    private Long orgId;

    private Long accountId;

    private Long exchangeId;

    private Long fromId;

    private Long endId;

    private Integer limit;

    private String symbolId;

    private String takerBuyFee;

    private String takerSellFee;

    private String makerBuyFee;

    private String makerSellFee;
}
