package io.bhex.broker.admin.controller.param;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import io.bhex.broker.admin.controller.dto.NewsDetailsDTO;
import lombok.Data;

@Data
public class OptionCreatePO {
    private Long id;
    private Long orgId;
    private Long exchangeId;
    @NotNull
    private String tokenId;
    @NotNull
    private String tokenName;
    @NotNull
    private BigDecimal strikePrice;
    @NotNull
    private Long issueDate;
    @NotNull
    private Long settlementDate;
    @NotNull
    private Integer isCall;
    @NotNull
    private BigDecimal maxPayOff;
    @NotNull
    private Integer positionLimit;
    @NotNull
    private String indexToken;
    @NotNull
    private BigDecimal minTradeQuantity;
    @NotNull
    private BigDecimal minTradeAmount;
    @NotNull
    private BigDecimal minPricePrecision;
    @NotNull
    private String digitMergeList;
    @NotNull
    private BigDecimal basePrecision;
    @NotNull
    private BigDecimal quotePrecision;
    @NotNull
    private BigDecimal makerFeeRate;
    @NotNull
    private BigDecimal takerFeeRate;
    @NotNull
    private String coinToken;
    private BigDecimal minPrecision;
    @NotNull
    private String underlyingId;

    public static void main(String[] args) {
        OptionCreatePO optionCreatePO =  new OptionCreatePO();
        optionCreatePO.setOrgId(6001l);
        optionCreatePO.setExchangeId(301L);
        optionCreatePO.setTokenId("BTC0911CS10600");
        optionCreatePO.setTokenName("BTC0911看涨10600");
        optionCreatePO.setStrikePrice(new BigDecimal(10600));
        optionCreatePO.setIssueDate(1599205800000l);
        optionCreatePO.setSettlementDate(1599810600000L);
        optionCreatePO.setIsCall(0);
        optionCreatePO.setMaxPayOff(new BigDecimal(500));
        optionCreatePO.setPositionLimit(100);
        optionCreatePO.setIndexToken("BTCUSDT");
        optionCreatePO.setMinTradeQuantity(new BigDecimal(1));
        optionCreatePO.setMinTradeAmount(new BigDecimal(0.01));
        optionCreatePO.setMinPricePrecision(new BigDecimal(0.0001));
        optionCreatePO.setDigitMergeList("0.0001");
        optionCreatePO.setBasePrecision(BigDecimal.ONE);
        optionCreatePO.setQuotePrecision(new BigDecimal("0.0001"));
        optionCreatePO.setMakerFeeRate(new BigDecimal("0.005"));
        optionCreatePO.setTakerFeeRate(new BigDecimal("0.005"));
        optionCreatePO.setCoinToken("USDT");
        optionCreatePO.setMinPrecision(new BigDecimal("8"));
        optionCreatePO.setUnderlyingId("BTC");

        System.out.println(new Gson().toJson(optionCreatePO));
    }
}
