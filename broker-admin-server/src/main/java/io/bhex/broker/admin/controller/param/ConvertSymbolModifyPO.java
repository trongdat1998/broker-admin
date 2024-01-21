package io.bhex.broker.admin.controller.param;

import io.bhex.bhop.common.util.validation.BigDecimalStringValid;
import io.bhex.bhop.common.util.validation.IntInValid;
import io.bhex.bhop.common.util.validation.SymbolValid;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author cookie.yuan
 * @description
 * @date 2020-08-18
 */
@Data
public class ConvertSymbolModifyPO {

    @NotNull
    private Long convertSymbolId;
    private Long brokerAccountId;
    @SymbolValid(allowEmpty = true)
    private String purchaseTokenId;
    @SymbolValid(allowEmpty = true)
    private String offeringsTokenId;
    private Integer purchasePrecision;
    private Integer offeringsPrecision;
    @IntInValid({1, 2})
    private Integer priceType;
    @BigDecimalStringValid
    private String priceValue;
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
    @IntInValid({1, 2}) //1GA校验，2短信校验
    private Integer authType;
    @NotNull
    private String verifyCode;

}
