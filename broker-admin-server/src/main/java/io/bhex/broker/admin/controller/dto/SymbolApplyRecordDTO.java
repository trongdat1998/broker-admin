package io.bhex.broker.admin.controller.dto;

import io.bhex.base.bhadmin.SymbolApplyObj;
import io.bhex.base.exadmin.SymbolRecord;
import io.bhex.base.proto.DecimalUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SymbolApplyRecordDTO {
    private Long id;
    private Integer state;

    private String symbolId;
    private String baseToken;
    private String quoteToken;

    private BigDecimal minPricePrecision;
    private BigDecimal basePrecision;
    private BigDecimal minTradeQuantity;

    private BigDecimal quotePrecision;
    private BigDecimal minTradeAmt;
    private String mergeDigitDepth;

    private String reason;

    private Long createAt;
    private Long updateAt;
    private Integer updateStatus;

    public SymbolApplyObj toApplyObj() {
        return SymbolApplyObj.newBuilder()
            .setId(this.id)
            .setState(this.state)
            .setSymbolId(this.symbolId)
            .setBaseTokenId(this.baseToken)
            .setQuoteTokenId(this.quoteToken)
            .setMinPricePrecision(DecimalUtil.fromBigDecimal(this.minPricePrecision))
            .setBasePrecision(DecimalUtil.fromBigDecimal(this.basePrecision))
            .setMinTradeQuantity(DecimalUtil.fromBigDecimal(this.minTradeQuantity))
            .setQuotePrecision(DecimalUtil.fromBigDecimal(this.quotePrecision))
            .setMinTradeAmt(DecimalUtil.fromBigDecimal(this.minTradeAmt))
            .setMergeDigitDepth(this.mergeDigitDepth)
            .setReason(this.reason)
            .setCreateAt(this.createAt)
            .setUpdateAt(this.updateAt)
            .build();
    }

    public static SymbolApplyRecordDTO parseSymbolRecord(SymbolApplyObj applyObj) {
        return SymbolApplyRecordDTO.builder()
            .id(applyObj.getId())
            .state(applyObj.getState())
            .symbolId(applyObj.getSymbolId())
            .baseToken(applyObj.getBaseTokenId())
            .quoteToken(applyObj.getQuoteTokenId())
            .minPricePrecision(DecimalUtil.toBigDecimal(applyObj.getMinPricePrecision()))
            .basePrecision(DecimalUtil.toBigDecimal(applyObj.getBasePrecision()))
            .minTradeQuantity(DecimalUtil.toBigDecimal(applyObj.getMinTradeQuantity()))
            .quotePrecision(DecimalUtil.toBigDecimal(applyObj.getQuotePrecision()))
            .minTradeAmt(DecimalUtil.toBigDecimal(applyObj.getMinTradeAmt()))
            .mergeDigitDepth(applyObj.getMergeDigitDepth())
            .reason(applyObj.getReason())
            .createAt(applyObj.getCreateAt())
            .updateAt(applyObj.getUpdateAt())
            .updateStatus(applyObj.getUpdateStatus())
            .build();
    }
}
