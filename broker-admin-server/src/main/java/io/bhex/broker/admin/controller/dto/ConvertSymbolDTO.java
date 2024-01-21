package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConvertSymbolDTO {

    private Long convertSymbolId;
    private Long brokerId;
    private Long brokerAccountId;
    private String symbolId;
    private String purchaseTokenId;
    private String purchaseTokenName;
    private String offeringsTokenId;
    private String offeringsTokenName;
    private Integer purchasePrecision;
    private Integer offeringsPrecision;
    private Integer priceType;
    private String priceValue;
    private String minQuantity;
    private String maxQuantity;
    private String accountDailyLimit;
    private String accountTotalLimit;
    private String symbolDailyLimit;
    private Boolean verifyKyc;
    private Boolean verifyMobile;
    private Integer verifyVipLevel;
    private Integer status;
    private Long created;
    private Long updated;

}
