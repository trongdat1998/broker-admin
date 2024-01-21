package io.bhex.broker.admin.controller.param;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.bhex.base.bhadmin.SymbolApplyObj;
import io.bhex.base.proto.DecimalUtil;
import io.bhex.bhop.common.util.percent.PercentageOutputSerialize;
import io.bhex.bhop.common.util.validation.TokenValid;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class SymbolApplyPO {

    private Long id;

    private String symbolId;

    @TokenValid
    @NotEmpty(message = "symbol.record.baseToken.required")
    private String baseToken;

    @TokenValid
    @NotEmpty(message = "symbol.record.quoteToken.required")
    private String quoteToken;

    @NotNull(message = "symbol.record.minPricePrecision.required")
    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal minPricePrecision;
    @NotNull(message = "symbol.record.basePrecision.required")
    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal basePrecision;
    @NotNull(message = "symbol.record.minTradeQuantity.required")
    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal minTradeQuantity;
    @NotNull(message = "symbol.record.quotePrecision.required")
    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal quotePrecision;
    @NotNull(message = "symbol.record.minTradeAmt.required")
    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal minTradeAmt;
    private String mergeDigitDepth;
    private String reason;
    private Long onlineTime;

    public SymbolApplyObj toApplyObj(Long exchangeId, long brokerId) {
        return SymbolApplyObj.newBuilder()
                .setId(this.id == null ? 0 : this.id)
                .setExchangeId(exchangeId)
                .setBrokerId(brokerId)
                .setSymbolId(this.symbolId)
                .setBaseTokenId(this.baseToken)
                .setQuoteTokenId(this.quoteToken)
                .setMinPricePrecision(DecimalUtil.fromBigDecimal(this.minPricePrecision))
                .setBasePrecision(DecimalUtil.fromBigDecimal(this.basePrecision))
                .setMinTradeQuantity(DecimalUtil.fromBigDecimal(this.minTradeQuantity))
                .setQuotePrecision(DecimalUtil.fromBigDecimal(this.quotePrecision))
                .setMinTradeAmt(DecimalUtil.fromBigDecimal(this.minTradeAmt))
                .setMergeDigitDepth(this.mergeDigitDepth)
                .setReason(this.reason == null ? StringUtils.EMPTY : this.reason)
                .setOnlineTime(this.onlineTime == null ? 0 : this.onlineTime)
                .build();
    }
}
