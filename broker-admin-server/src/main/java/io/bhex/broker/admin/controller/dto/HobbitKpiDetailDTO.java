package io.bhex.broker.admin.controller.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HobbitKpiDetailDTO {

    private Long id;

    private Long statisticsTime;

    private BigDecimal directCommissionUsdt = BigDecimal.ZERO;

    private BigDecimal indirectCommissionUsdt = BigDecimal.ZERO;

    private BigDecimal tradeKpiHbc = BigDecimal.ZERO;

    private BigDecimal directKpiHbc = BigDecimal.ZERO;

    private BigDecimal indirectKpiHbc = BigDecimal.ZERO;

    private Long tradeUserId;

    private Long tradeDetailId;//'tradeDetailId'

    private String feeTokenId;//'手续费tokenId'

    private BigDecimal fee;//'原始手续费金额'

    private BigDecimal hbcFee;

    private BigDecimal usdtFee;//'换算usdt金额'

    private BigDecimal hbcUsdtRate;


    private Integer side; //'0买1卖'

    private String symbolId;//'交易的币对'

    private Integer category;//1币币，2创新类别, 3期权, 4期货 5邀请产生

    private Long orderId;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal amount;//'交易数量'

    private Long matchTime;

    private Long leaderUserId;

    private Integer type;



}
