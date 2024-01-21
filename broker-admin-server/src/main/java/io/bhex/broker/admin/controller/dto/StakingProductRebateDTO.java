package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StakingProductRebateDTO {
    Long id;
    Long productId;
    String productName;
    Integer productType;
    Integer dividendType;
    String tokenId;
    String tokenName;
    Long rebateDate;
    Integer type;
    String principalAmount;
    String interestAmount;
    String interestTokenId;
    String interestTokenName;
    String rebateRate;
    Integer status;
    Integer numberOfPeriods;
    Long updateAt;
    Integer rebateCalcWay;
    String rebateAmount;
}
