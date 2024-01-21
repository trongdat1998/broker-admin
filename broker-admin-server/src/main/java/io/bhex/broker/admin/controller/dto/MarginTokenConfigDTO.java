package io.bhex.broker.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarginTokenConfigDTO {

    private Long id;
    private Long orgId;
    private Long exchangeId;
    private String tokenId;
    private String convertRate;
    private Long leverage;
    private Boolean canBorrow;
    private String maxQuantity;
    private String minQuantity;
    private Long quantityPrecision;
    private String repayMinQuantity;
    private Long created;
    private Long updated;
    private int isOpen;
    private int showInterestPrecision;

}
