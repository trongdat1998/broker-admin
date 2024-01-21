package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bhex.base.bhadmin.ContractApplyObj;
import io.bhex.base.proto.DecimalUtil;
import io.bhex.bhop.common.util.SymbolUtil;
import io.bhex.bhop.common.util.percent.CommaInputDeserialize;
import io.bhex.bhop.common.util.percent.PercentageInputDeserialize;
import io.bhex.bhop.common.util.percent.PercentageOutputSerialize;
import io.bhex.bhop.common.util.validation.CommonInputValid;
import io.bhex.bhop.common.util.validation.SymbolValid;
import io.bhex.bhop.common.util.validation.TokenValid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * @ProjectName: exchange
 * @Package: io.bhex.ex.admin.dto
 * @Author: ming.xu
 * @CreateDate: 2019/10/10 3:17 PM
 * @Copyright（C）: 2019 BHEX Inc. All rights reserved.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractApplyPO {

    private Long id;

    @SymbolValid(allowEmpty = true)
    private String symbolId;

    @SymbolValid(allowEmpty = true)
    private String symbolName;

    private List<SymbolNameLocale> symbolNameLocaleList;

    private List<RiskLimit> riskLimitList;

    @TokenValid
    @NotEmpty(message = "symbol.record.underlyingId.required")
    private String displayUnderlyingId;


    @NotNull(message = "symbol.record.minTradeQuantity.required")
    private BigDecimal minTradeQuantity; //单次交易最小交易base的数量

    @NotNull(message = "symbol.record.minPricePrecision.required")
    private BigDecimal minPricePrecision; //每次价格变动，最小的变动单位

    @NotEmpty(message = "symbol.record.digitMergeList.required")
    @JsonDeserialize(using = CommaInputDeserialize.class)
    private String digitMergeList; //深度合并。格式：0.01,0.0001,0.000001

    @NotNull(message = "symbol.record.basePrecision.required")
    private BigDecimal basePrecision;

    @TokenValid
    @NotEmpty(message = "symbol.record.currency.required")
    private String currency; //计价单位(token_id)

    @NotNull(message = "symbol.record.contractMultiplier.required")
    private BigDecimal contractMultiplier; //合约乘数

    @NotEmpty(message = "symbol.record.leverageRange.required")
    @JsonDeserialize(using = CommaInputDeserialize.class)
    private String leverageRange; //杠杆范围

    @NotNull(message = "symbol.record.overPriceRange.required")
    @JsonDeserialize(using = PercentageInputDeserialize.class)
    private BigDecimal overPriceUpRange; //超价浮动范围

    @NotNull(message = "symbol.record.overPriceRange.required")
    @JsonDeserialize(using = PercentageInputDeserialize.class)
    private BigDecimal overPriceDownRange; //超价浮动范围

    @NotNull(message = "symbol.record.marketPriceRange.required")
    @JsonDeserialize(using = PercentageInputDeserialize.class)
    private BigDecimal marketPriceUpRange; //市价浮动范围

    @NotNull(message = "symbol.record.marketPriceRange.required")
    @JsonDeserialize(using = PercentageInputDeserialize.class)
    private BigDecimal marketPriceDownRange; //市价浮动范围

    @NotNull(message = "symbol.record.isPerpetualSwap.required")
    private Integer isPerpetualSwap;

    @NotNull(message = "symbol.record.isReverse.required")
    private Integer isReverse;

    @NotNull(message = "symbol.record.marginPrecision.required")
    private BigDecimal marginPrecision; //用户修改保证金的最小精度

    private Long exchangeId; //申请的交易所id
    private Long brokerId; //申请的交易所id
    private Integer state; //期货审核状态值 0 申请中 1 通过 2 拒绝

    @Data
    public static class RiskLimit {

        private BigDecimal riskLimitAmount;

        @JsonDeserialize(using = PercentageInputDeserialize.class)
        @JsonSerialize(using = PercentageOutputSerialize.class)
        private BigDecimal maintainMargin;

        @JsonDeserialize(using = PercentageInputDeserialize.class)
        @JsonSerialize(using = PercentageOutputSerialize.class)
        private BigDecimal initialMargin;
    }

    @Data
    public static class SymbolNameLocale {

        private String locale;

        @CommonInputValid
        private String name;
    }

    public ContractApplyObj toFuturesRecord(Long brokerId, String symbolId, String symbolNameLocaleJson, String riskLimitJson) {
        String underlyingId;
        String quoteTokenId;

        if (isReverse.equals(1)) {
            // 反向合约 举例： BTC反向 支付BTC，标的USDT
            underlyingId = this.currency;
            quoteTokenId = this.displayUnderlyingId;
        } else {
            // 正向合约 举例：BTC正向 支付USDT，标的BTC
            underlyingId = this.displayUnderlyingId;
            quoteTokenId = this.currency;

        }
        String overPriceRange = SymbolUtil.priceRange(overPriceDownRange, overPriceUpRange);
        String marketPriceRange = SymbolUtil.priceRange(marketPriceDownRange, marketPriceUpRange);
        return ContractApplyObj.newBuilder()
                .setId(this.id == null ? 0 : this.id)
                .setSymbolId(symbolId)
                .setSymbolName(symbolId)
                .setBaseTokenId(symbolId)
                .setQuoteTokenId(quoteTokenId)
                .setUnderlyingId(underlyingId)
                .setDisplayUnderlyingId(displayUnderlyingId)
                .setSymbolNameLocaleJson(symbolNameLocaleJson)
                .setRiskLimitJson(riskLimitJson)
                .setBrokerId(brokerId)
                .setMinTradeQuantity(DecimalUtil.fromBigDecimal(this.minTradeQuantity))
                .setMinTradeAmount(DecimalUtil.fromBigDecimal(new BigDecimal("0.000000001")))
                .setMinPricePrecision(DecimalUtil.fromBigDecimal(this.minPricePrecision))
                .setDigitMergeList(this.digitMergeList)
                .setBasePrecision(DecimalUtil.fromBigDecimal(this.basePrecision))
                .setQuotePrecision(DecimalUtil.fromBigDecimal(isReverse.equals(0)? new BigDecimal("0.0001"): new BigDecimal("0.000000000000000001")))
                .setDisplayToken(this.currency)
                .setCurrency(this.currency)
                .setCurrencyDisplay("$")
                .setContractMultiplier(DecimalUtil.fromBigDecimal(this.contractMultiplier))
                .setLimitDownInTradingHours(DecimalUtil.fromBigDecimal(BigDecimal.ZERO))
                .setLimitUpInTradingHours(DecimalUtil.fromBigDecimal(BigDecimal.ZERO))
                .setLimitDownOutTradingHours(DecimalUtil.fromBigDecimal(BigDecimal.ZERO))
                .setLimitUpOutTradingHours(DecimalUtil.fromBigDecimal(BigDecimal.ZERO))
                .setMaxLeverage(DecimalUtil.fromBigDecimal(new BigDecimal("100")))
                .setLeverageRange(this.leverageRange)
                .setOverPriceRange(overPriceRange)
                .setMarketPriceRange(marketPriceRange)
                .setIsPerpetualSwap(this.isPerpetualSwap)
                .setFundingLowerBound(DecimalUtil.fromBigDecimal(new BigDecimal("-0.003")))
                .setFundingUpperBound(DecimalUtil.fromBigDecimal(new BigDecimal("0.003")))
                .setFundingInterest(DecimalUtil.fromBigDecimal(BigDecimal.ZERO))
                .setIsReverse(this.isReverse)
                .setMarginPrecision(DecimalUtil.fromBigDecimal(this.marginPrecision))
                .build();
    }
}
