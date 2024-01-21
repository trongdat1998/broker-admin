package io.bhex.broker.admin.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.reflect.TypeToken;
import io.bhex.base.bhadmin.ContractApplyObj;
import io.bhex.base.proto.DecimalUtil;
import io.bhex.bhop.common.util.Combo2;
import io.bhex.bhop.common.util.SymbolUtil;
import io.bhex.bhop.common.util.percent.PercentageOutputSerialize;
import io.bhex.broker.admin.controller.param.ContractApplyPO;
import io.bhex.broker.common.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

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
public class ContractApplyDTO {

    private Long id;

    private String symbolId;

    private String symbolName;

    private String baseTokenId;

    private String quoteTokenId;

    @JsonIgnore
    private String underlyingId; //标的id

    private String displayUnderlyingId; //展示标的id

    private List<ContractApplyPO.SymbolNameLocale> symbolNameLocaleList;

    private List<ContractApplyPO.RiskLimit> riskLimitList;

    private Long exchangeId; //申请的交易所id

    private Integer state; //期货审核状态值 0 申请中 1 通过 2 拒绝

    private BigDecimal minTradeQuantity; //单次交易最小交易base的数量

    private BigDecimal minTradeAmount; //最小交易额

    private BigDecimal minPricePrecision; //每次价格变动，最小的变动单位

    private String digitMergeList; //深度合并。格式：0.01,0.0001,0.000001

    private BigDecimal basePrecision;

    private BigDecimal quotePrecision;

    @JsonIgnore
    private String displayToken; //显示用的估价token

    private String currency; //计价单位(token_id)

    @JsonIgnore
    private String currencyDisplay; //显示价格单位

    private BigDecimal contractMultiplier; //合约乘数

    @JsonIgnore
    private BigDecimal limitDownInTradingHours; //交易时段内下跌限价

    @JsonIgnore
    private BigDecimal limitUpInTradingHours; //交易时段内上涨限价

    @JsonIgnore
    private BigDecimal limitDownOutTradingHours; //交易时段外下跌限价

    @JsonIgnore
    private BigDecimal limitUpOutTradingHours; //交易时段外上涨限价

    private BigDecimal maxLeverage; //最大杠杆

    private String leverageRange; //杠杆范围

    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal overPriceUpRange; //超价浮动范围

    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal overPriceDownRange; //超价浮动范围

    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal marketPriceUpRange; //市价浮动范围

    @JsonSerialize(using = PercentageOutputSerialize.class)
    private BigDecimal marketPriceDownRange; //市价浮动范围

    private Integer isPerpetualSwap;

    @JsonIgnore
    private String indexToken; //指数名称

    @JsonIgnore
    private String displayIndexToken; //用于页面显示指数价格(正向=index_token,反则反之)

    @JsonIgnore
    private BigDecimal fundingLowerBound; //永续合约资金费率下限

    @JsonIgnore
    private BigDecimal fundingUpperBound; //永续合约资金费率下限

    @JsonIgnore
    private BigDecimal fundingInterest; //永续合约两币种借贷利率之和

    private Long createdAt;

    private Long updatedAt;

    private Integer isReverse;

    private BigDecimal marginPrecision; //用户修改保证金的最小精度

    public static ContractApplyDTO parseFromProtoObj(ContractApplyObj symbolRecord) {
        String symbolNameLocaleJson = symbolRecord.getSymbolNameLocaleJson();
        String riskLimitJson = symbolRecord.getRiskLimitJson();
        List<ContractApplyPO.SymbolNameLocale> nameLocaleList = new ArrayList<>();
        List<ContractApplyPO.RiskLimit> riskLimitList = new ArrayList<>();
        String symbolName = symbolRecord.getSymbolName();
        if (StringUtils.isNotEmpty(symbolNameLocaleJson)) {
            nameLocaleList = JsonUtil.defaultGson().fromJson(symbolNameLocaleJson, new TypeToken<List<ContractApplyPO.SymbolNameLocale>>() {}.getType());
            Map<String, ContractApplyPO.SymbolNameLocale> symbolNameLocaleMap = nameLocaleList.stream()
                    .collect(Collectors.toMap(ContractApplyPO.SymbolNameLocale::getLocale, Function.identity()));
            ContractApplyPO.SymbolNameLocale nameLocal = Objects.nonNull(symbolNameLocaleMap.get("en-us"))? symbolNameLocaleMap.get("en-us"): symbolNameLocaleMap.get("zh-cn");
            if (Objects.nonNull(nameLocal)) {
                symbolName = nameLocal.getName();
            }
        }

        if (StringUtils.isNotEmpty(riskLimitJson)) {
            riskLimitList = JsonUtil.defaultGson().fromJson(riskLimitJson, new TypeToken<List<ContractApplyPO.RiskLimit>>() {}.getType());
        }
        Combo2<BigDecimal, BigDecimal> overPriceRangeCombo2 = SymbolUtil.priceRangeFromString(symbolRecord.getOverPriceRange());
        Combo2<BigDecimal, BigDecimal> marketPriceRangeCombo2 = SymbolUtil.priceRangeFromString(symbolRecord.getMarketPriceRange());
        return ContractApplyDTO.builder()
                .id(symbolRecord.getId())
                .symbolId(symbolRecord.getSymbolId())
                .symbolName(symbolName)
                .baseTokenId(symbolRecord.getBaseTokenId())
                .quoteTokenId(symbolRecord.getQuoteTokenId())
                .underlyingId(symbolRecord.getUnderlyingId())
                .displayUnderlyingId(symbolRecord.getDisplayUnderlyingId())
                .symbolNameLocaleList(nameLocaleList)
                .riskLimitList(riskLimitList)
                .exchangeId(symbolRecord.getExchangeId())
                .minTradeQuantity(DecimalUtil.toBigDecimal(symbolRecord.getMinTradeQuantity()))
                .minTradeAmount(DecimalUtil.toBigDecimal(symbolRecord.getMinTradeAmount()))
                .minPricePrecision(DecimalUtil.toBigDecimal(symbolRecord.getMinPricePrecision()))
                .digitMergeList(symbolRecord.getDigitMergeList())
                .basePrecision(DecimalUtil.toBigDecimal(symbolRecord.getBasePrecision()))
                .quotePrecision(DecimalUtil.toBigDecimal(symbolRecord.getQuotePrecision()))
                .displayToken(symbolRecord.getDisplayToken())
                .currency(symbolRecord.getCurrency())
                .currencyDisplay(symbolRecord.getCurrencyDisplay())
                .contractMultiplier(DecimalUtil.toBigDecimal(symbolRecord.getContractMultiplier()))
                .limitDownInTradingHours(DecimalUtil.toBigDecimal(symbolRecord.getLimitDownInTradingHours()))
                .limitUpInTradingHours(DecimalUtil.toBigDecimal(symbolRecord.getLimitUpInTradingHours()))
                .limitDownOutTradingHours(DecimalUtil.toBigDecimal(symbolRecord.getLimitDownOutTradingHours()))
                .limitUpOutTradingHours(DecimalUtil.toBigDecimal(symbolRecord.getLimitUpOutTradingHours()))
                .maxLeverage(DecimalUtil.toBigDecimal(symbolRecord.getMaxLeverage()))
                .leverageRange(symbolRecord.getLeverageRange())
                .overPriceDownRange(Objects.isNull(overPriceRangeCombo2)? BigDecimal.ZERO: overPriceRangeCombo2.getV1())
                .overPriceUpRange(Objects.isNull(overPriceRangeCombo2)? BigDecimal.ZERO: overPriceRangeCombo2.getV2())
                .marketPriceDownRange(Objects.isNull(marketPriceRangeCombo2)? BigDecimal.ZERO: marketPriceRangeCombo2.getV1())
                .marketPriceUpRange(Objects.isNull(marketPriceRangeCombo2)? BigDecimal.ZERO: marketPriceRangeCombo2.getV2())
                .isPerpetualSwap(symbolRecord.getIsPerpetualSwap())
                .indexToken(symbolRecord.getIndexToken())
                .displayIndexToken(symbolRecord.getDisplayIndexToken())
                .fundingLowerBound(DecimalUtil.toBigDecimal(symbolRecord.getFundingLowerBound()))
                .fundingUpperBound(DecimalUtil.toBigDecimal(symbolRecord.getFundingUpperBound()))
                .fundingInterest(DecimalUtil.toBigDecimal(symbolRecord.getFundingInterest()))
                .createdAt(symbolRecord.getCreatedAt())
                .updatedAt(symbolRecord.getUpdatedAt())
                .isReverse(symbolRecord.getIsReverse())
                .state(symbolRecord.getState())
                .marginPrecision(DecimalUtil.toBigDecimal(symbolRecord.getMarginPrecision()))
                .build();
    }
}
