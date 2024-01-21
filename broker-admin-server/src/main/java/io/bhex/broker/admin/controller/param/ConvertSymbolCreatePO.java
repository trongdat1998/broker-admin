package io.bhex.broker.admin.controller.param;

import io.bhex.bhop.common.util.validation.BigDecimalStringValid;
import io.bhex.bhop.common.util.validation.IntInValid;
import io.bhex.bhop.common.util.validation.SymbolValid;
import io.bhex.bhop.common.util.validation.TokenValid;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author cookie.yuan
 * @description
 * @date 2020-08-18
 */
@Data
public class ConvertSymbolCreatePO {

    @NotNull
    private Long brokerAccountId;
    @TokenValid
    private String purchaseTokenId;
    @TokenValid
    private String offeringsTokenId;
    @IntInValid({1, 2}) //1固定价格，2浮动价格
    private Integer priceType;
    @BigDecimalStringValid
    private String priceValue;
    @NotNull
    private Integer purchasePrecision;
    @NotNull
    private Integer offeringsPrecision;
    @SymbolValid(allowEmpty = true)
    private String symbolId;
    @BigDecimalStringValid
    private String minQuantity;
    @BigDecimalStringValid
    private String maxQuantity;
    @BigDecimalStringValid
    private String accountDailyLimit;
    @BigDecimalStringValid
    private String accountTotalLimit;
    @BigDecimalStringValid
    private String symbolDailyLimit;
    private Boolean verifyKyc;
    private Boolean verifyMobile;
    private Integer verifyVipLevel;
    private Integer status;
    @IntInValid({1, 2}) //1GA校验，2短信校验
    private Integer authType;
    @NotNull
    private String verifyCode;
}
